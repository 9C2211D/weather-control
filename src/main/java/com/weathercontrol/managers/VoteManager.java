package com.weathercontrol.managers;

import com.weathercontrol.WeatherControlPlugin;
import com.weathercontrol.models.WeatherVote;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteManager {
    
    private final WeatherControlPlugin plugin;
    private WeatherVote currentVote;
    private BukkitTask voteTask;
    private Map<UUID, Long> voteCooldowns;
    
    public VoteManager(WeatherControlPlugin plugin) {
        this.plugin = plugin;
        this.voteCooldowns = new HashMap<>();
    }
    
    /**
     * 发起天气投票
     * @param initiator 发起者
     * @param weatherType 目标天气类型
     * @return 是否成功发起投票
     */
    public boolean startVote(Player initiator, String weatherType) {
        // 检查是否已有投票进行中
        if (currentVote != null) {
            initiator.sendMessage(plugin.getConfigManager().getMessage("vote_already_active"));
            return false;
        }
        
        // 检查冷却时间
        if (isOnCooldown(initiator)) {
            long remainingTime = getRemainingCooldown(initiator);
            initiator.sendMessage(plugin.getConfigManager().getMessage("vote_cooldown", "time", String.valueOf(remainingTime)));
            return false;
        }
        
        // 检查在线玩家数量
        int onlineCount = Bukkit.getOnlinePlayers().size();
        if (onlineCount < plugin.getConfigManager().getMinVoters()) {
            initiator.sendMessage(plugin.getConfigManager().getMessage("vote_timeout", "min_voters", String.valueOf(plugin.getConfigManager().getMinVoters())));
            return false;
        }
        
        // 创建新投票
        currentVote = new WeatherVote(initiator, weatherType);
        
        // 如果配置允许，发起者自动投赞成票
        if (plugin.getConfigManager().isAutoVoteInitiator()) {
            currentVote.addVote(initiator.getUniqueId(), true);
        }
        
        // 设置冷却时间
        voteCooldowns.put(initiator.getUniqueId(), System.currentTimeMillis());
        
        // 广播投票开始消息
        broadcastVoteStart();
        
        // 启动投票计时器
        startVoteTimer();
        
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("投票已开始: " + initiator.getName() + " -> " + weatherType);
        }
        
        return true;
    }
    
    /**
     * 玩家投票
     * @param player 投票玩家
     * @param vote true为赞成，false为反对
     * @return 是否投票成功
     */
    public boolean vote(Player player, boolean vote) {
        if (currentVote == null) {
            return false;
        }
        
        if (currentVote.hasVoted(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("already_voted"));
            return false;
        }
        
        currentVote.addVote(player.getUniqueId(), vote);
        
        String voteType = vote ? "赞成" : "反对";
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info(player.getName() + " 投了" + voteType + "票");
        }
        
        return true;
    }
    
    /**
     * 广播投票开始消息
     */
    private void broadcastVoteStart() {
        String weatherDisplayName = plugin.getConfigManager().getWeatherDisplayName(currentVote.getWeatherType());
        String startMessage = plugin.getConfigManager().getMessage("vote_started", 
            "player", currentVote.getInitiator().getName(),
            "weather", weatherDisplayName);
        
        // 发送投票开始消息
        Bukkit.broadcastMessage(startMessage);
        
        // 创建可点击的投票按钮
        TextComponent message = new TextComponent(plugin.getConfigManager().getMessage("vote_instruction", "time", String.valueOf(plugin.getConfigManager().getVoteDuration())));
        
        TextComponent yesButton = new TextComponent(plugin.getConfigManager().getMessage("vote_yes"));
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/weather vote-yes"));
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(plugin.getConfigManager().getMessage("vote_yes_hover")).create()));
        
        TextComponent noButton = new TextComponent(" " + plugin.getConfigManager().getMessage("vote_no"));
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/weather vote-no"));
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(plugin.getConfigManager().getMessage("vote_no_hover")).create()));
        
        message.addExtra(" ");
        message.addExtra(yesButton);
        message.addExtra(noButton);
        
        // 发送给所有在线玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(message);
        }
    }
    
    /**
     * 启动投票计时器
     */
    private void startVoteTimer() {
        voteTask = new BukkitRunnable() {
            @Override
            public void run() {
                endVote();
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getVoteDuration() * 20L);
    }
    
    /**
     * 结束投票并公布结果
     */
    private void endVote() {
        if (currentVote == null) {
            return;
        }
        
        int yesVotes = currentVote.getYesVotes();
        int noVotes = currentVote.getNoVotes();
        int totalVotes = yesVotes + noVotes;
        
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("投票结束 - 赞成: " + yesVotes + ", 反对: " + noVotes + ", 总计: " + totalVotes);
        }
        
        // 检查是否达到最少投票人数
        if (totalVotes < plugin.getConfigManager().getMinVoters()) {
            String timeoutMessage = plugin.getConfigManager().getMessage("vote_timeout", "min_voters", String.valueOf(plugin.getConfigManager().getMinVoters()));
            Bukkit.broadcastMessage(timeoutMessage);
        } else {
            // 计算赞成比例
            double yesRatio = (double) yesVotes / totalVotes;
            double requiredRatio = plugin.getConfigManager().getSuccessRatio();
            
            if (yesRatio >= requiredRatio) {
                // 投票成功，改变天气
                plugin.getWeatherManager().setWeatherForPlayer(currentVote.getInitiator(), currentVote.getWeatherType());
                
                String weatherDisplayName = plugin.getConfigManager().getWeatherDisplayName(currentVote.getWeatherType());
                String successMessage = plugin.getConfigManager().getMessage("vote_success",
                    "weather", weatherDisplayName,
                    "yes", String.valueOf(yesVotes),
                    "no", String.valueOf(noVotes));
                Bukkit.broadcastMessage(successMessage);
            } else {
                // 投票失败
                String failedMessage = plugin.getConfigManager().getMessage("vote_failed",
                    "yes", String.valueOf(yesVotes),
                    "no", String.valueOf(noVotes),
                    "required", String.valueOf((int)(requiredRatio * 100)));
                Bukkit.broadcastMessage(failedMessage);
            }
        }
        
        // 清理投票数据
        currentVote = null;
        if (voteTask != null) {
            voteTask.cancel();
            voteTask = null;
        }
    }
    
    /**
     * 检查玩家是否在冷却时间内
     * @param player 玩家
     * @return 是否在冷却时间内
     */
    private boolean isOnCooldown(Player player) {
        if (!voteCooldowns.containsKey(player.getUniqueId())) {
            return false;
        }
        
        long lastVoteTime = voteCooldowns.get(player.getUniqueId());
        long cooldownMs = plugin.getConfigManager().getVoteCooldown() * 1000L;
        
        return System.currentTimeMillis() - lastVoteTime < cooldownMs;
    }
    
    /**
     * 获取剩余冷却时间（秒）
     * @param player 玩家
     * @return 剩余冷却时间
     */
    private long getRemainingCooldown(Player player) {
        if (!voteCooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long lastVoteTime = voteCooldowns.get(player.getUniqueId());
        long cooldownMs = plugin.getConfigManager().getVoteCooldown() * 1000L;
        long elapsed = System.currentTimeMillis() - lastVoteTime;
        
        return Math.max(0, (cooldownMs - elapsed) / 1000);
    }
    
    /**
     * 取消所有投票（插件禁用时调用）
     */
    public void cancelAllVotes() {
        if (voteTask != null) {
            voteTask.cancel();
            voteTask = null;
        }
        currentVote = null;
    }
    
    /**
     * 检查是否有投票进行中
     * @return 是否有投票进行中
     */
    public boolean hasActiveVote() {
        return currentVote != null;
    }
    
    /**
     * 获取当前投票
     * @return 当前投票对象
     */
    public WeatherVote getCurrentVote() {
        return currentVote;
    }
}