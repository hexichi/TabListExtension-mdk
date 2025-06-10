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

### 技术架构
- **事件处理系统**：使用Forge事件系统拦截原版TAB列表渲染
- **网络数据包系统**：自定义PlayerDimensionPacket实现客户端-服务端数据同步
- **客户端数据管理**：ClientDataManager负责管理和存储玩家维度信息
- **自定义渲染器**：TabListRenderer提供完全自定义的TAB列表渲染
- **配置系统**：基于Forge ConfigSpec的完整配置系统

#### 维度配置
- 每个维度可单独配置颜色和启用状态
- 支持所有Minecraft颜色格式
- 支持自定义维度

#### 显示配置
- show_prefix ：是否显示维度前缀
- group_by_dimension ：是否按维度分组显示
- show_ping ：是否显示玩家延迟
- header_text ：自定义TAB列表标题
- update_interval ：数据更新间隔（1-100 tick）

### 为什么选择这个模组
如果您需要：
- 更直观地了解服务器中玩家的分布情况
- 实时监控玩家的网络连接质量
- 高度可定制的TAB列表显示效果
- 稳定可靠的客户端-服务端数据同步

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
- 配置系统支持热重载，无需重启游戏

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

### Technical Architecture
- **Event Handling System**: Uses Forge event system to intercept vanilla TAB list rendering
- **Network Packet System**: Custom PlayerDimensionPacket for client-server data synchronization
- **Client Data Management**: ClientDataManager handles and stores player dimension information
- **Custom Renderer**: TabListRenderer provides completely custom TAB list rendering
- **Configuration System**: Complete configuration system based on Forge ConfigSpec


#### Dimension Configuration
- Each dimension can be individually configured for color and enabled status
- Supports all Minecraft color formats
- Supports custom dimensions

#### Display Configuration
- `show_prefix`: Whether to show dimension prefixes
- `group_by_dimension`: Whether to group display by dimension
- `show_ping`: Whether to show player latency
- `header_text`: Custom TAB list title
- `update_interval`: Data update interval (1-100 ticks)

### Why Choose This Mod
If you need:
- More intuitive understanding of player distribution on the server
- Real-time monitoring of player network connection quality
- Highly customizable TAB list display effects
- Stable and reliable client-server data synchronization

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
- Configuration system supports hot reloading without game restart

---

## 开发信息 / Development Information

### 版本历史 / Version History
- **v1.5.0**: 完全重写架构，采用客户端渲染和事件系统技术
- **v1.3.0**: 初始版本，基于记分板团队系统

### 贡献 / Contributing
欢迎提交Issue和Pull Request来改进这个模组。<br>Welcome to submit Issues and Pull Requests to improve this mod.

### 许可证 / License
MIT License

### 作者 / Author
hexichi