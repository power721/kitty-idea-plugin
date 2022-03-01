package cn.har01d.plugin.kitty.ui

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected


class SettingDialog(private val model: SettingData) : DialogWrapper(true) {
    private lateinit var remoteCheckbox: CellBuilder<JBCheckBox>

    init {
        title = message("setting.dialog.name")
        setSize(400, 260)
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            checkBox(message("setting.enabled"), model::enabled, comment = message("setting.enabled.tip"))
        }
        titledRow(message("setting.time")) {
            row(message("setting.work.time")) {
                cell {
                    spinner(model::workTime, minValue = 5, maxValue = 120, step = 5)
                    label(message("setting.minute"))
                }
            }
            row(message("setting.rest.time")) {
                cell {
                    spinner(model::restTime, minValue = 1, maxValue = 20, step = 1)
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
                        model::remoteImages,
                        comment = message("setting.remote.image.tip")
                    )
            }
            row(message("setting.images.url")) {
                textField(model::imageApi).enableIf(remoteCheckbox.selected)
            }
        }
    }
}
