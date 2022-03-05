package cn.har01d.plugin.kitty.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

class KittyPreloadingActivity : PreloadingActivity() {
    override fun preload(indicator: ProgressIndicator) {
        ApplicationManager.getApplication().getService(KittyApplicationService::class.java)
    }
}
