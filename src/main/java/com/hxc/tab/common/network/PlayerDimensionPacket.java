package com.hxc.tab.common.network;

import com.hxc.tab.client.ClientDataManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 玩家维度信息数据包
 */
public class PlayerDimensionPacket {
    private final String playerName;
    private final String dimension;
    private final int latency;
    
    public PlayerDimensionPacket(String playerName, String dimension, int latency) {
        this.playerName = playerName;
        this.dimension = dimension;
        this.latency = latency;
    }
    
    public static void encode(PlayerDimensionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.playerName);
        buffer.writeUtf(packet.dimension);
        buffer.writeInt(packet.latency);
    }
    
    public static PlayerDimensionPacket decode(FriendlyByteBuf buffer) {
        return new PlayerDimensionPacket(
            buffer.readUtf(),
            buffer.readUtf(),
            buffer.readInt()
        );
    }
    
    public static void handle(PlayerDimensionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 确保只在客户端处理
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDataManager.updatePlayerDimension(
                packet.playerName,
                packet.dimension,
                packet.latency
            ));
        });
        context.setPacketHandled(true);
    }
}