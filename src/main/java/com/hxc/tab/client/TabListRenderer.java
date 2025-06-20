package com.hxc.tab.client;

import com.hxc.tab.TabListExtension;
import com.hxc.tab.common.data.PlayerData;
import com.hxc.tab.config.TabListConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
// 定义一个仅在客户端使用的 Tab 列表渲染器类
public class TabListRenderer {
    // 获取 Minecraft 实例，用于后续操作
    private final Minecraft minecraft = Minecraft.getInstance();
    // 定义字体缩放比例，用于调整文本显示大小
    private static final float FONT_SCALE = 0.8f;
    
    // 分页相关变量
    // 当前显示的页码，从 0 开始计数
    private static int currentPage = 0;
    // 总页数，初始值为 1
    private static int totalPages = 1;
    // 每页最大显示的玩家数量
    private static final int maxPlayersPerPage = 80;

    // 缓存相关变量
    /**
     * 布局缓存类，用于存储和验证布局信息，避免重复计算
     */
    private static class LayoutCache {
        // 玩家数量
        private final int playerCount;
        // 屏幕宽度
        private final int screenWidth;
        // 屏幕高度
        private final int screenHeight;
        // GUI 缩放值
        private final double guiScale;
        // 是否显示 ping 值
        private final boolean showPing;
        // 标题文本
        private final String headerText;
        
        // 缓存的布局信息
        // 最大玩家名称宽度
        private final int maxPlayerNameWidth;
        // 最大维度名称宽度
        private final int maxDimensionWidth;
        // 最大 ping 值宽度
        private final int maxPingWidth;
        // 列宽度
        private final int columnWidth;
        // 实际列数
        private final int actualColumns;
        // 每列最大显示条目数
        private final int maxEntriesPerColumn;
        // 总宽度
        private final int totalWidth;
        // 总高度
        private final int totalHeight;
        // 缓存创建时间
        private final long cacheTime;
        
        /**
         * 构造函数，初始化布局缓存信息
         * @param playerCount 玩家数量
         * @param screenWidth 屏幕宽度
         * @param screenHeight 屏幕高度
         * @param guiScale GUI 缩放值
         * @param showPing 是否显示 ping 值
         * @param headerText 标题文本
         * @param maxPlayerNameWidth 最大玩家名称宽度
         * @param maxDimensionWidth 最大维度名称宽度
         * @param maxPingWidth 最大 ping 值宽度
         * @param columnWidth 列宽度
         * @param actualColumns 实际列数
         * @param maxEntriesPerColumn 每列最大显示条目数
         * @param totalWidth 总宽度
         * @param totalHeight 总高度
         */
        public LayoutCache(int playerCount, int screenWidth, int screenHeight, double guiScale,
                          boolean showPing, String headerText, int maxPlayerNameWidth, 
                          int maxDimensionWidth, int maxPingWidth, int columnWidth, 
                          int actualColumns, int maxEntriesPerColumn, int totalWidth, 
                          int totalHeight) {
            this.playerCount = playerCount;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.guiScale = guiScale;
            this.showPing = showPing;
            this.headerText = headerText;
            this.maxPlayerNameWidth = maxPlayerNameWidth;
            this.maxDimensionWidth = maxDimensionWidth;
            this.maxPingWidth = maxPingWidth;
            this.columnWidth = columnWidth;
            this.actualColumns = actualColumns;
            this.maxEntriesPerColumn = maxEntriesPerColumn;
            this.totalWidth = totalWidth;
            this.totalHeight = totalHeight;
            // 记录缓存创建时间
            this.cacheTime = System.currentTimeMillis();
        }
        
        /**
         * 验证缓存是否有效
         * @param playerCount 玩家数量
         * @param screenWidth 屏幕宽度
         * @param screenHeight 屏幕高度
         * @param guiScale GUI 缩放值
         * @param showPing 是否显示 ping 值
         * @param headerText 标题文本
         * @return 缓存是否有效
         */
        public boolean isValid(int playerCount, int screenWidth, int screenHeight,
                              double guiScale, boolean showPing, String headerText) {
            // 缓存有效期为 100ms，避免使用过期数据
            long currentTime = System.currentTimeMillis();
            if (currentTime - cacheTime > 100) {
                return false;
            }
            
            // 检查各项参数是否与缓存中的一致
            return this.playerCount == playerCount &&
                   this.screenWidth == screenWidth &&
                   this.screenHeight == screenHeight &&
                   Math.abs(this.guiScale - guiScale) < 0.01 &&
                   this.showPing == showPing &&
                   this.headerText.equals(headerText);
        }
    }
    
    // 文本宽度缓存
    /**
     * 文本宽度缓存类，用于缓存文本的渲染宽度，避免重复计算
     */
    private static class TextWidthCache {
        // 存储文本及其对应的渲染宽度
        private final Map<String, Integer> cache = new HashMap<>();
        // 上次清理缓存的时间
        private long lastClearTime = System.currentTimeMillis();
        
        /**
         * 获取文本在指定缩放比例下的渲染宽度
         * @param text 文本内容
         * @param scale 缩放比例
         * @return 文本的渲染宽度
         */
        public int getTextWidth(String text, float scale) {
            // 每 5 秒清理一次缓存，避免内存泄漏
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClearTime > 5000) {
                cache.clear();
                lastClearTime = currentTime;
            }
            
            // 生成缓存键，包含文本内容和缩放比例
            String key = text + "_" + scale;
            // 如果缓存中不存在，则计算宽度并缓存
            return cache.computeIfAbsent(key, k -> (int)(Minecraft.getInstance().font.width(text) * scale));
        }
    }
    
    // 布局缓存实例
    private static LayoutCache layoutCache = null;
    // 文本宽度缓存实例
    private static final TextWidthCache textWidthCache = new TextWidthCache();
    
    /**
     * 获取当前界面缩放值
     * @return 当前界面缩放值
     */
    private double getGuiScale() {
        // 获取 Minecraft 的 GUI 缩放值
        double guiScale = minecraft.options.guiScale().get();
        
        // 如果是自动缩放(0)，则根据屏幕分辨率计算实际缩放值
        if (guiScale == 0) {
            guiScale = minecraft.getWindow().getGuiScale();
        }
        
        return guiScale;
    }
    
    /**
     * 收集所有玩家数据，便于渲染和测试
     * @return 包含所有玩家数据的列表
     */
    public List<PlayerData> collectAllPlayers() {
        // 从客户端数据管理器获取按维度分类的玩家数据
        Map<String, List<PlayerData>> playersByDimension = ClientDataManager.getPlayersByDimension();
        // 存储所有玩家数据的列表
        List<PlayerData> allPlayers = new ArrayList<>();
        // 遍历每个维度的玩家数据列表，添加到总列表中
        for (List<PlayerData> list : playersByDimension.values()) {
            allPlayers.addAll(list);
        }
        return allPlayers;
    }

    /**
     * 设置当前页面
     * @param page 要设置的页面编号
     */
    public static void setCurrentPage(int page) {
        // 确保页面编号在有效范围内
        if (page >= 0 && page < totalPages) {
            currentPage = page;
        }
    }
    
    /**
     * 获取当前页面
     * @return 当前页面编号
     */
    public static int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * 获取总页数
     * @return 总页数
     */
    public static int getTotalPages() {
        return totalPages;
    }
    
    /**
     * 切换到下一页
     */
    public static void nextPage() {
        // 确保当前页面不是最后一页
        if (currentPage < totalPages - 1) {
            currentPage++;
        }
    }
    
    /**
     * 切换到上一页
     */
    public static void previousPage() {
        // 确保当前页面不是第一页
        if (currentPage > 0) {
            currentPage--;
        }
    }
    
    /**
     * 渲染自定义 TAB 列表（带分页功能和缓存优化）
     * @param guiGraphics 图形绘制上下文
     * @param width 屏幕宽度
     * @param height 屏幕高度
     * @param playerInfoList 玩家信息列表
     */
    public void renderCustomPlayerList(GuiGraphics guiGraphics, int width, int height, List<PlayerInfo> playerInfoList) {
        try {
            // 获取当前界面缩放值
            double guiScale = getGuiScale();
            // 获取是否显示 ping 值的配置
            boolean showPing = TabListConfig.SHOW_PING.get();
            // 获取标题文本配置
            String headerText = TabListConfig.HEADER_TEXT.get();

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
            allPlayers.sort((p1, p2) -> p1.name().compareToIgnoreCase(p2.name()));
            
            // 计算分页
            totalPages = Math.max(1, (int) Math.ceil((double) allPlayers.size() / maxPlayersPerPage));
            
            // 确保当前页面在有效范围内
            if (currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }
            if (currentPage < 0) {
                currentPage = 0;
            }
            
            // 获取当前页面的玩家列表
            int startIndex = currentPage * maxPlayersPerPage;
            int endIndex = Math.min(startIndex + maxPlayersPerPage, allPlayers.size());
            List<PlayerData> currentPagePlayers = allPlayers.subList(startIndex, endIndex);

            // 检查缓存是否有效
            LayoutCache currentLayout;
            if (layoutCache != null && layoutCache.isValid(currentPagePlayers.size(), width, height, guiScale, showPing, headerText)) {
                currentLayout = layoutCache;
            } else {
                // 重新计算布局并缓存
                currentLayout = calculateAndCacheLayout(currentPagePlayers, width, height, guiScale, showPing, headerText);
                layoutCache = currentLayout;
            }

            // 使用缓存的布局信息进行渲染
            renderWithCachedLayout(guiGraphics, currentPagePlayers, currentLayout, width, height);
            
        } catch (Exception e) {
            // 记录渲染异常信息
            TabListExtension.LOGGER.error("[TabListRenderer] 渲染 Tab 列表时发生异常: {}", e.getMessage(), e);
            // 渲染降级玩家列表
            renderFallbackPlayerList(guiGraphics, width, height, playerInfoList);
        }
    }
    
    /**
     * 计算布局并缓存结果
     * @param players 当前页面的玩家数据列表
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @param guiScale GUI 缩放值
     * @param showPing 是否显示 ping 值
     * @param headerText 标题文本
     * @return 布局缓存实例
     */
    private LayoutCache calculateAndCacheLayout(List<PlayerData> players, int screenWidth, int screenHeight, 
                                               double guiScale, boolean showPing, String headerText) {
        // 计算布局 - 根据界面缩放调整
        // 基础标题高度
        int baseHeaderHeight = 20;
        // 基础条目高度
        int entryHeight = 10;

        int maxEntries = Math.min(players.size(), maxPlayersPerPage);

        // 计算实际内容宽度
        int maxContentWidth = 0;
        // 创建标题组件
        Component title = Component.literal(headerText);
        // 获取标题文本的渲染宽度
        int titleWidth = textWidthCache.getTextWidth(title.getString(), FONT_SCALE);
        maxContentWidth = Math.max(maxContentWidth, titleWidth);

        // 计算最长的玩家名称宽度、维度名称宽度、ping 值宽度
        int maxPlayerNameWidth = 0;
        int maxDimensionWidth = 0;
        int maxPingWidth = 0;

        // 遍历每个玩家数据，计算各项最大宽度
        for (PlayerData player : players) {
            String playerName = player.name();
            int playerNameWidth = textWidthCache.getTextWidth(playerName, FONT_SCALE);
            maxPlayerNameWidth = Math.max(maxPlayerNameWidth, playerNameWidth);

            String dimension = player.dimension();
            String dimensionName = getLocalizedDimensionName(dimension);
            String dimensionText = "[" + dimensionName + "]";
            int dimensionWidth = textWidthCache.getTextWidth(dimensionText, FONT_SCALE);
            maxDimensionWidth = Math.max(maxDimensionWidth, dimensionWidth);

            if (showPing) {
                int latency = player.latency();
                String pingText = latency < 0 ?
                    Component.translatable("tablist.tablistextension.ping_unknown").getString() :
                    latency + Component.translatable("tablist.tablistextension.ping_ms").getString();
                int pingTextWidth = textWidthCache.getTextWidth(pingText, FONT_SCALE);
                maxPingWidth = Math.max(maxPingWidth, pingTextWidth);
            }
        }

        // 计算总内容宽度
        int spacing = 2;
        int totalContentWidth = maxPlayerNameWidth + spacing + maxDimensionWidth;
        if (showPing) {
            totalContentWidth += spacing + maxPingWidth;
        }

        maxContentWidth = Math.max(maxContentWidth, totalContentWidth);

        // 背景比内容大 2 个像素
        int backgroundPadding = 2;
        int columnWidth = maxContentWidth + (backgroundPadding * 2);

        // 计算每列最大显示的玩家数量
        int maxEntriesPerColumn = Math.min(maxEntries, (screenHeight / 2) / entryHeight);

        // 计算需要的列数
        int numColumns = (int)Math.ceil((double)maxEntries / maxEntriesPerColumn);

        // 限制最大宽度为屏幕的 4/5
        int maxTotalWidth = (int)(screenWidth * 0.8);
        int actualColumns = Math.min(numColumns, maxTotalWidth / (columnWidth + 1));

        if (actualColumns < numColumns) {
            maxEntriesPerColumn = (int)Math.ceil((double)maxEntries / actualColumns);
            int maxHeight = screenHeight - 2 - 20;
            int maxPossibleEntries = (maxHeight - baseHeaderHeight) / entryHeight;
            maxEntriesPerColumn = Math.min(maxEntriesPerColumn, maxPossibleEntries);
        }

        // 计算总宽度和总高度
        int totalWidth = (columnWidth * actualColumns) + (actualColumns - 1);
        int listContentHeight = entryHeight * Math.min(maxEntriesPerColumn, maxEntries);
        int totalHeight = baseHeaderHeight + listContentHeight;

        // 创建并返回布局缓存实例
        return new LayoutCache(players.size(), screenWidth, screenHeight, guiScale, showPing, headerText,
                              maxPlayerNameWidth, maxDimensionWidth, maxPingWidth, columnWidth,
                              actualColumns, maxEntriesPerColumn, totalWidth, totalHeight);
    }
    
    /**
     * 使用缓存的布局信息进行渲染
     * @param guiGraphics 图形绘制上下文
     * @param players 当前页面的玩家数据列表
     * @param layout 布局缓存实例
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     */
    private void renderWithCachedLayout(GuiGraphics guiGraphics, List<PlayerData> players, 
                                       LayoutCache layout, int screenWidth, int screenHeight) {
        // 位置：顶部居中
        int startX = (screenWidth - layout.totalWidth) / 2;
        int startY = 6;

        // 绘制半透明背景
        guiGraphics.fill(startX, startY, startX + layout.totalWidth, startY + layout.totalHeight, 0x50000000);

        // 绘制标题
        Component title = Component.literal(layout.headerText);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);
        // 计算缩放后的标题 X 坐标
        float scaledTitleX = (startX + (float) layout.totalWidth / 2) / FONT_SCALE;
        // 计算缩放后的标题 Y 坐标
        float scaledTitleY = (startY + 5) / FONT_SCALE;
        // 绘制居中的标题文本
        guiGraphics.drawCenteredString(minecraft.font, title, (int)scaledTitleX, (int)scaledTitleY, 0xFFFFFF);
        guiGraphics.pose().popPose();
        
        // 绘制页面指示器（仅当有多页时显示）
        if (totalPages > 1) {
            String pageIndicator = (currentPage + 1) + "/" + totalPages;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);
            // 计算缩放后的页面指示器 X 坐标
            float pageIndicatorX = (startX + layout.totalWidth - 10) / FONT_SCALE;
            // 计算缩放后的页面指示器 Y 坐标
            float pageIndicatorY = (startY + 5) / FONT_SCALE;
            // 绘制页面指示器文本
            guiGraphics.drawString(minecraft.font, pageIndicator, (int)pageIndicatorX - minecraft.font.width(pageIndicator), (int)pageIndicatorY, 0xAAAAAA);
            guiGraphics.pose().popPose();
        }

        // 渲染玩家列表
        int headerHeight = 20;
        int entryHeight = 10;
        int listContentHeight = entryHeight * Math.min(layout.maxEntriesPerColumn, players.size());
        
        // 遍历每一列
        for (int col = 0; col < layout.actualColumns; col++) {
            int colStartX = startX + (col * (layout.columnWidth + 1));
            int colStartY = startY + headerHeight;

            if (col > 0) {
                // 绘制列分隔线
                guiGraphics.fill(colStartX - 1, colStartY, colStartX, colStartY + listContentHeight, 0x80FFFFFF);
            }

            int startIndex2 = col * layout.maxEntriesPerColumn;
            int endIndex2 = Math.min(startIndex2 + layout.maxEntriesPerColumn, players.size());

            // 遍历当前列的每个玩家条目
            for (int i = startIndex2; i < endIndex2 && i < players.size(); i++) {
                PlayerData player = players.get(i);
                int yPos = colStartY + ((i - startIndex2) * entryHeight);
                // 渲染单个玩家条目
                renderPlayerEntry(guiGraphics, player, colStartX, yPos, layout.columnWidth,
                                layout.maxPlayerNameWidth, layout.maxDimensionWidth, layout.maxPingWidth);
            }
        }
    }
    
    /**
     * 降级渲染方案，确保在出错时仍能显示基本信息
     * @param guiGraphics 图形绘制上下文
     * @param width 屏幕宽度
     * @param height 屏幕高度
     * @param playerInfoList 玩家信息列表
     */
    private void renderFallbackPlayerList(GuiGraphics guiGraphics, int width, int height, List<PlayerInfo> playerInfoList) {
        try {
            // 简单的玩家列表渲染
            Component title = Component.literal("Players (" + playerInfoList.size() + ")");
            // 绘制标题文本
            guiGraphics.drawCenteredString(minecraft.font, title, width / 2, 10, 0xFFFFFF);
            
            int y = 25;
            // 遍历前 20 个玩家信息，绘制玩家名称
            for (int i = 0; i < Math.min(playerInfoList.size(), 20); i++) {
                PlayerInfo player = playerInfoList.get(i);
                String name = player.getProfile().getName();
                guiGraphics.drawString(minecraft.font, name, 10, y, 0xFFFFFF);
                y += 10;
            }
        } catch (Exception fallbackException) {
            // 记录降级渲染失败信息
            TabListExtension.LOGGER.error("[TabListRenderer] 降级渲染也失败了", fallbackException);
        }
    }
    
    /**
     * 获取当前玩家所在维度
     * @return 当前玩家所在维度的键名
     */
    private String getCurrentPlayerDimension() {
        if (minecraft.level == null) {
            return "overworld";
        }

        // 检查 level 是否为空,避免空指针异常
        if (minecraft.level != null) {
            return minecraft.level.dimension().location().getPath();
        }
        return "overworld"; // 如果 level 为空则返回默认维度
    }
    
    /**
     * 渲染单个玩家条目
     */
    private void renderPlayerEntry(GuiGraphics guiGraphics, PlayerData player, int x, int y, int width, 
                                  int maxPlayerNameWidth, int maxDimensionWidth, int maxPingWidth) {
        // 获取维度信息
        String dimension = player.dimension();
        String dimensionName = getLocalizedDimensionName(dimension);
        int dimensionColor = getDimensionColor(dimension);
        
        // 获取玩家名称字符串
        String playerName = player.name();
        
        // 定义背景内边距
        int backgroundPadding = 2;
        
        // 计算内容起始位置 (居中整个内容区域)
        int contentStartX = x + backgroundPadding;

        // 计算维度名称的起始位置（右对齐）
        String dimensionText = "[" + dimensionName + "]";
        int dimensionWidth = (int)(minecraft.font.width(dimensionText) * FONT_SCALE);
        int dimensionX = contentStartX + (maxDimensionWidth - dimensionWidth);
        
        // 计算玩家名称的起始位置 (在维度名称之后)
        int playerNameX = contentStartX + maxDimensionWidth + 2;
        
        // 应用字体缩放
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);
        
        // 计算缩放后的坐标
        float scaledDimensionX = dimensionX / FONT_SCALE;
        float scaledPlayerNameX = playerNameX / FONT_SCALE;
        float scaledY = y / FONT_SCALE;
        
        // 绘制维度名称 (带颜色)
        guiGraphics.drawString(minecraft.font, dimensionText, (int)scaledDimensionX, (int)scaledY, dimensionColor);
        
        // 绘制玩家名称 (白色)
        guiGraphics.drawString(minecraft.font, playerName, (int)scaledPlayerNameX, (int)scaledY, 0xFFFFFF);
        
        // 根据配置决定是否显示ping
        if (TabListConfig.SHOW_PING.get()) {
            // 计算ping值的起始位置（左对齐）
            int pingX = contentStartX + maxDimensionWidth + 2 + maxPlayerNameWidth + 2;
            float scaledPingX = pingX / FONT_SCALE;
            
            // 获取ping文本和颜色
            Object[] pingInfo = getPingTextAndColor(player.latency());
            String pingText = (String) pingInfo[0];
            int pingColor = (int) pingInfo[1];
            
            // 绘制ping值（左对齐）
            guiGraphics.drawString(minecraft.font, pingText, (int)scaledPingX, (int)scaledY, pingColor);
        }
        
        // 恢复缩放
        guiGraphics.pose().popPose();
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
     * 获取维度颜色
     */
    private int getDimensionColor(String dimension) {
        // 使用配置文件中的颜色设置
        TabListConfig.DimensionConfig config = TabListConfig.DIMENSION_CONFIGS.get(dimension);
        if (config != null && config.enabled().get()) {
            return config.getColor();
        }
        
        // 原有的默认逻辑作为后备
        return switch (dimension) {
            case "overworld" -> 0x55FF55;
            case "the_nether" -> 0xFF5555;
            case "the_end" -> 0xAA00AA;
            default -> 0xFFFFFF;
        };
    }
    
    /**
     * 获取本地化的维度名称
     */
    private String getLocalizedDimensionName(String dimension) {
        String key = switch (dimension) {
            case "overworld" -> "dimension.tablistextension.overworld";
            case "the_nether" -> "dimension.tablistextension.nether";
            case "the_end" -> "dimension.tablistextension.end";
            default -> "dimension.tablistextension.other";
        };
        return Component.translatable(key).getString().replace("[", "").replace("] ", "");
    }
}