package com.hxc.tab.client;

import com.hxc.tab.common.data.PlayerData;
import com.hxc.tab.config.TabListConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class TabListRenderer {
    private static final ResourceLocation PING_ICONS = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft minecraft = Minecraft.getInstance();
    
    /**
     * 渲染自定义TAB列表
     */
    public void renderCustomPlayerList(GuiGraphics guiGraphics, int width, int height, List<PlayerInfo> playerInfoList) {
        // 获取玩家维度数据
        Map<String, List<PlayerData>> playersByDimension = ClientDataManager.getPlayersByDimension();
        
        // 收集所有玩家（不限制维度）
        List<PlayerData> allPlayers = new ArrayList<>();
        
        // 如果有维度数据，使用维度数据
        if (!playersByDimension.isEmpty()) {
            for (List<PlayerData> dimensionPlayers : playersByDimension.values()) {
                allPlayers.addAll(dimensionPlayers);
            }
        } else {
            // 如果没有维度数据，使用原版玩家列表
            String currentDimension = getCurrentPlayerDimension();
            allPlayers = playerInfoList.stream()
                .map(playerInfo -> new PlayerData(
                    playerInfo.getProfile().getName(),
                    currentDimension,
                    playerInfo.getLatency()
                ))
                .collect(Collectors.toList());
        }
        
        // 按玩家名称排序
        allPlayers.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
        
        // 计算布局
        int headerHeight = 20;
        int entryHeight = 12;
        int maxEntries = Math.min(allPlayers.size(), 80);
        
        // 计算实际内容宽度
        int maxContentWidth = 0;
        Component title = Component.literal(TabListConfig.HEADER_TEXT.get());
        int titleWidth = minecraft.font.width(title);
        maxContentWidth = Math.max(maxContentWidth, titleWidth);
        
        // 计算最长的玩家名称宽度
        int maxPlayerNameWidth = 0;
        // 计算最长的维度名称宽度
        int maxDimensionWidth = 0;
        // 计算最长的ping值宽度
        int maxPingWidth = 0;
        
        // 计算每行玩家条目的最大宽度
        for (PlayerData player : allPlayers) {
            String playerName = player.getName();
            int playerNameWidth = minecraft.font.width(playerName);
            maxPlayerNameWidth = Math.max(maxPlayerNameWidth, playerNameWidth);
            
            String dimension = player.getDimension();
            String dimensionName = getLocalizedDimensionName(dimension);
            String dimensionText = "[" + dimensionName + "]";
            int dimensionWidth = minecraft.font.width(dimensionText);
            maxDimensionWidth = Math.max(maxDimensionWidth, dimensionWidth);
            
            if (TabListConfig.SHOW_PING.get()) {
                int latency = player.getLatency();
                String pingText = latency < 0 ? 
                    Component.translatable("tablist.tablistextension.ping_unknown").getString() : 
                    latency + Component.translatable("tablist.tablistextension.ping_ms").getString();
                int pingTextWidth = minecraft.font.width(pingText);
                maxPingWidth = Math.max(maxPingWidth, pingTextWidth);
            }
        }
        
        // 计算总内容宽度 (玩家名称 + 间隔 + 维度名称 + 间隔 + ping值)
        int spacing = 2; // 各元素之间的间隔
        int totalContentWidth = maxPlayerNameWidth + spacing + maxDimensionWidth;
        if (TabListConfig.SHOW_PING.get()) {
            totalContentWidth += spacing + maxPingWidth;
        }
        
        maxContentWidth = Math.max(maxContentWidth, totalContentWidth);
        
        // 背景比内容大2个像素
        int backgroundPadding = 2;
        int listWidth = maxContentWidth + (backgroundPadding * 2);
        int listHeight = headerHeight + (entryHeight * maxEntries);
        
        // 位置：顶部居中
        int startX = (width - listWidth) / 2;
        int startY = 6;
        
        // 绘制半透明背景
        guiGraphics.fill(startX, startY, startX + listWidth, startY + listHeight, 0x50000000);
        
        // 绘制标题
        guiGraphics.drawCenteredString(minecraft.font, title, startX + listWidth / 2, startY + 5, 0xFFFFFF);
        
        // 渲染玩家列表
        int yPos = startY + headerHeight;
        for (int i = 0; i < maxEntries && i < allPlayers.size(); i++) {
            PlayerData player = allPlayers.get(i);
            renderPlayerEntry(guiGraphics, player, startX, yPos, listWidth, maxPlayerNameWidth, maxDimensionWidth, maxPingWidth, spacing);
            yPos += entryHeight;
        }
    }
    
    /**
     * 获取当前玩家所在维度
     */
    private String getCurrentPlayerDimension() {
        if (minecraft.level == null) {
            return "overworld";
        }
        
        String dimensionKey = minecraft.level.dimension().location().getPath();
        return dimensionKey;
    }
    
    /**
     * 渲染单个玩家条目
     */
    private void renderPlayerEntry(GuiGraphics guiGraphics, PlayerData player, int x, int y, int width, 
                                  int maxPlayerNameWidth, int maxDimensionWidth, int maxPingWidth, int spacing) {
        // 获取维度信息
        String dimension = player.getDimension();
        String dimensionName = getLocalizedDimensionName(dimension);
        int dimensionColor = getDimensionColor(dimension);
        
        // 获取玩家名称字符串
        String playerName = player.getName();
        
        // 定义背景内边距
        int backgroundPadding = 2;
        
        // 计算内容起始位置 (居中整个内容区域)
        int contentStartX = x + backgroundPadding;
        int contentWidth = width - (backgroundPadding * 2);
        
        // 计算维度名称的起始位置（右对齐）
        String dimensionText = "[" + dimensionName + "]";
        int dimensionWidth = minecraft.font.width(dimensionText);
        int dimensionX = contentStartX + (maxDimensionWidth - dimensionWidth);
        
        // 计算玩家名称的起始位置 (在维度名称之后)
        int playerNameX = contentStartX + maxDimensionWidth + spacing;
        
        // 绘制维度名称 (带颜色)
        guiGraphics.drawString(minecraft.font, dimensionText, dimensionX, y, dimensionColor);
        
        // 绘制玩家名称 (白色)
        guiGraphics.drawString(minecraft.font, playerName, playerNameX, y, 0xFFFFFF);
        
        // 根据配置决定是否显示ping
        if (TabListConfig.SHOW_PING.get()) {
            // 计算ping值的起始位置（左对齐）
            int pingX = contentStartX + maxDimensionWidth + spacing + maxPlayerNameWidth + spacing;
            
            // 获取ping文本和颜色
            Object[] pingInfo = getPingTextAndColor(player.getLatency());
            String pingText = (String) pingInfo[0];
            int pingColor = (int) pingInfo[1];
            
            // 绘制ping值（左对齐）
            guiGraphics.drawString(minecraft.font, pingText, pingX, y, pingColor);
        }
    }
    
    /**
     * 获取ping文本和颜色
     * @param latency 延迟值
     * @return 包含ping文本和颜色的数组，索引0为文本，索引1为颜色值
     */
    private Object[] getPingTextAndColor(int latency) {
        String pingText;
        int pingColor;
        
        if (latency < 0) {
            pingText = Component.translatable("tablist.tablistextension.ping_unknown").getString(); // 未知延迟
            pingColor = 0x888888;
        } else {
            pingText = latency + Component.translatable("tablist.tablistextension.ping_ms").getString();
            // 根据延迟设置颜色
            if (latency < 150) {
                pingColor = 0x00FF00; // 绿色 - 良好
            } else if (latency < 300) {
                pingColor = 0xFFFF00; // 黄色 - 一般
            } else if (latency < 600) {
                pingColor = 0xFF8800; // 橙色 - 较差
            } else {
                pingColor = 0xFF0000; // 红色 - 很差
            }
        }
        
        return new Object[]{pingText, pingColor};
    }

    /**
     * 渲染ping数值
     */
    private void renderPingValue(GuiGraphics guiGraphics, int latency, int x, int y) {
        String pingText;
        int pingColor;
        
        if (latency < 0) {
            pingText = Component.translatable("tablist.tablistextension.ping_unknown").getString(); // 未知延迟
            pingColor = 0x888888;
        } else {
            pingText = latency + Component.translatable("tablist.tablistextension.ping_ms").getString();
            // 根据延迟设置颜色
            if (latency < 150) {
                pingColor = 0x00FF00; // 绿色 - 良好
            } else if (latency < 300) {
                pingColor = 0xFFFF00; // 黄色 - 一般
            } else if (latency < 600) {
                pingColor = 0xFF8800; // 橙色 - 较差
            } else {
                pingColor = 0xFF0000; // 红色 - 很差
            }
        }
        
        // 绘制ping文本
        guiGraphics.drawString(minecraft.font, pingText, x, y, pingColor);
    }
    
    /**
     * 渲染ping图标
     */
    private void renderPingIcon(GuiGraphics guiGraphics, int latency, int x, int y) {
        int pingIndex;
        
        if (latency < 0) {
            pingIndex = 5;
        } else if (latency < 150) {
            pingIndex = 0;
        } else if (latency < 300) {
            pingIndex = 1;
        } else if (latency < 600) {
            pingIndex = 2;
        } else if (latency < 1000) {
            pingIndex = 3;
        } else {
            pingIndex = 4;
        }
        
        // 绘制ping图标
        guiGraphics.blit(PING_ICONS, x, y, 0, 176 + pingIndex * 8, 10, 8);
    }
    
    /**
     * 获取维度颜色
     */
    private int getDimensionColor(String dimension) {
        // 使用配置文件中的颜色设置
        TabListConfig.DimensionConfig config = TabListConfig.DIMENSION_CONFIGS.get(dimension);
        if (config != null && config.enabled.get()) {
            return config.getColor();
        }
        
        // 原有的默认逻辑作为后备
        switch (dimension) {
            case "overworld": return 0x55FF55;
            case "the_nether": return 0xFF5555;
            case "the_end": return 0xAA00AA;
            default: return 0xFFFFFF;
        }
    }
    
    /**
     * 获取本地化的维度名称
     */
    private String getLocalizedDimensionName(String dimension) {
        String key;
        switch (dimension) {
            case "overworld":
                key = "dimension.tablistextension.overworld";
                break;
            case "the_nether":
                key = "dimension.tablistextension.nether";
                break;
            case "the_end":
                key = "dimension.tablistextension.end";
                break;
            default:
                key = "dimension.tablistextension.other";
                break;
        }
        return Component.translatable(key).getString().replace("[", "").replace("] ", "");
    }
}