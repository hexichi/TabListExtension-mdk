package com.hxc.tab.client;

import com.hxc.tab.common.data.PlayerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端数据管理器，存储从服务端同步的玩家维度信息
 */
@OnlyIn(Dist.CLIENT)
public class ClientDataManager {
    private static final Map<String, List<PlayerData>> playersByDimension = new HashMap<>();
    
    /**
     * 更新玩家维度信息
     */
    public static void updatePlayerDimension(String playerName, String dimension, int latency) {
        // 创建玩家数据
        PlayerData playerData = new PlayerData(playerName, dimension, latency);
        
        // 从所有维度中移除该玩家
        for (List<PlayerData> players : playersByDimension.values()) {
            players.removeIf(p -> p.getName().equals(playerName));
        }
        
        // 添加到新维度
        playersByDimension.computeIfAbsent(dimension, k -> new ArrayList<>()).add(playerData);
    }
    
    /**
     * 获取按维度分组的玩家
     */
    public static Map<String, List<PlayerData>> getPlayersByDimension() {
        return playersByDimension;
    }
    
    /**
     * 移除玩家
     */
    public static void removePlayer(String playerName) {
        for (List<PlayerData> players : playersByDimension.values()) {
            players.removeIf(p -> p.getName().equals(playerName));
        }
    }
    
    /**
     * 清除所有数据
     */
    public static void clear() {
        playersByDimension.clear();
    }
}