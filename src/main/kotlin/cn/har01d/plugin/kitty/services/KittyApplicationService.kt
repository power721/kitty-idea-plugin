package cn.har01d.plugin.kitty.services

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.ui.KittyDialog
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationBuilder
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class KittyApplicationService {
    private val scheduler = Executors.newScheduledThreadPool(2)
    private val setting: SettingData = SettingData()
    private val dialog: KittyDialog = KittyDialog(this)

    var status: Status = Status.IDLE

    private var workFuture: ScheduledFuture<*>? = null
    private var restFuture: ScheduledFuture<*>? = null
    private var lastWorkTime: Long = 0
    private var countdown: Int = 60

    init {
        PropertiesComponent.getInstance().loadFields(setting)
        start()
    }

    fun getSettings(): SettingData = setting

    fun saveSettings() {
        PropertiesComponent.getInstance().saveFields(setting)
        start()
    }

    private fun start() {
        restFuture?.cancel(true)
        workFuture?.cancel(true)
        status = Status.IDLE
        if (setting.enabled) {
            work()
        }
    }

    private fun work() {
        status = Status.WORK
        notify(message("message.start.work") + " ${LocalDateTime.now()}")
        workFuture = scheduler.schedule(this::rest, getDelay(), TimeUnit.MILLISECONDS)
    }

    private fun rest() {
        status = Status.REST
        notify(message("message.rest.now") + " ${LocalDateTime.now()}")
        countdown = setting.restTime * 60
        restFuture = scheduler.scheduleAtFixedRate(this::countdown, 0, 1, TimeUnit.SECONDS)
        dialog.setTime(countdownToString(countdown))
        dialog.refreshAndShow()
    }

    private fun countdown() {
        countdown--
        dialog.setTime(countdownToString(countdown))
        if (countdown <= 0) {
            dialog.dispose()
            restFuture?.cancel(true)
            work()
        }
    }

    private fun notify(message: String) {
        val notification =
            NotificationBuilder("Kitty Notifications", message, NotificationType.INFORMATION).build()
        Notifications.Bus.notify(notification)
    }

    private fun getDelay(): Long {
        val now = System.currentTimeMillis()
        val delay = TimeUnit.MINUTES.toMillis(setting.workTime.toLong())
        val targetTime = lastWorkTime + delay
        lastWorkTime = now
        return if (targetTime - now > 1000L) {
            targetTime - now
        } else {
            delay
        }
    }

    private fun countdownToString(countdown: Int): String {
        val minutes = countdown / 60
        val seconds = countdown % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

enum class Status {
    IDLE,
    WORK,
    REST
}
