package cn.har01d.plugin.kitty.actions

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.services.KittyApplicationService
import cn.har01d.plugin.kitty.ui.SettingDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class KittySettingAction : AnAction(message("name")) {
    override fun actionPerformed(e: AnActionEvent) {
        val applicationService = ApplicationManager.getApplication().getService(KittyApplicationService::class.java)
        val dialog = SettingDialog(applicationService)
        if (dialog.showAndGet()) {
            applicationService.saveSettings()
            applicationService.start()
        }
    }
}
