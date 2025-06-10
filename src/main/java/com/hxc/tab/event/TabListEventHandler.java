package com.hxc.tab.event;

import com.hxc.tab.client.TabListRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = "tablistextension", value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TabListEventHandler {
    private static final TabListRenderer renderer = new TabListRenderer();
    
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        // 检查是否是玩家列表覆盖层
        if (event.getOverlay().id().toString().contains("player_list") || 
            event.getOverlay().id().toString().contains("tab_list")) {
            
            // 检查Tab键是否被按下
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.keyPlayerList.isDown()) {
                event.setCanceled(true);
                
                // 获取玩家列表
                List<PlayerInfo> playerInfoList = getPlayerInfoList();
                
                // 使用自定义渲染器，传入正确的屏幕尺寸
                renderer.renderCustomPlayerList(event.getGuiGraphics(), 
                    minecraft.getWindow().getGuiScaledWidth(), 
                    minecraft.getWindow().getGuiScaledHeight(), 
                    playerInfoList);
            }
        }
    }
    
    private static List<PlayerInfo> getPlayerInfoList() {
        Minecraft minecraft = Minecraft.getInstance();
        
        if (minecraft == null) {
            return Collections.emptyList();
        }
        
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null) {
            return Collections.emptyList();
        }
        
        return connection.getOnlinePlayers().stream().toList();
    }
}