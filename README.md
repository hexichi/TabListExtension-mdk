# TAB List Extension 模组描述 / TAB List Extension Mod Description

## 语言/Language

[中文](#cn) / [English](#en)

---

## 中文描述 <a id="cn"></a>

### 模组功能
- TAB List Extension 是一个功能强大的 Minecraft Forge 模组，它完全重写了TAB列表的渲染系统，通过自定义渲染器为玩家提供更丰富的信息显示。模组能够实时显示玩家所在维度、网络延迟，并支持按维度分组显示，让您轻松掌握服务器中所有玩家的状态。
- 这是一个使用先进的客户端-服务端数据同步技术和Mixin注入技术的模组，为玩家提供更好的游戏体验。

### 主要特点
- **完全自定义TAB列表**：使用Mixin技术完全接管原版TAB列表渲染，提供更灵活的显示方式
- **维度颜色区分**：根据玩家所在维度自动为玩家名称设置不同颜色
- **按维度分组显示**：玩家按所在维度分组显示，结构清晰易读
- **实时延迟显示**：显示每个玩家的网络延迟（ping），用文本形式直观展示连接质量，并根据延迟值显示不同颜色
- **实时数据同步**：服务端每秒向所有客户端同步玩家维度和延迟信息
- **高度可配置**：支持丰富的配置选项，用户可自定义显示效果
- **客户端渲染**：所有渲染在客户端进行，减少服务器负担
- **自定义标题**：支持自定义TAB列表标题文本
- **智能缓存管理**：自动处理客户端连接/断开事件，确保数据一致性

### 技术架构
- **事件处理系统**：使用Forge事件系统拦截原版TAB列表渲染
- **网络数据包系统**：自定义PlayerDimensionPacket实现客户端-服务端数据同步
- **客户端数据管理**：ClientDataManager负责管理和存储玩家维度信息
- **客户端缓存管理**：ClientEventHandler处理连接事件，确保数据一致性
- **自定义渲染器**：TabListRenderer提供完全自定义的TAB列表渲染
- **配置系统**：基于Forge ConfigSpec的完整配置系统

### 为什么选择这个模组
如果您需要：
- 更直观地了解服务器中玩家的分布情况
- 实时监控玩家的网络连接质量
- 高度可定制的TAB列表显示效果
- 稳定可靠的客户端-服务端数据同步
- 无缓存问题的流畅体验

这个模组正是您所需要的。它不仅提供了视觉上的改进，还通过技术创新确保了最佳的性能和兼容性。

### 安装和使用
1. **服务器端**：将模组jar文件放入服务器的mods文件夹
2. **客户端**：将模组jar文件放入客户端的mods文件夹
3. **配置**：编辑 `config/tablistextension-common.toml` 自定义显示效果
4. **使用**：打开TAB列表（默认Tab键）查看增强效果

### 技术细节
模组使用现代化的架构设计：
- 服务端通过TickEvent定期收集玩家信息（每10tick同步一次）
- 使用自定义网络数据包进行高效的数据传输
- 客户端使用事件系统拦截原版TAB列表渲染
- 客户端连接/断开事件自动清理缓存数据，确保数据一致性
- 配置系统支持热重载，无需重启游戏
- 优化的数据同步机制，只在数据变化时发送更新

---

## English Description <a id="en"></a>

### Mod Function
- TAB List Extension is a powerful Minecraft Forge mod that completely rewrites the TAB list rendering system. Through a custom renderer, it provides players with richer information display, including real-time dimension location, network latency, and dimension-grouped display, allowing you to easily monitor all player statuses on the server.
- This is a mod that utilizes advanced client-server data synchronization and event handling technologies to provide players with a better gaming experience.

### Key Features
- **Completely Custom TAB List**: Uses event handling to fully take over vanilla TAB list rendering, providing more flexible display options
- **Dimension Color Distinction**: Automatically sets different colors for player names based on their current dimension
- **Dimension-Grouped Display**: Players are grouped by their current dimension for clear and readable structure
- **Real-time Latency Display**: Shows each player's network latency (ping) with text representation of connection quality, colored based on latency values
- **Real-time Data Sync**: Server synchronizes player dimension and latency information to all clients every second
- **Highly Configurable**: Supports rich configuration options for users to customize display effects
- **Client-side Rendering**: All rendering performed on client-side, reducing server load
- **Custom Title**: Support for customizing the TAB list title text
- **Smart Cache Management**: Automatically handles client connection/disconnection events to ensure data consistency

### Technical Architecture
- **Event Handling System**: Uses Forge event system to intercept vanilla TAB list rendering
- **Network Packet System**: Custom PlayerDimensionPacket for client-server data synchronization
- **Client Data Management**: ClientDataManager handles and stores player dimension information
- **Client Cache Management**: ClientEventHandler processes connection events to ensure data consistency
- **Custom Renderer**: TabListRenderer provides completely custom TAB list rendering
- **Configuration System**: Complete configuration system based on Forge ConfigSpec

### Why Choose This Mod
If you need:
- More intuitive understanding of player distribution on the server
- Real-time monitoring of player network connection quality
- Highly customizable TAB list display effects
- Stable and reliable client-server data synchronization
- Smooth experience without cache issues

This mod is exactly what you need. It not only provides visual improvements but also ensures optimal performance and compatibility through technical innovation.

### Installation and Usage
1. **Server-side**: Place the mod jar file in the server's mods folder
2. **Client-side**: Place the mod jar file in the client's mods folder
3. **Configuration**: Edit `config/tablistextension-common.toml` to customize display effects
4. **Usage**: Open TAB list (default Tab key) to view enhanced effects

### Technical Details
The mod uses modern architectural design:
- Server periodically collects player information through TickEvent (syncs every 10 ticks)
- Uses custom network packets for efficient data transmission
- Client uses event system to intercept vanilla TAB list rendering
- Client connection/disconnection events automatically clear cache data to ensure consistency
- Configuration system supports hot reloading without game restart
- Optimized data synchronization mechanism that only sends updates when data changes

---

## 故障排除 / Troubleshooting

### 常见问题 / Common Issues

#### 服务器重启后显示旧玩家数据 / Server Restart Shows Old Player Data
**问题 / Problem**：服务器重启后，客户端TAB列表仍显示之前的玩家信息 / After server restart, client TAB list still shows previous player information

**解决方案 / Solution**：
- 确保使用最新版本的模组（v2.1.0+）/ Ensure using the latest mod version (v2.1.0+)
- 重新连接服务器 / Reconnect to the server
- 如果问题持续，重启客户端 / If the issue persists, restart the client

#### 模组使用与修改 / Module use and modification
玩家可以自由的将本模组使用于任何服务器，在遵守本模组开源协议的情况下可以对本模组进行修改。 / Players are free to use this module on any server, and may modify this module in compliance with the open source agreement for this module.
---

#### 注意事项 / Caveat
2.0.0版本完全重写了 TAB 列表的渲染系统，拦截了原版 TAB 列表渲染，所以不兼容依赖或修改原版 TAB 列表的模组。 / Version 2.0.0 completely rewrites the TAB list rendering system and intercepts the original TAB list rendering, so it is not compatible with modules that depend on or modify the original TAB list.
---

### 技术支持 / Technical Support
如果遇到其他问题，请：/ If you encounter other issues, please:
1. 检查游戏日志文件 / Check game log files
2. 确认模组版本和Forge版本 / Confirm mod version and Forge version
3. 在GitHub Issues中报告问题 / Report issues in GitHub Issues

---

## 开发信息 / Development Information

### 版本历史 / Version History
- **v2.1.0**: 修复客户端缓存问题和配置键名不匹配问题 / Fixed client cache issues and configuration key name mismatches
  - 添加ClientEventHandler处理客户端连接/断开事件 / Added ClientEventHandler for client connection/disconnection events
  - 修复配置文件中键名与Java代码不匹配的问题 / Fixed configuration key name mismatches between config file and Java code
  - 优化服务器重启后的数据清理机制 / Optimized data cleanup mechanism after server restart
- **v2.0.0**: 完全重写架构，采用客户端渲染和事件系统技术
- **v1.5.0**: 引入Forge配置系统，允许动态修改维度前缀和颜色,支持多语言
- **v1.3.0**: 初始版本，基于记分板团队系统

### 贡献 / Contributing
欢迎提交Issue和Pull Request来改进这个模组。<br>Welcome to submit Issues and Pull Requests to improve this mod.

### 许可证 / License
MIT License

### 作者 / Author
hexichi