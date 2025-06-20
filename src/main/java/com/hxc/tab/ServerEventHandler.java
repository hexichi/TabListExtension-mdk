package com.hxc.tab;

import com.hxc.tab.common.network.PacketHandler;
import com.hxc.tab.common.network.PlayerDimensionPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 服务器事件处理器，负责同步玩家维度信息到客户端
 */
public class ServerEventHandler {
    private int tickCounter = 0;
    private static final int SYNC_INTERVAL = 10;
    
    // 添加数据变化追踪
    private final Map<String, PlayerDimensionData> lastPlayerData = new HashMap<>();

    private record PlayerDimensionData(String dimension, int latency) {

        @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof PlayerDimensionData other)) return false;
                return Objects.equals(dimension, other.dimension) && latency == other.latency;
            }

    }
    
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter < SYNC_INTERVAL) return;
        tickCounter = 0;
        
        net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                syncPlayerDimensionIfChanged(player);
            }
            
            // 清理已离线玩家的数据
            Set<String> onlinePlayerNames = server.getPlayerList().getPlayers()
                .stream().map(p -> p.getGameProfile().getName()).collect(Collectors.toSet());
            lastPlayerData.keySet().removeIf(name -> !onlinePlayerNames.contains(name));
        }
    }
    
    /**
     * 只在数据变化时同步玩家维度信息
     */
    private void syncPlayerDimensionIfChanged(ServerPlayer player) {
        String playerName = player.getGameProfile().getName();
        String dimension = player.level().dimension().location().getPath();
        int latency = player.latency;
        
        PlayerDimensionData newData = new PlayerDimensionData(dimension, latency);
        PlayerDimensionData oldData = lastPlayerData.get(playerName);
        
        // 只在数据变化时发送数据包
        if (!newData.equals(oldData)) {
            PlayerDimensionPacket packet = new PlayerDimensionPacket(playerName, dimension, latency);
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
            lastPlayerData.put(playerName, newData);
        }
    }
}