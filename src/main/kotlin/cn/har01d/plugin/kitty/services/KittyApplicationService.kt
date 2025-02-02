package cn.har01d.plugin.kitty.services

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.ui.KittyDialog
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class KittyApplicationService {
    private val propertiesComponent: PropertiesComponent = PropertiesComponent.getInstance()
    private val notificationGroup: NotificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("Kitty Notifications")
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
    private val setting: SettingData = SettingData()
    private val dialog: KittyDialog = KittyDialog(this)

    var status: Status = Status.IDLE

    private var workFuture: ScheduledFuture<*>? = null
    private var restFuture: ScheduledFuture<*>? = null
    private var lastWorkTime: Long = 0
    private var countdown: Int = 60

    init {
        propertiesComponent.loadFields(setting)
        start()
    }

    fun getSettings(): SettingData = setting

    fun saveSettings() {
        propertiesComponent.saveFields(setting)
    }

    fun start() {
        restFuture?.cancel(true)
        workFuture?.cancel(true)
        status = Status.IDLE
        if (setting.enabled) {
            work()
        }
    }

    fun cancel() {
        countdown = 0
    }

    fun getNextRestTime(): LocalDateTime {
        return if (lastWorkTime > 0) {
            val time = lastWorkTime + TimeUnit.MINUTES.toMillis(setting.workTime.toLong())
            val instant = Instant.ofEpochSecond(time / 1000)
            LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault())
        } else {
            LocalDateTime.now().plusMinutes(setting.workTime.toLong())
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

    fun extend(minutes: Long) {
        dialog.dispose()
        restFuture?.cancel(true)
        status = Status.WORK
        workFuture = scheduler.schedule(this::rest, minutes, TimeUnit.MINUTES)
    }

    private fun countdown() {
        countdown--
        if (countdown >= 0) {
            dialog.setTime(countdownToString(countdown))
        }
        if (countdown <= 0) {
            dialog.dispose()
            restFuture?.cancel(true)
            work()
        }
    }

    private fun notify(message: String) {
        val notification: Notification = notificationGroup.createNotification(message, NotificationType.INFORMATION)
        notification.notify(null)
    }

    private fun getDelay(): Long {
        val now = System.currentTimeMillis()
        val delay = TimeUnit.MINUTES.toMillis(setting.workTime.toLong())
        val targetTime = lastWorkTime + delay
        return if (targetTime > now) {
            targetTime - now
        } else {
            lastWorkTime = now
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
