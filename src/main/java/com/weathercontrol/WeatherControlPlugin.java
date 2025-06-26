package com.weathercontrol;

import com.weathercontrol.commands.WeatherCommand;
import com.weathercontrol.listeners.PlayerListener;
import com.weathercontrol.managers.VoteManager;
import com.weathercontrol.managers.WeatherManager;
import com.weathercontrol.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeatherControlPlugin extends JavaPlugin {
    
    private static WeatherControlPlugin instance;
    private ConfigManager configManager;
    private WeatherManager weatherManager;
    private VoteManager voteManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // 初始化管理器
        weatherManager = new WeatherManager(this);
        voteManager = new VoteManager(this);
        
        // 注册命令
        getCommand("weather").setExecutor(new WeatherCommand(this));
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getLogger().info("天气控制插件已启用！");
    }
    
    @Override
    public void onDisable() {
        // 取消所有正在进行的投票
        if (voteManager != null) {
            voteManager.cancelAllVotes();
        }
        
        getLogger().info("天气控制插件已禁用！");
    }
    
    public static WeatherControlPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public WeatherManager getWeatherManager() {
        return weatherManager;
    }
    
    public VoteManager getVoteManager() {
        return voteManager;
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        configManager.loadConfig();
    }
}