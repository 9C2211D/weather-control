package com.weathercontrol.managers;

import com.weathercontrol.WeatherControlPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WeatherManager {
    
    private final WeatherControlPlugin plugin;
    
    public WeatherManager(WeatherControlPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 设置世界天气
     * @param world 目标世界
     * @param weatherType 天气类型 (clear, rain, thunder, snow)
     * @return 是否设置成功
     */
    public boolean setWeather(World world, String weatherType) {
        if (world == null) {
            return false;
        }
        
        try {
            switch (weatherType.toLowerCase()) {
                case "clear":
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setClearWeatherDuration(6000); // 5分钟
                    break;
                    
                case "rain":
                    world.setStorm(true);
                    world.setThundering(false);
                    world.setWeatherDuration(6000); // 5分钟
                    break;
                    
                case "thunder":
                    world.setStorm(true);
                    world.setThundering(true);
                    world.setThunderDuration(6000); // 5分钟
                    world.setWeatherDuration(6000);
                    break;
                    
                case "snow":
                    // 雪天实际上是雨天，但在寒冷生物群系中会显示为雪
                    world.setStorm(true);
                    world.setThundering(false);
                    world.setWeatherDuration(6000); // 5分钟
                    break;
                    
                default:
                    return false;
            }
            
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("天气已设置为: " + weatherType + " 在世界: " + world.getName());
            }
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("设置天气时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 为玩家所在世界设置天气
     * @param player 玩家
     * @param weatherType 天气类型
     * @return 是否设置成功
     */
    public boolean setWeatherForPlayer(Player player, String weatherType) {
        return setWeather(player.getWorld(), weatherType);
    }
    
    /**
     * 获取当前天气类型
     * @param world 世界
     * @return 天气类型字符串
     */
    public String getCurrentWeather(World world) {
        if (world.hasStorm()) {
            if (world.isThundering()) {
                return "thunder";
            } else {
                // 检查是否在寒冷生物群系（简化判断）
                return "rain"; // 这里可以根据需要进一步判断是否为雪天
            }
        } else {
            return "clear";
        }
    }
    
    /**
     * 获取天气剩余持续时间（tick）
     * @param world 世界
     * @return 剩余时间（tick）
     */
    public int getWeatherDuration(World world) {
        if (world.hasStorm()) {
            return world.getWeatherDuration();
        } else {
            return world.getClearWeatherDuration();
        }
    }
    
    /**
     * 验证天气类型是否有效
     * @param weatherType 天气类型
     * @return 是否有效
     */
    public boolean isValidWeatherType(String weatherType) {
        if (weatherType == null) {
            return false;
        }
        
        String type = weatherType.toLowerCase();
        return type.equals("clear") || type.equals("rain") || 
               type.equals("thunder") || type.equals("snow");
    }
    
    /**
     * 获取所有可用的天气类型
     * @return 天气类型数组
     */
    public String[] getAvailableWeatherTypes() {
        return new String[]{"clear", "rain", "thunder", "snow"};
    }
}