package com.hxc.tab.common.network;

import com.hxc.tab.TabListExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TabListExtension.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextID() {
        return packetId++;
    }

    public static void init() {
        // 注册数据包
        INSTANCE.registerMessage(
                nextID(),
                PlayerDimensionPacket.class,
                PlayerDimensionPacket::encode,
                PlayerDimensionPacket::decode,
                PlayerDimensionPacket::handle
        );
    }
}