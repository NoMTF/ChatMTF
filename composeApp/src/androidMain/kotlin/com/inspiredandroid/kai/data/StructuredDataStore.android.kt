package com.inspiredandroid.kai.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.koin.java.KoinJavaComponent.inject

private const val DB_NAME = "kai_structured.db"
private const val DB_VERSION = 1
private const val TABLE_JSON = "json_store"

actual fun createStructuredDataStore(): StructuredDataStore? {
    val context: Context by inject(Context::class.java)
    return AndroidStructuredDataStore(context.applicationContext)
}

class AndroidStructuredDataStore(context: Context) : StructuredDataStore {
    private val helper = Helper(context)

    override fun readJson(key: String): String? = helper.readableDatabase.query(
        TABLE_JSON,
        arrayOf("value"),
        "key = ?",
        arrayOf(key),
        null,
        null,
        null,
    ).use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }

    override fun writeJson(key: String, json: String) {
        val values = ContentValues().apply {
            put("key", key)
            put("value", json)
            put("updated_at", System.currentTimeMillis())
        }
        helper.writableDatabase.insertWithOnConflict(TABLE_JSON, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun remove(key: String) {
        helper.writableDatabase.delete(TABLE_JSON, "key = ?", arrayOf(key))
    }

    override fun migrateFromSettingsIfNeeded(appSettings: AppSettings) {
        if (appSettings.settings.getBoolean(AppSettings.KEY_STRUCTURED_DATA_MIGRATION_COMPLETE, false)) return
        runCatching {
            appSettings.getConversationsJson()?.takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.CONVERSATIONS, it) }
            appSettings.getMemoriesJson().takeIf { it.isNotBlank() && it != "[]" }?.let { writeJson(StructuredDataKeys.MEMORIES, it) }
            appSettings.getScheduledTasksJson().takeIf { it.isNotBlank() && it != "[]" }?.let { writeJson(StructuredDataKeys.TASKS, it) }
            appSettings.getEmailPendingJson().takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.EMAIL_PENDING, it) }
            appSettings.getSmsPendingJson().takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.SMS_PENDING, it) }
            appSettings.getSmsDraftsJson().takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.SMS_DRAFTS, it) }
            appSettings.getNotificationsPendingJson().takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.NOTIFICATION_PENDING, it) }
            appSettings.getNotificationsStoreJson().takeIf { it.isNotBlank() }?.let { writeJson(StructuredDataKeys.NOTIFICATION_STORE, it) }
            appSettings.settings.putBoolean(AppSettings.KEY_STRUCTURED_DATA_MIGRATION_COMPLETE, true)
        }.onFailure {
            println("StructuredDataStore migration failed: ${it.message}")
        }
    }

    private class Helper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_JSON (
                    key TEXT PRIMARY KEY NOT NULL,
                    value TEXT NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
    }
}
