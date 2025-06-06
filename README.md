# TAB List Extension 模组描述 / TAB List Extension Mod Description

## 中文描述

### 模组功能
- TAB List Extension 是一个简单而实用的 Minecraft Forge 模组，它通过在 TAB 列表中为玩家名称添加颜色和前缀，使您能够轻松识别每个玩家所在的维度。无论是在多人服务器上与朋友一起玩，还是在单人游戏中，这个模组都能帮助您快速了解玩家分布情况。
- 这是我学习创作的第一个模组，未来会接入更多功能。

### 主要特点
- **维度颜色区分**：根据玩家所在维度自动为 TAB 列表中的玩家名称设置不同颜色
  - 主世界：绿色 + "[主世界]" 前缀
  - 下界：红色 + "[下界]" 前缀
  - 末地：深紫色 + "[末地]" 前缀
  - 其他维度：白色 + "[未知]" 前缀（后续版本更新会支持更多维度）
- **实时更新**：当玩家在不同维度间切换时，TAB 列表中的颜色和前缀会立即更新
- **服务器兼容**：完全兼容多人服务器，所有玩家都能看到相同的颜色效果
- **无需客户端**：作为服务器端模组，客户端无需安装即可看到效果

### 为什么下载
如果您厌倦了在多人游戏中不断询问"你在哪个维度?"，或者想要更直观地了解服务器中玩家的分布情况，这个模组正是您所需要的。它通过视觉上的区分，让游戏协作变得更加简单和高效。

### 使用方法
1. 将模组安装到服务器端（或单人游戏）
2. 无需额外配置，模组会自动工作
3. 打开 TAB 列表（默认按 Tab 键）查看效果

### 技术说明
模组使用 Minecraft 原生的记分板团队系统实现功能，确保了最大的兼容性和稳定性。它会自动监听玩家的维度变化、登录和登出事件，并实时更新玩家所属的团队，从而改变 TAB 列表中的显示效果。

---

## English Description

### Mod Function
- TAB List Extension is a simple yet practical Minecraft Forge mod that adds colors and prefixes to player names in the TAB list, allowing you to easily identify which dimension each player is in. Whether playing on a multiplayer server with friends or in singleplayer, this mod helps you quickly understand player distribution.
- This is the first mod I've learned to create and will be accessing more features in the future.

### Key Features
- **Dimension Color Distinction**: Automatically sets different colors for player names in the TAB list based on their current dimension
  - Overworld: Green + "[Overworld]" prefix
  - Nether: Red + "[Nether]" prefix
  - End: Dark Purple + "[End]" prefix
  - Other dimensions: White + "[Unknown]" prefix（Subsequent version updates will support more dimensions）
- **Real-time Updates**: When players switch between dimensions, the colors and prefixes in the TAB list update immediately
- **Server Compatible**: Fully compatible with multiplayer servers, all players see the same color effects
- **No Client Required**: As a server-side mod, clients don't need to install it to see the effects

### Why Download
If you're tired of constantly asking "which dimension are you in?" in multiplayer games, or want to more intuitively understand the distribution of players on your server, this mod is exactly what you need. It makes game collaboration simpler and more efficient through visual distinction.

### How to Use
1. Install the mod on the server side (or singleplayer)
2. No additional configuration needed, the mod works automatically
3. Open the TAB list (default Tab key) to see the effects

### Technical Notes
The mod uses Minecraft's native scoreboard team system to implement its functionality, ensuring maximum compatibility and stability. It automatically listens for player dimension changes, logins, and logouts, and updates the player's team in real-time to change the display effect in the TAB list.
