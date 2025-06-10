package com.hxc.tab;

import com.hxc.tab.common.network.PacketHandler;
import com.hxc.tab.config.TabListConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TabListExtension.MOD_ID)
public class TabListExtension {
    public static final String MOD_ID = "tablistextension";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public TabListExtension() {
        LOGGER.info("Initializing TabListExtension mod");
        
        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置
        try {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TabListConfig.SPEC);
        } catch (Exception e) {
            LOGGER.warn("Failed to register config: " + e.getMessage());
        }
        
        // 注册事件处理器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // 注册服务端事件处理器（通用）
        try {
            MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        } catch (Exception e) {
            LOGGER.warn("Failed to register server event handler: " + e.getMessage());
        }
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup phase");
        // 初始化网络处理
        try {
            PacketHandler.init();
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize packet handler: " + e.getMessage());
        }
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup phase");
        // 注册客户端事件处理器
        try {
            // 只在客户端注册客户端事件
            if (FMLEnvironment.dist == Dist.CLIENT) {
                // 注意：不要在这里注册，因为 TabListEventHandler 已经使用了 @Mod.EventBusSubscriber
                // 客户端事件处理器会自动注册
                LOGGER.info("Client event handlers will be auto-registered");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to register client event handler: " + e.getMessage());
        }
    }
}