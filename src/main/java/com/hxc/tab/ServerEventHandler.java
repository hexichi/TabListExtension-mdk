package com.hxc.tab;

import com.hxc.tab.common.network.PacketHandler;
import com.hxc.tab.common.network.PlayerDimensionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

/**
 * 服务器事件处理器，负责同步玩家维度信息到客户端
 */
public class ServerEventHandler {
    // 用于控制数据同步频率的计数器
    private int tickCounter = 0;
    private static final int SYNC_INTERVAL = 10; // 以tick为单位，20tick = 1秒

    /**
     * 服务器tick事件，用于定期同步玩家维度信息
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        // 控制同步频率
        tickCounter++;
        if (tickCounter < SYNC_INTERVAL) return;
        tickCounter = 0;
        
        // 获取服务器并同步所有玩家信息
        net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                syncPlayerDimension(player);
            }
        }
    }
    
    /**
     * 同步玩家维度信息到所有客户端
     */
    private void syncPlayerDimension(ServerPlayer player) {
        String dimension = player.level().dimension().location().getPath();
        
        // 使用正确的方法获取延迟
        int latency = player.latency;
        
        // 创建数据包并发送给所有玩家
        PlayerDimensionPacket packet = new PlayerDimensionPacket(
            player.getGameProfile().getName(),
            dimension,
            latency
        );
        
        PacketHandler.INSTANCE.send(
            PacketDistributor.ALL.noArg(),
            packet
        );
    }
}