package com.inspiredandroid.kai.data

import androidx.compose.runtime.Immutable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Immutable
@Serializable
data class HeartbeatLogEntry(
    val timestampEpochMs: Long,
    val success: Boolean,
    val error: String? = null,
)

@Serializable
data class HeartbeatConfig(
    val enabled: Boolean = true,
    val intervalMinutes: Int = 30,
    val activeHoursStart: Int = 8,
    val activeHoursEnd: Int = 22,
    val lastHeartbeatEpochMs: Long = 0L,
    val heartbeatInstanceId: String? = null,
)

@OptIn(ExperimentalTime::class)
class HeartbeatManager(
    private val appSettings: AppSettings,
    private val memoryStore: MemoryStore,
    private val taskStore: TaskStore,
    private val emailStore: EmailStore? = null,
) {

    private val json = SharedJson

    fun getConfig(): HeartbeatConfig {
        val raw = appSettings.getHeartbeatConfigJson()
        if (raw.isEmpty()) return HeartbeatConfig()
        return try {
            json.decodeFromString<HeartbeatConfig>(raw)
        } catch (_: Exception) {
            HeartbeatConfig()
        }
    }

    fun saveConfig(config: HeartbeatConfig) {
        appSettings.setHeartbeatConfigJson(json.encodeToString(config))
    }

    fun isHeartbeatDue(): Boolean {
        val config = getConfig()
        if (!config.enabled) return false

        val now = Clock.System.now()
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val currentHour = localNow.hour

        // Check active hours
        if (currentHour < config.activeHoursStart || currentHour >= config.activeHoursEnd) return false

        // Check elapsed time
        val elapsedMs = now.toEpochMilliseconds() - config.lastHeartbeatEpochMs
        val intervalMs = config.intervalMinutes * 60_000L
        return elapsedMs >= intervalMs
    }

    fun buildHeartbeatPrompt(
        recentResponses: List<String> = emptyList(),
        pendingEmails: List<EmailMessage> = emptyList(),
        pendingSms: List<SmsMessage> = emptyList(),
        pendingNotifications: List<NotificationRecord> = emptyList(),
        recentConversations: List<HeartbeatRecentConversation> = emptyList(),
    ): String {
        val customPrompt = appSettings.getHeartbeatPrompt()
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = now.toLocalDateTime(timeZone)
        val runtime = HeartbeatRuntimeContext(
            nowLocalIsoWithOffset = "$localDateTime${timeZone.offsetAt(now)}",
            timeZoneId = timeZone.id,
            nowUtcIsoString = now.toString(),
        )
        val tasksSplit = taskStore.getPendingTasksPartitioned()
        val pendingTasks = tasksSplit.scheduled
        val heartbeatAdditions = tasksSplit.heartbeatAdditions
        val emailEnabled = emailStore != null && appSettings.isEmailEnabled()
        val accounts = if (emailEnabled) emailStore.getAccounts() else emptyList()
        val emailAccounts: List<EmailAccountSummary> = accounts.map { account ->
            val syncState = emailStore!!.getSyncState(account.id)
            EmailAccountSummary(
                email = account.email,
                unreadCount = syncState.unreadCount,
                lastSyncEpochMs = syncState.lastSyncEpochMs,
                lastError = syncState.lastError,
            )
        }
        val accountEmailById = accounts.associate { it.id to it.email }
        val heartbeatPending: List<HeartbeatPendingEmail> = if (emailEnabled) {
            pendingEmails.map { msg ->
                HeartbeatPendingEmail(
                    accountEmail = accountEmailById[msg.accountId] ?: msg.accountId,
                    from = msg.from,
                    subject = msg.subject,
                    preview = msg.preview,
                )
            }
        } else {
            emptyList()
        }
        val smsEnabled = appSettings.isSmsEnabled()
        val heartbeatSms: List<HeartbeatPendingSms> = if (smsEnabled) {
            pendingSms.map { msg ->
                HeartbeatPendingSms(
                    id = msg.id,
                    from = msg.address,
                    preview = msg.preview,
                )
            }
        } else {
            emptyList()
        }
        val notificationsEnabled = appSettings.isNotificationsEnabled()
        // Cap the heartbeat snapshot so a flurry of group-chat pings can't blow out the
        // prompt. Newest first; the rest stay in the pending queue and will surface on
        // subsequent heartbeats (or via `check_notifications` on demand).
        val heartbeatNotifications: List<HeartbeatPendingNotification> = if (notificationsEnabled) {
            pendingNotifications
                .sortedByDescending { it.postedAtEpochMs }
                .take(MAX_NOTIFICATIONS_IN_PROMPT)
                .map { record ->
                    HeartbeatPendingNotification(
                        id = record.id,
                        appLabel = record.appLabel,
                        title = record.title,
                        preview = record.preview,
                    )
                }
        } else {
            emptyList()
        }
        val promotionCandidates = memoryStore.getPromotionCandidates().map { entry ->
            HeartbeatPromotionCandidate(
                key = entry.key,
                hitCount = entry.hitCount,
                category = entry.category,
                content = entry.content,
            )
        }
        return buildHeartbeatPrompt(
            customOrDefaultPrompt = customPrompt.ifEmpty { DEFAULT_HEARTBEAT_PROMPT },
            runtime = runtime,
            heartbeatAdditions = heartbeatAdditions,
            recentResponses = recentResponses,
            recentConversations = recentConversations,
            pendingTasks = pendingTasks,
            emailAccounts = emailAccounts,
            pendingEmails = heartbeatPending,
            pendingSms = heartbeatSms,
            pendingNotifications = heartbeatNotifications,
            promotionCandidates = promotionCandidates,
        )
    }

    fun recordHeartbeat(success: Boolean, error: String? = null) {
        val entry = HeartbeatLogEntry(
            timestampEpochMs = Clock.System.now().toEpochMilliseconds(),
            success = success,
            error = error,
        )
        val log = getHeartbeatLog().toMutableList()
        log.add(0, entry)
        val trimmed = log.take(MAX_LOG_ENTRIES)
        appSettings.setHeartbeatLogJson(json.encodeToString(trimmed))
    }

    fun getHeartbeatLog(): List<HeartbeatLogEntry> {
        val raw = appSettings.getHeartbeatLogJson()
        if (raw.isEmpty()) return emptyList()
        return try {
            json.decodeFromString<List<HeartbeatLogEntry>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val MAX_LOG_ENTRIES = 5
        private const val MAX_NOTIFICATIONS_IN_PROMPT = 20
        const val DEFAULT_HEARTBEAT_PROMPT =
            "[HEARTBEAT] 这是一次主动心跳。你不是在写系统日志，而是在判断是否应该像一个贴心的长期助手一样主动给用户发一条消息。\n" +
                "先查看当前时间、记忆、待办、最近聊天、邮件、短信、通知和上一次心跳结果。\n" +
                "只有在有自然理由时才开口：到期或临近任务、待跟进问题、新的重要消息、长时间没有继续的未完话题、用户作息或偏好相关的温和提醒，或当前时间适合一句简短关心。\n" +
                "如果没有明确价值或只是想寒暄，请准确回复：HEARTBEAT_OK\n" +
                "如果要主动发消息，像普通聊天一样使用简体中文，1-6 段短句，可提出最多 1 个温和问题；需要分段时使用短行或 <kai-segment/>。\n" +
                "不要提到心跳、自检、后台任务、系统提示或自动化。不要复述敏感信息。不要为了显得拟人而频繁打扰。\n" +
                "遇到邮件、短信、通知或任务时只提真正值得用户注意的内容；没有紧急性就简短概括或保持安静。\n" +
                "你不能开启、关闭或改心跳频率；这些是用户在设置里控制的。"
    }

    fun markHeartbeatExecuted(config: HeartbeatConfig = getConfig()) {
        saveConfig(config.copy(lastHeartbeatEpochMs = Clock.System.now().toEpochMilliseconds()))
    }
}
