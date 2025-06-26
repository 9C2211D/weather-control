package com.weathercontrol.commands;

import com.weathercontrol.WeatherControlPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherCommand implements CommandExecutor, TabCompleter {
    
    private final WeatherControlPlugin plugin;
    
    public WeatherCommand(WeatherControlPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "set":
                return handleSetCommand(sender, args);
            case "vote":
                return handleVoteCommand(sender, args);
            case "vote-yes":
                return handleVoteYes(sender);
            case "vote-no":
                return handleVoteNo(sender);
            case "info":
                return handleInfoCommand(sender);
            case "reload":
                return handleReloadCommand(sender);
            default:
                sendHelpMessage(sender);
                return true;
        }
    }
    
    /**
     * 处理设置天气命令
     */
    private boolean handleSetCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("weather.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /weather set <clear|rain|thunder|snow>");
            return true;
        }
        
        String weatherType = args[1].toLowerCase();
        
        if (!plugin.getWeatherManager().isValidWeatherType(weatherType)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_weather"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getWeatherManager().setWeatherForPlayer(player, weatherType)) {
            String weatherDisplayName = plugin.getConfigManager().getWeatherDisplayName(weatherType);
            sender.sendMessage(plugin.getConfigManager().getMessage("weather_set", "weather", weatherDisplayName));
        } else {
            sender.sendMessage(ChatColor.RED + "设置天气失败！");
        }
        
        return true;
    }
    
    /**
     * 处理投票命令
     */
    private boolean handleVoteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("weather.vote")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /weather vote <clear|rain|thunder|snow>");
            return true;
        }
        
        String weatherType = args[1].toLowerCase();
        
        if (!plugin.getWeatherManager().isValidWeatherType(weatherType)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_weather"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getVoteManager().startVote(player, weatherType)) {
            // 投票成功开始，消息已在VoteManager中处理
        } else {
            // 投票开始失败，错误消息已在VoteManager中发送
        }
        
        return true;
    }
    
    /**
     * 处理投赞成票
     */
    private boolean handleVoteYes(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getVoteManager().hasActiveVote()) {
            player.sendMessage(ChatColor.RED + "当前没有进行中的投票！");
            return true;
        }
        
        plugin.getVoteManager().vote(player, true);
        return true;
    }
    
    /**
     * 处理投反对票
     */
    private boolean handleVoteNo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getVoteManager().hasActiveVote()) {
            player.sendMessage(ChatColor.RED + "当前没有进行中的投票！");
            return true;
        }
        
        plugin.getVoteManager().vote(player, false);
        return true;
    }
    
    /**
     * 处理信息查看命令
     */
    private boolean handleInfoCommand(CommandSender sender) {
        if (!sender.hasPermission("weather.info")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        
        Player player = (Player) sender;
        String currentWeather = plugin.getWeatherManager().getCurrentWeather(player.getWorld());
        String weatherDisplayName = plugin.getConfigManager().getWeatherDisplayName(currentWeather);
        int duration = plugin.getWeatherManager().getWeatherDuration(player.getWorld()) / 20; // 转换为秒
        
        sender.sendMessage(plugin.getConfigManager().getMessage("weather_info", 
            "weather", weatherDisplayName,
            "duration", String.valueOf(duration)));
        
        // 如果有进行中的投票，显示投票信息
        if (plugin.getVoteManager().hasActiveVote()) {
            var vote = plugin.getVoteManager().getCurrentVote();
            String voteWeatherName = plugin.getConfigManager().getWeatherDisplayName(vote.getWeatherType());
            sender.sendMessage(ChatColor.YELLOW + "当前投票: 将天气改为 " + ChatColor.AQUA + voteWeatherName + 
                ChatColor.YELLOW + " (发起者: " + vote.getInitiator().getName() + ")");
            sender.sendMessage(ChatColor.YELLOW + "赞成: " + ChatColor.GREEN + vote.getYesVotes() + 
                ChatColor.YELLOW + ", 反对: " + ChatColor.RED + vote.getNoVotes());
        }
        
        return true;
    }
    
    /**
     * 处理重载配置命令
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("weather.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        plugin.reloadPluginConfig();
        sender.sendMessage(plugin.getConfigManager().getMessage("config_reloaded"));
        
        return true;
    }
    
    /**
     * 发送帮助消息
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 天气控制插件帮助 ===");
        
        if (sender.hasPermission("weather.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/weather set <type>" + ChatColor.WHITE + " - 直接设置天气");
        }
        
        if (sender.hasPermission("weather.vote")) {
            sender.sendMessage(ChatColor.YELLOW + "/weather vote <type>" + ChatColor.WHITE + " - 发起天气投票");
        }
        
        if (sender.hasPermission("weather.info")) {
            sender.sendMessage(ChatColor.YELLOW + "/weather info" + ChatColor.WHITE + " - 查看天气信息");
        }
        
        if (sender.hasPermission("weather.reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/weather reload" + ChatColor.WHITE + " - 重新加载配置");
        }
        
        sender.sendMessage(ChatColor.GRAY + "天气类型: clear(晴天), rain(雨天), thunder(雷暴), snow(雪天)");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 第一个参数：子命令
            List<String> subCommands = new ArrayList<>();
            
            if (sender.hasPermission("weather.admin")) {
                subCommands.add("set");
            }
            if (sender.hasPermission("weather.vote")) {
                subCommands.add("vote");
            }
            if (sender.hasPermission("weather.info")) {
                subCommands.add("info");
            }
            if (sender.hasPermission("weather.reload")) {
                subCommands.add("reload");
            }
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("vote"))) {
            // 第二个参数：天气类型
            List<String> weatherTypes = Arrays.asList("clear", "rain", "thunder", "snow");
            
            for (String weatherType : weatherTypes) {
                if (weatherType.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(weatherType);
                }
            }
        }
        
        return completions;
    }
}