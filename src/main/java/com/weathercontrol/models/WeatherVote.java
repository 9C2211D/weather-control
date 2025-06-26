package com.weathercontrol.models;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeatherVote {
    
    private final Player initiator;
    private final String weatherType;
    private final long startTime;
    private final Map<UUID, Boolean> votes;
    
    public WeatherVote(Player initiator, String weatherType) {
        this.initiator = initiator;
        this.weatherType = weatherType;
        this.startTime = System.currentTimeMillis();
        this.votes = new HashMap<>();
    }
    
    /**
     * 添加投票
     * @param playerId 玩家UUID
     * @param vote true为赞成，false为反对
     */
    public void addVote(UUID playerId, boolean vote) {
        votes.put(playerId, vote);
    }
    
    /**
     * 检查玩家是否已投票
     * @param playerId 玩家UUID
     * @return 是否已投票
     */
    public boolean hasVoted(UUID playerId) {
        return votes.containsKey(playerId);
    }
    
    /**
     * 获取玩家的投票
     * @param playerId 玩家UUID
     * @return 投票结果，null表示未投票
     */
    public Boolean getVote(UUID playerId) {
        return votes.get(playerId);
    }
    
    /**
     * 获取赞成票数
     * @return 赞成票数
     */
    public int getYesVotes() {
        return (int) votes.values().stream().filter(vote -> vote).count();
    }
    
    /**
     * 获取反对票数
     * @return 反对票数
     */
    public int getNoVotes() {
        return (int) votes.values().stream().filter(vote -> !vote).count();
    }
    
    /**
     * 获取总投票数
     * @return 总投票数
     */
    public int getTotalVotes() {
        return votes.size();
    }
    
    /**
     * 获取投票发起者
     * @return 发起者
     */
    public Player getInitiator() {
        return initiator;
    }
    
    /**
     * 获取目标天气类型
     * @return 天气类型
     */
    public String getWeatherType() {
        return weatherType;
    }
    
    /**
     * 获取投票开始时间
     * @return 开始时间（毫秒时间戳）
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * 获取投票持续时间（毫秒）
     * @return 持续时间
     */
    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 获取所有投票记录
     * @return 投票记录映射
     */
    public Map<UUID, Boolean> getAllVotes() {
        return new HashMap<>(votes);
    }
    
    /**
     * 清除所有投票
     */
    public void clearVotes() {
        votes.clear();
    }
    
    @Override
    public String toString() {
        return "WeatherVote{" +
                "initiator=" + initiator.getName() +
                ", weatherType='" + weatherType + '\'' +
                ", yesVotes=" + getYesVotes() +
                ", noVotes=" + getNoVotes() +
                ", totalVotes=" + getTotalVotes() +
                ", duration=" + getDuration() + "ms" +
                '}';
    }
}