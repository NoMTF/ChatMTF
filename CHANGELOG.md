# Changelog

## v2.7.0-chatmtf

- 项目更名为 ChatMTF，仓库目标切换为 NoMTF/ChatMTF。
- 默认界面改为简体中文，主聊天体验调整为白底、低饱和粉蓝点缀。
- 移除旧项目推广、赞助入口和默认代理服务依赖。
- 互动 UI 默认关闭，入口收敛到高级实验设置，历史 kai-ui 消息仍兼容渲染。
- 移除 Soul 4000 字限制，新增 read_soul、update_soul、append_soul、reset_soul 工具。
- 新增 AI 控制的分段发送能力，用于合适的短消息气泡拆分。
- 新增 OCR 设置入口，支持配置 OCR 服务实例、模型 ID 和 OpenAI 兼容 endpoint。
- 新增 Android SQLite 结构化存储基础，用于会话、记忆、任务、邮件、短信和通知 pending 数据迁移。
- Android 应用 ID 切换为 com.nomtfs.chatmtf。
- 发布自动化收敛为 Android APK Release。
