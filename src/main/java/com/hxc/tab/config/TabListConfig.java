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
    
    static {
        BUILDER.comment("TabListExtension配置文件").push("dimensions");
        
        // 主世界配置
        BUILDER.push("overworld");
        DIMENSION_CONFIGS.put("overworld", new DimensionConfig(
                BUILDER.comment("是否启用主世界颜色").define("enabled", true),
                BUILDER.comment("主世界颜色").define("color", "GREEN")
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
        SHOW_PREFIX = BUILDER.comment("是否在TAB列表中显示维度前缀").define("show_prefix", true);
        BUILDER.pop();
        
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
                return ChatFormatting.WHITE; // 默认为白色
            }
        }
    }
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        TabListExtension.LOGGER.info("Loaded TabListExtension config");
    }
    
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        TabListExtension.LOGGER.info("Reloaded TabListExtension config");
    }
}