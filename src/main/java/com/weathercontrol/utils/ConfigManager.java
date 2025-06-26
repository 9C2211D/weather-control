package com.weathercontrol.utils;

import com.weathercontrol.WeatherControlPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final WeatherControlPlugin plugin;
    private FileConfiguration config;
    
    // 投票配置
    private int voteDuration;
    private int minVoters;
    private double successRatio;
    private int voteCooldown;
    private boolean autoVoteInitiator;
    
    // 天气类型显示名称
    private Map<String, String> weatherDisplayNames;
    private Map<String, String> weatherDescriptions;
    
    // 消息配置
    private Map<String, String> messages;
    
    // 调试模式
    private boolean debug;
    
    public ConfigManager(WeatherControlPlugin plugin) {
        this.plugin = plugin;
        this.weatherDisplayNames = new HashMap<>();
        this.weatherDescriptions = new HashMap<>();
        this.messages = new HashMap<>();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // 加载投票配置
        voteDuration = config.getInt("voting.duration", 30);
        minVoters = config.getInt("voting.min_voters", 2);
        successRatio = config.getDouble("voting.success_ratio", 0.5);
        voteCooldown = config.getInt("voting.cooldown", 60);
        autoVoteInitiator = config.getBoolean("voting.auto_vote_initiator", true);
        
        // 加载天气类型配置
        loadWeatherTypes();
        
        // 加载消息配置
        loadMessages();
        
        // 加载调试配置
        debug = config.getBoolean("debug", false);
        
        if (debug) {
            plugin.getLogger().info("配置已加载 - 投票时长: " + voteDuration + "s, 最少投票人数: " + minVoters + ", 成功比例: " + (successRatio * 100) + "%");
        }
    }
    
    private void loadWeatherTypes() {
        weatherDisplayNames.clear();
        weatherDescriptions.clear();
        
        for (String weatherType : new String[]{"clear", "rain", "thunder", "snow"}) {
            String displayName = config.getString("weather_types." + weatherType + ".display_name", weatherType);
            String description = config.getString("weather_types." + weatherType + ".description", "");
            
            weatherDisplayNames.put(weatherType, displayName);
            weatherDescriptions.put(weatherType, description);
        }
    }
    
    private void loadMessages() {
        messages.clear();
        
        if (config.getConfigurationSection("messages") != null) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                String message = config.getString("messages." + key, "");
                messages.put(key, ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "&c消息未找到: " + key);
    }
    
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        
        // 替换占位符
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        
        return message;
    }
    
    public String getWeatherDisplayName(String weatherType) {
        return weatherDisplayNames.getOrDefault(weatherType, weatherType);
    }
    
    public String getWeatherDescription(String weatherType) {
        return weatherDescriptions.getOrDefault(weatherType, "");
    }
    
    // Getters
    public int getVoteDuration() {
        return voteDuration;
    }
    
    public int getMinVoters() {
        return minVoters;
    }
    
    public double getSuccessRatio() {
        return successRatio;
    }
    
    public int getVoteCooldown() {
        return voteCooldown;
    }
    
    public boolean isAutoVoteInitiator() {
        return autoVoteInitiator;
    }
    
    public boolean isDebug() {
        return debug;
    }
}