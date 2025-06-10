package com.hxc.tab.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderGuiOverlayEvent; // 添加这个导入

@Mod.EventBusSubscriber(modid = "tablistextension", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static final TabListRenderer renderer = new TabListRenderer();
    
    // 客户端专用的事件处理
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent event) {
        // 处理客户端渲染
    }
}