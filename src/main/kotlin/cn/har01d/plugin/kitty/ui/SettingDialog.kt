package cn.har01d.plugin.kitty.ui

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.services.KittyApplicationService
import cn.har01d.plugin.kitty.services.Status
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not

class SettingDialog(private val service: KittyApplicationService) : DialogWrapper(true) {
    private val setting: SettingData = service.getSettings()
    private lateinit var localCheckbox: Cell<JBCheckBox>

    init {
        title = message("setting.dialog.name")
        setSize(400, 260)
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            checkBox(message("setting.enabled"))
                    .comment(message("setting.enabled.tip"))
                    .bindSelected(setting::enabled)
        }
        group(message("setting.time")) {
            row(message("setting.work.time")) {
                spinner(5..120, 5).bindIntValue(setting::workTime)
                label(message("setting.minute"))
            }
            row(message("setting.rest.time")) {
                spinner(1..20).bindIntValue(setting::restTime)
                label(message("setting.minute"))
            }
        }
        group(message("setting.image.source")) {
            row {
                localCheckbox = checkBox(message("setting.local.image"))
                        .comment(message("setting.image.source.tip"))
                        .bindSelected(setting::localImages)

            }
            row(message("setting.images.folder")) {
                textFieldWithBrowseButton(message("message.image.select.title"), null, FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
                    it.path
                }
                        .enabledIf(localCheckbox.selected)
                        .bindText(setting::imageFolder)
            }
            row(message("setting.images.url")) {
                textField()
                        .enabledIf(localCheckbox.selected.not())
                        .bindText(setting::imageApi)
            }
        }
        if (service.status == Status.WORK) {
            row {
                label(message("message.rest.time") + service.getNextRestTime().toString())
            }
        }
    }
}
