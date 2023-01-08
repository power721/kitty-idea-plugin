package cn.har01d.plugin.kitty.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PreloadingActivity

class KittyPreloadingActivity : PreloadingActivity() {
    override suspend fun execute() {
        ApplicationManager.getApplication().getService(KittyApplicationService::class.java)
    }
}
