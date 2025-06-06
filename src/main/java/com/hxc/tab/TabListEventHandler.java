package com.hxc.tab;

import com.hxc.tab.config.TabListConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理TAB列表玩家名称颜色的事件处理器类
 */
public class TabListEventHandler {
    // 在类顶部添加日志记录器
    private static final Logger LOGGER = LogManager.getLogger();
    
    // 缓存玩家维度信息，避免频繁更新
    private final Map<UUID, ResourceKey<Level>> playerDimensions = new ConcurrentHashMap<>();
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
     * 创建或获取团队
     * @param scoreboard 记分板
     * @param teamName 团队名称
     * @param dimensionKey 维度键名（overworld, nether, end, other）
     * @return 团队对象
     */
    private PlayerTeam ensureTeamExists(Scoreboard scoreboard, String teamName, String dimensionKey) {
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
            
            // 从配置中获取颜色
            TabListConfig.DimensionConfig config = TabListConfig.DIMENSION_CONFIGS.get(dimensionKey);
            ChatFormatting color = config != null ? config.getChatFormatting() : ChatFormatting.WHITE;
            team.setColor(color);
            
            // 如果启用了前缀显示
            if (TabListConfig.SHOW_PREFIX.get()) {
                // 从语言文件获取前缀文本
                String translationKey = "dimension.tablistextension." + dimensionKey;
                Component prefix = Component.translatable(translationKey).withStyle(color);
                team.setPlayerPrefix(prefix);
            }
        }
        return team;
    }

    /**
     * 确保所有需要的团队都已创建
     */
    private void ensureTeamsExist() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        
        Scoreboard scoreboard = server.getScoreboard();
        
        // 创建所有团队
        ensureTeamExists(scoreboard, TEAM_OVERWORLD, "overworld");
        ensureTeamExists(scoreboard, TEAM_NETHER, "nether");
        ensureTeamExists(scoreboard, TEAM_END, "end");
        ensureTeamExists(scoreboard, TEAM_OTHER, "other");
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
     * 检查并更新玩家维度信息
     * @param player 玩家
     * @return 如果维度发生变化返回true，否则返回false
     */
    private boolean checkAndUpdatePlayerDimension(ServerPlayer player) {
        ResourceKey<Level> dimension = player.level().dimension();
        UUID playerId = player.getUUID();
        
        // 检查维度是否真的变化了
        ResourceKey<Level> oldDimension = playerDimensions.get(playerId);
        if (oldDimension != null && oldDimension.equals(dimension)) {
            return false; // 维度没有变化，不需要更新
        }
        
        // 添加日志
        LOGGER.debug("Player {} changed dimension from {} to {}", 
                   player.getName().getString(), 
                   oldDimension != null ? oldDimension.location() : "null", 
                   dimension.location());
        
        // 更新缓存
        playerDimensions.put(playerId, dimension);
        
        // 更新玩家团队
        updatePlayerTeam(player, dimension);
        return true;
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
        
        checkAndUpdatePlayerDimension((ServerPlayer) player);
    }

    /**
     * 监听实体加入世界事件
     * 用于捕获通过指令传送的玩家
     * @param event 实体加入世界事件
     */
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // 添加服务器端检查，避免客户端执行
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        
        ServerPlayer player = (ServerPlayer) event.getEntity();
        
        // 强制更新玩家团队，忽略缓存检查
        ResourceKey<Level> dimension = player.level().dimension();
        playerDimensions.put(player.getUUID(), dimension); // 先更新缓存
        updatePlayerTeam(player, dimension); // 直接更新团队
        
        // 添加更详细的日志
        LOGGER.info("Entity join level event: Player {} joined dimension {}", 
                   player.getName().getString(), 
                   dimension.location());
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
        
        checkAndUpdatePlayerDimension((ServerPlayer) player);
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
        
        // 强制更新玩家团队，忽略缓存检查
        ResourceKey<Level> dimension = serverPlayer.level().dimension();
        playerDimensions.put(serverPlayer.getUUID(), dimension); // 先更新缓存
        updatePlayerTeam(serverPlayer, dimension); // 直接更新团队
        
        // 添加更详细的日志
        LOGGER.info("Player respawn event: Player {} respawned in dimension {}", 
                   serverPlayer.getName().getString(), 
                   dimension.location());
    }

    /**
     * 监听配置重载事件
     */
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TabListConfig.SPEC) {
            // 重新创建所有团队以应用新配置
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Scoreboard scoreboard = server.getScoreboard();
                // 清理旧团队
                scoreboard.getPlayerTeams().forEach(team -> {
                    if (team.getName().startsWith("tab_")) {
                        scoreboard.removePlayerTeam(team);
                    }
                });
            }
        }
    }
}