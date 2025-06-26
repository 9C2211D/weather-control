# WeatherControl - 天气控制插件

一个功能丰富的Minecraft天气控制插件，支持投票系统和管理员直接控制。

## 🌤️ 功能特性

### 天气类型支持
- **晴天** (clear) - 阳光明媚的好天气
- **雨天** (rain) - 淅淅沥沥的雨水
- **雷暴** (thunder) - 电闪雷鸣的暴风雨
- **雪天** (snow) - 纷纷扬扬的雪花

### 控制方式
- **管理员直接控制** - 拥有权限的管理员可以直接设置天气
- **玩家投票系统** - 普通玩家可以发起投票来改变天气
- **可点击投票界面** - 聊天框中的赞成/反对按钮

### 投票系统特性
- 可配置的投票持续时间
- 可配置的最少投票人数要求
- 可配置的投票成功比例
- 投票冷却时间防止刷屏
- 实时投票结果显示

## 📋 命令列表

| 命令 | 权限 | 描述 |
|------|------|------|
| `/weather set <type>` | `weather.admin` | 直接设置天气 |
| `/weather vote <type>` | `weather.vote` | 发起天气投票 |
| `/weather info` | `weather.info` | 查看当前天气信息 |
| `/weather reload` | `weather.reload` | 重新加载配置文件 |

### 天气类型参数
- `clear` - 晴天
- `rain` - 雨天
- `thunder` - 雷暴
- `snow` - 雪天

## 🔐 权限系统

| 权限节点 | 默认 | 描述 |
|----------|------|------|
| `weather.admin` | OP | 允许直接设置天气 |
| `weather.vote` | true | 允许发起和参与天气投票 |
| `weather.info` | true | 允许查看天气信息 |
| `weather.reload` | OP | 允许重新加载配置 |
| `weather.use` | true | 基础天气命令权限 |

## ⚙️ 配置文件

### 投票系统配置
```yaml
voting:
  duration: 30          # 投票持续时间（秒）
  min_voters: 2         # 最少投票人数要求
  success_ratio: 0.5    # 投票成功所需的赞成比例（0.0-1.0）
  cooldown: 60          # 投票冷却时间（秒）
  auto_vote_initiator: true  # 发起者是否自动投赞成票
```

### 消息自定义
所有插件消息都可以在配置文件中自定义，支持颜色代码（使用 `&` 符号）。

## 🚀 安装方法

1. 下载插件jar文件
2. 将jar文件放入服务器的 `plugins` 文件夹
3. 重启服务器或使用 `/reload` 命令
4. 根据需要修改 `plugins/WeatherControl/config.yml` 配置文件
5. 使用 `/weather reload` 重新加载配置

## 🔧 构建方法

```bash
# 克隆项目
git clone <repository-url>
cd weather-control

# 构建插件
./gradlew build

# 生成的jar文件位于 build/libs/ 目录
```

## 📖 使用示例

### 管理员直接设置天气
```
/weather set rain
```

### 玩家发起投票
```
/weather vote thunder
```

### 查看当前天气
```
/weather info
```

## 🎮 投票流程

1. 玩家使用 `/weather vote <type>` 发起投票
2. 系统广播投票消息给所有在线玩家
3. 玩家点击聊天框中的 **[赞成]** 或 **[反对]** 按钮投票
4. 投票时间结束后，系统根据配置的成功比例判断结果
5. 如果投票成功，自动改变天气；如果失败，维持当前天气

## 🐛 问题反馈

如果遇到问题或有功能建议，请提交Issue或联系开发者。

## 📄 许可证

本项目采用 MIT 许可证。

---

**版本**: 1.0-SNAPSHOT  
**兼容性**: Minecraft 1.20.1+  
**服务端**: Paper/Spigot