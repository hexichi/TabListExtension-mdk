package com.hxc.tab;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 处理TAB列表玩家名称颜色的事件处理器类
 */
public class TabListEventHandler {
    // 缓存玩家维度信息，避免频繁更新
    private final Map<UUID, ResourceKey<Level>> playerDimensions = new HashMap<>();
    // 维度对应的团队名称
    private static final String TEAM_OVERWORLD = "tab_overworld";
    private static final String TEAM_NETHER = "tab_nether";
    private static final String TEAM_END = "tab_end";
    private static final String TEAM_OTHER = "tab_other";

    public TabListEventHandler() {
        // 注册这个事件处理器到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 确保所有需要的团队都已创建
     */
    private void ensureTeamsExist() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        
        Scoreboard scoreboard = server.getScoreboard();
        
        // 创建或获取主世界团队
        PlayerTeam overworldTeam = scoreboard.getPlayerTeam(TEAM_OVERWORLD);
        if (overworldTeam == null) {
            overworldTeam = scoreboard.addPlayerTeam(TEAM_OVERWORLD);
            overworldTeam.setColor(ChatFormatting.GREEN);
            overworldTeam.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[主世界] ").withStyle(ChatFormatting.GREEN));
        }
        
        // 创建或获取下界团队
        PlayerTeam netherTeam = scoreboard.getPlayerTeam(TEAM_NETHER);
        if (netherTeam == null) {
            netherTeam = scoreboard.addPlayerTeam(TEAM_NETHER);
            netherTeam.setColor(ChatFormatting.DARK_RED);
            netherTeam.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[下界] ").withStyle(ChatFormatting.DARK_RED));
        }
        
        // 创建或获取末地团队
        PlayerTeam endTeam = scoreboard.getPlayerTeam(TEAM_END);
        if (endTeam == null) {
            endTeam = scoreboard.addPlayerTeam(TEAM_END);
            endTeam.setColor(ChatFormatting.DARK_PURPLE);
            endTeam.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[末地] ").withStyle(ChatFormatting.DARK_PURPLE));
        }
        
        // 创建或获取其他维度团队
        PlayerTeam otherTeam = scoreboard.getPlayerTeam(TEAM_OTHER);
        if (otherTeam == null) {
            otherTeam = scoreboard.addPlayerTeam(TEAM_OTHER);
            otherTeam.setColor(ChatFormatting.WHITE);
            otherTeam.setPlayerPrefix(net.minecraft.network.chat.Component.literal("[未知] ").withStyle(ChatFormatting.WHITE));
        }
    }
    
    /**
     * 根据维度获取对应的团队名称
     * @param dimension 维度
     * @return 团队名称
     */
    private String getTeamNameForDimension(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.OVERWORLD)) {
            return TEAM_OVERWORLD;
        } else if (dimension.equals(Level.NETHER)) {
            return TEAM_NETHER;
        } else if (dimension.equals(Level.END)) {
            return TEAM_END;
        } else {
            return TEAM_OTHER;
        }
    }
    
    /**
     * 更新玩家的团队
     * @param player 玩家
     * @param dimension 维度
     */
    private void updatePlayerTeam(ServerPlayer player, ResourceKey<Level> dimension) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        
        // 确保所有团队存在
        ensureTeamsExist();
        
        Scoreboard scoreboard = server.getScoreboard();
        String teamName = getTeamNameForDimension(dimension);
        
        // 从当前团队中移除玩家
        scoreboard.removePlayerFromTeam(player.getScoreboardName());
        
        // 将玩家添加到新团队
        scoreboard.addPlayerToTeam(player.getScoreboardName(), scoreboard.getPlayerTeam(teamName));
    }
    
    /**
     * 监听玩家维度变化事件
     * 当玩家切换维度时更新TAB列表中的名称颜色
     * @param event 玩家维度变化事件
     */
    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) return;
        
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ResourceKey<Level> dimension = serverPlayer.level().dimension();
        UUID playerId = player.getUUID();
        
        // 检查维度是否真的变化了
        ResourceKey<Level> oldDimension = playerDimensions.get(playerId);
        if (oldDimension != null && oldDimension.equals(dimension)) {
            return; // 维度没有变化，不需要更新
        }
        
        // 更新缓存
        playerDimensions.put(playerId, dimension);
        
        // 更新玩家团队
        updatePlayerTeam(serverPlayer, dimension);
    }
    
    /**
     * 监听玩家登录事件
     * 当玩家登录时设置初始颜色
     * @param event 玩家登录事件
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) return;
        
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ResourceKey<Level> dimension = serverPlayer.level().dimension();
        UUID playerId = player.getUUID();
        
        // 更新缓存
        playerDimensions.put(playerId, dimension);
        
        // 更新玩家团队
        updatePlayerTeam(serverPlayer, dimension);
    }
    
    /**
     * 监听玩家登出事件
     * 当玩家登出时清理缓存
     * @param event 玩家登出事件
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        playerDimensions.remove(player.getUUID());
    }
    
    /**
     * 监听玩家重生事件
     * 当玩家死亡重生时更新TAB列表中的名称颜色
     * @param event 玩家重生事件
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) return;
        
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ResourceKey<Level> dimension = serverPlayer.level().dimension();
        UUID playerId = player.getUUID();
        
        // 更新缓存
        playerDimensions.put(playerId, dimension);
        
        // 更新玩家团队
        updatePlayerTeam(serverPlayer, dimension);
    }
    
    /**
     * 监听实体加入世界事件
     * 用于捕获通过指令传送的玩家
     * @param event 实体加入世界事件
     */
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ResourceKey<Level> dimension = player.level().dimension();
        UUID playerId = player.getUUID();
        
        // 检查维度是否真的变化了
        ResourceKey<Level> oldDimension = playerDimensions.get(playerId);
        if (oldDimension != null && oldDimension.equals(dimension)) {
            return; // 维度没有变化，不需要更新
        }
        
        // 更新缓存
        playerDimensions.put(playerId, dimension);
        
        // 更新玩家团队
        updatePlayerTeam(player, dimension);
    }
}