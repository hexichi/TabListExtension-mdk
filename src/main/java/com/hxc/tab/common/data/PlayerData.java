package com.hxc.tab.common.data;

import net.minecraft.network.chat.Component;

/**
 * 玩家数据类，存储玩家的维度信息和延迟
 */
public class PlayerData {
    private final String name;
    private final String dimension;
    private final int latency;
    
    public PlayerData(String name, String dimension, int latency) {
        this.name = name;
        this.dimension = dimension;
        this.latency = latency;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDimension() {
        return dimension;
    }
    
    public int getLatency() {
        return latency;
    }
    
    public Component getDisplayName() {
        return Component.literal(name);
    }
}