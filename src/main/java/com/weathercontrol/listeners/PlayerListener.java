package com.weathercontrol.listeners;

import com.weathercontrol.WeatherControlPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final WeatherControlPlugin plugin;
    
    public PlayerListener(WeatherControlPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 处理玩家离开事件
     * 如果投票发起者离开，可以选择取消投票或继续进行
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 检查是否有进行中的投票
        if (plugin.getVoteManager().hasActiveVote()) {
            var currentVote = plugin.getVoteManager().getCurrentVote();
            
            // 如果离开的是投票发起者，记录日志
            if (currentVote.getInitiator().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info("投票发起者 " + event.getPlayer().getName() + " 离开了服务器，投票继续进行");
                }
            }
        }
    }
}