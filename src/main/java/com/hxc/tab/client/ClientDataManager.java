package com.hxc.tab.client;

import com.hxc.tab.common.data.PlayerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端数据管理器，存储从服务端同步的玩家维度信息
 */
@OnlyIn(Dist.CLIENT)
public class ClientDataManager {
    private static final Map<String, List<PlayerData>> playersByDimension = new ConcurrentHashMap<>();
    // 添加玩家到维度的映射，提高查找效率
    private static final Map<String, String> playerToDimension = new ConcurrentHashMap<>();

    /**
     * 更新玩家维度信息 - 优化版本
     */
    public static void updatePlayerDimension(String playerName, String dimension, int latency) {
        // 创建玩家数据
        PlayerData playerData = new PlayerData(playerName, dimension, latency);
        
        String oldDimension = playerToDimension.get(playerName);
        if (oldDimension != null) {
            List<PlayerData> oldDimensionPlayers = playersByDimension.get(oldDimension);
            if (oldDimensionPlayers != null) {
                oldDimensionPlayers.removeIf(p -> p.name().equals(playerName));
                // 如果维度列表为空，移除该维度
                if (oldDimensionPlayers.isEmpty()) {
                    playersByDimension.remove(oldDimension);
                }
            }
        }
        
        // 添加到新维度
        playersByDimension.computeIfAbsent(dimension, k -> new ArrayList<>()).add(playerData);
        playerToDimension.put(playerName, dimension);
    }

    /**
     * 清除所有数据 - 优化版本
     */
    public static void clear() {
        playersByDimension.clear();
        playerToDimension.clear();
    }
    
    /**
     * 获取按维度分组的玩家
     */
    public static Map<String, List<PlayerData>> getPlayersByDimension() {
        return playersByDimension;
    }
}