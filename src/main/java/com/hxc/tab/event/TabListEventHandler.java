package com.hxc.tab.event;

import com.hxc.tab.client.TabListRenderer;
import com.hxc.tab.TabListExtension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = "tablistextension", value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TabListEventHandler {
    private static final TabListRenderer renderer = new TabListRenderer();
    
    // 定义常量
    private static final String PLAYER_LIST_OVERLAY = "minecraft:player_list";
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        try {
            if (PLAYER_LIST_OVERLAY.equals(event.getOverlay().id().toString())) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.options.keyPlayerList.isDown()) {
                    event.setCanceled(true);
                    List<PlayerInfo> playerInfoList = getPlayerInfoList();
                    renderer.renderCustomPlayerList(event.getGuiGraphics(),
                        minecraft.getWindow().getGuiScaledWidth(),
                        minecraft.getWindow().getGuiScaledHeight(),
                        playerInfoList);
                }
            }
        } catch (Exception e) {
            TabListExtension.LOGGER.error("[TabListEventHandler] 渲染Tab列表事件异常", e);
        }
    }
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            
            // 只有在Tab键按下且显示Tab列表时才处理分页键
            if (minecraft.options.keyPlayerList.isDown() && TabListRenderer.getTotalPages() > 1) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    if (event.getKey() == GLFW.GLFW_KEY_LEFT) {
                        // 左箭头键 - 上一页
                        TabListRenderer.previousPage();
                    } else if (event.getKey() == GLFW.GLFW_KEY_RIGHT) {
                        // 右箭头键 - 下一页
                        TabListRenderer.nextPage();
                    }
                }
            }
        } catch (Exception e) {
            TabListExtension.LOGGER.error("[TabListEventHandler] 处理键盘输入事件异常", e);
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