package com.hxc.tab;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TabListExtension.MOD_ID) // 使用Mod注解标记这是一个Forge模组
public class TabListExtension {
    // 定义模组ID常量
    public static final String MOD_ID = "tablistextension";
    // 创建日志记录器
    private static final Logger LOGGER = LogManager.getLogger();

    public TabListExtension() {
        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册到Forge事件总线，用于监听游戏事件
        MinecraftForge.EVENT_BUS.register(this);
        
        // 初始化事件处理器
        new TabListEventHandler();
        
        // 输出模组初始化日志
        LOGGER.info("TAB List Extension mod initialized!");
    }
}