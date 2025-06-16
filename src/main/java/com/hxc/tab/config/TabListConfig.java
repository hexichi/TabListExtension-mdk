package com.hxc.tab.config;

import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;

import com.hxc.tab.TabListExtension;

@Mod.EventBusSubscriber(modid = TabListExtension.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabListConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // 维度配置
    public static final Map<String, DimensionConfig> DIMENSION_CONFIGS = new HashMap<>();
    
    // 一般设置
    public static final ForgeConfigSpec.BooleanValue SHOW_PREFIX;
    public static final ForgeConfigSpec.BooleanValue GROUP_BY_DIMENSION;
    public static final ForgeConfigSpec.IntValue UPDATE_INTERVAL;
    public static final ForgeConfigSpec.BooleanValue SHOW_PING;
    public static final ForgeConfigSpec.ConfigValue<String> HEADER_TEXT;
    
    // 分页设置
    public static final ForgeConfigSpec.IntValue MAX_PLAYERS_PER_PAGE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PAGINATION;
    public static final ForgeConfigSpec.BooleanValue SHOW_PAGE_INDICATOR;
    
    static {
        BUILDER.comment("TabListExtension配置文件").push("dimensions");
        
        // 主世界配置
        BUILDER.push("overworld");
        DIMENSION_CONFIGS.put("overworld", new DimensionConfig(
                BUILDER.comment("是否启用主世界颜色").define("enabled", true),
                // 在静态块中添加颜色验证
                BUILDER.comment("主世界颜色")
                    .define("color", "GREEN", obj -> {
                        if (obj instanceof String) {
                            try {
                                ChatFormatting.valueOf((String) obj);
                                return true;
                            } catch (IllegalArgumentException e) {
                                return false;
                            }
                        }
                        return false;
                    })
        ));
        BUILDER.pop();
        
        // 下界配置
        BUILDER.push("nether");
        DIMENSION_CONFIGS.put("nether", new DimensionConfig(
                BUILDER.comment("是否启用下界颜色").define("enabled", true),
                BUILDER.comment("下界颜色").define("color", "DARK_RED")
        ));
        BUILDER.pop();
        
        // 末地配置
        BUILDER.push("end");
        DIMENSION_CONFIGS.put("end", new DimensionConfig(
                BUILDER.comment("是否启用末地颜色").define("enabled", true),
                BUILDER.comment("末地颜色").define("color", "DARK_PURPLE")
        ));
        BUILDER.pop();
        
        // 其他维度配置
        BUILDER.push("other");
        DIMENSION_CONFIGS.put("other", new DimensionConfig(
                BUILDER.comment("是否启用其他维度颜色").define("enabled", true),
                BUILDER.comment("其他维度颜色").define("color", "WHITE")
        ));
        BUILDER.pop();
        
        BUILDER.pop(); // dimensions
        
        // 一般设置
        BUILDER.push("general");
        SHOW_PREFIX = BUILDER.comment("是否在玩家名称前显示维度前缀").define("show_prefix", true);
        GROUP_BY_DIMENSION = BUILDER.comment("是否按维度分组显示玩家").define("group_by_dimension", true);
        UPDATE_INTERVAL = BUILDER.comment("玩家信息同步间隔（tick）").defineInRange("update_interval", 10, 1, 100);
        SHOW_PING = BUILDER.comment("是否显示玩家延迟（ping）").define("show_ping", true);
        HEADER_TEXT = BUILDER.comment("Tab列表顶部显示的自定义文本").define("header_text", "玩家列表");
        
        // 分页设置
        MAX_PLAYERS_PER_PAGE = BUILDER.comment("每页显示的最大玩家数量").defineInRange("max_players_per_page", 80, 5, 100);
        ENABLE_PAGINATION = BUILDER.comment("是否启用分页功能").define("enable_pagination", true);
        SHOW_PAGE_INDICATOR = BUILDER.comment("是否显示页面指示器").define("show_page_indicator", true);
        
        BUILDER.pop(); // general
        SPEC = BUILDER.build();
    }
    
    public static class DimensionConfig {
        public final ForgeConfigSpec.BooleanValue enabled;
        public final ForgeConfigSpec.ConfigValue<String> color;
        
        public DimensionConfig(ForgeConfigSpec.BooleanValue enabled, ForgeConfigSpec.ConfigValue<String> color) {
            this.enabled = enabled;
            this.color = color;
        }
        
        public ChatFormatting getChatFormatting() {
            try {
                return ChatFormatting.valueOf(color.get());
            } catch (IllegalArgumentException e) {
                TabListExtension.LOGGER.warn("Invalid color format: {}, using WHITE as default", color.get());
                return ChatFormatting.WHITE;
            }
        }
        
        public int getColor() {
            ChatFormatting formatting = getChatFormatting();
            // 使用 Minecraft 内置的颜色值
            Integer color = formatting.getColor();
            return color != null ? color : 0xFFFFFF;
        }
    }
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        TabListExtension.LOGGER.info("Loaded TabListExtension config");
    }
    
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        TabListExtension.LOGGER.info("Reloaded TabListExtension config");
        // 添加配置验证
        validateConfig();
        // 通知其他组件配置已更新
    }
    
    private static void validateConfig() {
        // 验证所有维度配置的颜色值
        DIMENSION_CONFIGS.forEach((dimension, config) -> {
            try {
                ChatFormatting.valueOf(config.color.get());
            } catch (IllegalArgumentException e) {
                TabListExtension.LOGGER.warn("Invalid color for dimension {}: {}", dimension, config.color.get());
            }
        });
    }
}