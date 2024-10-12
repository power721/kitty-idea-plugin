package cn.har01d.plugin.kitty.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class KittyPreloadingActivity : StartupActivity {
    override fun runActivity(project: Project) {
        ApplicationManager.getApplication().getService(KittyApplicationService::class.java)
    }
}
