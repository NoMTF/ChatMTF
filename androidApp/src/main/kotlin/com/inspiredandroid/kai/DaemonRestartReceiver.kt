package com.inspiredandroid.kai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.context.GlobalContext

/**
 * Best-effort daemon restoration after reboot or app upgrade. Recent Android
 * versions may still defer foreground-service starts based on battery policy,
 * but when the system allows the broadcast this keeps heartbeat scheduling alive
 * without requiring the user to manually reopen the app.
 */
class DaemonRestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }

        val koin = GlobalContext.getOrNull() ?: return
        val controller = koin.get<DaemonController>()
        if (controller is AndroidDaemonController && controller.shouldAutoStart()) {
            controller.start()
        }
    }
}
