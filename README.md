# WeatherControl

一个为 Minecraft Paper/Spigot 服务器设计的智能天气控制插件，支持民主投票系统和管理员直接控制，让服务器的天气变化更加有趣和互动。

## ✨ 功能特性

- 🌤️ **多种天气类型** - 支持晴天、雨天、雷暴、雪天四种天气模式
- 🗳️ **民主投票系统** - 玩家可以发起投票来决定天气变化
- ⚡ **管理员快速控制** - 管理员可以直接设置天气，无需投票
- 🎯 **智能投票机制** - 可配置投票时长、最少人数、成功比例等参数
- 🔄 **防刷屏保护** - 投票冷却时间机制，避免频繁发起投票
- 💬 **交互式界面** - 聊天框中的可点击赞成/反对按钮
- 📊 **实时结果显示** - 投票过程中实时显示当前投票状况
- 🎨 **颜色代码支持** - 支持 Minecraft 颜色代码，消息更加生动

## 📋 系统要求

- **Minecraft 版本**: 1.20+
- **服务端**: Paper/Spigot
- **Java 版本**: 17+

## 🚀 安装方法

1. 下载插件 jar 文件
2. 将文件放入服务器的 `plugins` 文件夹
3. 重启服务器或使用 `/reload` 命令
4. 插件会自动生成默认配置文件

## 📋 命令列表

| 命令 | 权限 | 描述 |
|------|------|------|
| `/weather set <type>` | `weather.admin` | 直接设置天气 |
| `/weather vote <type>` | `weather.vote` | 发起天气投票 |
| `/weather info` | `weather.info` | 查看当前天气信息 |
| `/weather reload` | `weather.reload` | 重新加载配置文件 |

### 🌤️ 天气类型参数
- `clear` - 晴天 ☀️
- `rain` - 雨天 🌧️
- `thunder` - 雷暴 ⛈️
- `snow` - 雪天 ❄️

## 🔐 权限系统

| 权限节点 | 默认 | 描述 |
|----------|------|------|
| `weather.admin` | OP | 允许直接设置天气 |
| `weather.vote` | true | 允许发起和参与天气投票 |
| `weather.info` | true | 允许查看天气信息 |
| `weather.reload` | OP | 允许重新加载配置 |
| `weather.use` | true | 基础天气命令权限 |

## ⚙️ 配置说明

配置文件位置：`plugins/WeatherControl/config.yml`

### 投票系统配置

```yaml
# 投票系统设置
voting:
  duration: 30          # 投票持续时间（秒）
  min_voters: 2         # 最少投票人数要求
  success_ratio: 0.5    # 投票成功所需的赞成比例（0.0-1.0）
  cooldown: 60          # 投票冷却时间（秒）
  auto_vote_initiator: true  # 发起者是否自动投赞成票
```

### 消息自定义

```yaml
# 消息模板（支持颜色代码）
messages:
  vote_started: "&e%s &7发起了天气投票：&b%s"
  vote_success: "&a投票成功！天气已变更为：&b%s"
  vote_failed: "&c投票失败，未达到所需票数"
  # ... 更多消息模板
```

> 💡 **提示**: 使用 `&` 符号添加颜色代码，`%s` 作为占位符

## 🎮 使用方法

### 管理员操作
```
/weather set rain     # 直接设置为雨天
/weather reload       # 重新加载配置
```

### 玩家投票
```
/weather vote thunder # 发起雷暴天气投票
/weather info         # 查看当前天气信息
```

### 投票流程
1. 玩家使用 `/weather vote <天气类型>` 发起投票
2. 其他玩家点击聊天框中的 **[赞成]** 或 **[反对]** 按钮
3. 投票时间结束后，根据结果决定是否改变天气

## 🔧 工作原理

1. **投票发起** - 玩家发起天气投票请求
2. **权限检查** - 验证玩家是否有投票权限
3. **冷却检测** - 检查是否在冷却时间内
4. **投票进行** - 显示可点击的投票界面
5. **结果统计** - 实时统计赞成/反对票数
6. **天气变更** - 根据投票结果执行天气改变

## 📝 配置示例

### 场景1：小型服务器（快速投票）
```yaml
voting:
  duration: 15
  min_voters: 1
  success_ratio: 0.6
  cooldown: 30
```

### 场景2：大型服务器（严格投票）
```yaml
voting:
  duration: 60
  min_voters: 5
  success_ratio: 0.7
  cooldown: 120
```

### 场景3：管理员主导模式
```yaml
voting:
  duration: 45
  min_voters: 10
  success_ratio: 0.8
  cooldown: 300
```

## 🛠️ 开发信息

- **开发者**: 9C2211D
- **版本**: 1.0-SNAPSHOT
- **API 版本**: 1.20
- **构建工具**: Gradle
- **依赖**: Paper API 1.20.1

## 📦 构建项目

```bash
# 克隆项目
git clone <repository-url>
cd weather-control

# 构建插件
./gradlew build

# 运行测试服务器
./gradlew runServer
```

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🎯 未来计划

- [ ] 支持更多天气类型（如雾天、风暴等）
- [ ] 添加天气持续时间控制
- [ ] 支持区域性天气设置
- [ ] 添加天气变化音效
- [ ] 支持自定义天气效果
- [ ] 添加投票历史记录
- [ ] 支持多语言消息模板

---

**如果这个插件对你有帮助，请给个 ⭐ Star！**