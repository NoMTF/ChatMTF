package com.inspiredandroid.kai.data

interface StructuredDataStore {
    fun readJson(key: String): String?
    fun writeJson(key: String, json: String)
    fun remove(key: String)
    fun migrateFromSettingsIfNeeded(appSettings: AppSettings)
}

class StructuredDataStoreHolder(val store: StructuredDataStore?)

expect fun createStructuredDataStore(): StructuredDataStore?

object StructuredDataKeys {
    const val CONVERSATIONS = "conversations"
    const val MEMORIES = "memories"
    const val TASKS = "tasks"
    const val EMAIL_PENDING = "email_pending"
    const val SMS_PENDING = "sms_pending"
    const val SMS_DRAFTS = "sms_drafts"
    const val NOTIFICATION_PENDING = "notification_pending"
    const val NOTIFICATION_STORE = "notification_store"
}
