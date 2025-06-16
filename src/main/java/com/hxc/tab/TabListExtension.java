package com.hxc.tab;

import com.hxc.tab.common.network.PacketHandler;
import com.hxc.tab.config.TabListConfig;
import com.hxc.tab.client.ClientEventHandler;
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
    private static String logPrefix() {
        return "[" + MOD_ID + "] ";
    }

    public TabListExtension() {
        LOGGER.info(logPrefix() + "Initializing TabListExtension mod");

        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置
        try {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TabListConfig.SPEC);
            MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        } catch (Exception e) {
            LOGGER.warn(logPrefix() + "Failed to register config or server event handler: " + e.getMessage());
        }
        
        // 注册事件处理器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info(logPrefix() + "Common setup phase");
        // 初始化网络处理
        try {
            PacketHandler.init();
        } catch (Exception e) {
            LOGGER.warn(logPrefix() + "Failed to initialize packet handler: " + e.getMessage());
        }
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        // 客户端事件处理器已通过@Mod.EventBusSubscriber自动注册
        LOGGER.info(logPrefix() + "Client setup completed");
    }
}