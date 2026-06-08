# ChatMTF

<p align="center">
  <img src="composeApp/icon.png" width="96" alt="ChatMTF logo">
</p>

<p align="center">
  <b>类 Chatbox 的自定义 API 助手，带长期记忆、Soul 维护、工具调用、MCP、沙盒、OCR 和多服务 fallback。</b>
</p>

<p align="center">
  <a href="https://github.com/NoMTF/ChatMTF/releases">下载 Android APK</a>
  ·
  <a href="https://github.com/NoMTF/ChatMTF/issues">反馈问题</a>
  ·
  <a href="https://github.com/NoMTF/ChatMTF">项目仓库</a>
</p>

## 项目定位

ChatMTF 的主体验是一块干净、轻亮、接近 ChatGPT / Chatbox 的聊天界面。你可以接入 OpenAI-compatible API、OpenAI、Anthropic、Gemini、DeepSeek、Mistral、OpenRouter、本地 LiteRT 模型等服务，让它成为一个长期可用的个人 API 助手。

它保留了原项目强大的 agent 能力，但把实验性互动 UI 默认关闭并移入高级设置。默认体验不再生成复杂互动界面，而是普通、稳定、可长期使用的聊天助手。

## 核心能力

- **自定义 API 助手**：支持 OpenAI-compatible endpoint，可配置 base URL、API key 和模型。
- **跨对话长期记忆**：保存稳定偏好、长期事实、学习结论和错误修正，并在后续对话中自动使用。
- **Soul 无长度限制**：Soul 编辑器不再限制 4000 字，AI 也可以通过工具读取、追加、覆盖或重置自己的 Soul。
- **多服务 fallback**：可按顺序配置多个服务，当前服务失败时自动尝试下一项。
- **默认中文界面**：默认资源切到简体中文，设置、错误、权限说明和主流程文案优先中文。
- **轻亮视觉**：白底、雾白、低饱和粉蓝强调色，减少深色和高对比紫色。
- **实验性互动 UI 可选**：默认关闭，旧消息里的 kai-ui 仍可渲染，避免历史对话损坏。
- **AI 控制分段发送**：短回复可由 AI 在合适时拆成多个聊天气泡，长文、代码、表格和工具结果保持完整。
- **OCR 自定义**：可配置 OCR 开关、服务实例、模型 ID 和 OpenAI-compatible OCR endpoint。
- **Android SQLite 存储层**：会话、记忆、任务、邮箱/SMS/通知 pending 数据逐步迁移到结构化 SQLite；API key、邮箱密码和 Soul 仍保留加密设置。
- **工具 / MCP / 沙盒**：支持工具调用、MCP server、Android Linux sandbox、日程、通知、邮箱、短信等能力。

## Android 使用

1. 到 [Releases](https://github.com/NoMTF/ChatMTF/releases) 下载最新 APK。
2. 安装后打开 **设置 → 服务**。
3. 添加一个服务，推荐从 **OpenAI 兼容 API** 开始。
4. 填写 base URL、API key，并选择聊天模型。
5. 回到聊天页开始使用。

没有配置聊天服务时，ChatMTF 不会使用旧项目共享代理，会提示你添加自己的 API 或本地模型。

## Soul 工具

ChatMTF 新增以下 Soul 维护工具：

- `read_soul`：读取当前 Soul。
- `update_soul`：覆盖保存 Soul。
- `append_soul`：追加内容到 Soul。
- `reset_soul`：恢复默认 Soul。

AI 可以主动维护自己的 Soul，但应避免无意义频繁改写。涉及重大人格、边界或行为策略变化时，需要在回复中说明。

## OCR 设置

在 **设置 → 代理 / OCR** 中可配置：

- 是否启用 OCR。
- 使用哪个服务实例执行 OCR。
- OCR 模型 ID。
- 可选的 OpenAI-compatible OCR base URL。

图片和 PDF 附件会优先尝试 OCR 提取文本，再把文本作为上下文发给聊天模型。OCR 失败时会回退原附件流程，并提示未能完成 OCR。

## 本地构建

需要 JDK 21、Android SDK，以及仓库自带 Gradle Wrapper。

```bash
./gradlew :androidApp:assembleFossDebug
```

Windows PowerShell 示例：

```powershell
$env:ANDROID_HOME="C:\Android"
$env:ANDROID_SDK_ROOT="C:\Android"
.\gradlew.bat :androidApp:assembleFossDebug --console=plain
```

Release APK：

```powershell
$env:ANDROID_HOME="C:\Android"
$env:ANDROID_SDK_ROOT="C:\Android"
.\gradlew.bat :androidApp:assembleFossRelease --console=plain
```

生成文件通常位于：

```text
androidApp/build/outputs/apk/foss/release/
```

## 开发说明

- Android 包名：`com.nomtfs.chatmtf`
- Kotlin namespace 暂时保留原路径，避免一次性大规模迁移影响稳定性。
- `kai-ui` 代码块协议名暂时保留，用于兼容旧消息和渲染器。
- 默认不内置旧共享代理；请配置自己的 API、云服务或本地模型。

## 仓库

项目仓库：[NoMTF/ChatMTF](https://github.com/NoMTF/ChatMTF)

问题反馈：[GitHub Issues](https://github.com/NoMTF/ChatMTF/issues)

## License

见 [LICENSE.txt](LICENSE.txt)。
