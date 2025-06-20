package com.hxc.tab.common.data;

/**
 * 玩家数据类，存储玩家的维度信息和延迟
 */
public record PlayerData(String name, String dimension, int latency) {
}