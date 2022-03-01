package cn.har01d.plugin.kitty.ui

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.services.KittyApplicationService
import cn.har01d.plugin.kitty.services.Status
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected

class SettingDialog(private val service: KittyApplicationService) : DialogWrapper(true) {
    private val setting: SettingData = service.getSettings()
    private lateinit var remoteCheckbox: CellBuilder<JBCheckBox>

    init {
        title = message("setting.dialog.name")
        setSize(400, 260)
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            checkBox(message("setting.enabled"), setting::enabled, comment = message("setting.enabled.tip"))
        }
        titledRow(message("setting.time")) {
            row(message("setting.work.time")) {
                cell {
                    spinner(setting::workTime, minValue = 5, maxValue = 120, step = 5)
                    label(message("setting.minute"))
                }
            }
            row(message("setting.rest.time")) {
                cell {
                    spinner(setting::restTime, minValue = 1, maxValue = 20, step = 1)
                    label(message("setting.minute"))
                }
            }
        }
        titledRow(message("setting.image.source")) {
            row {
                comment(message("setting.image.source.tip"))
            }
            row {
                remoteCheckbox =
                    checkBox(
                        message("setting.remote.image"),
                        setting::remoteImages,
                        comment = message("setting.remote.image.tip")
                    )
            }
            row(message("setting.images.url")) {
                textField(setting::imageApi).enableIf(remoteCheckbox.selected)
            }
        }
        if (service.status == Status.WORK) {
            row {
                label(message("message.rest.time") + service.getNextRestTime().toString())
            }
        }
    }
}
