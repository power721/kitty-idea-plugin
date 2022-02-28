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
            checkBox(message("enabled"), model::enabled, comment = message("enabled.tip"))
        }
        titledRow(message("time")) {
            row(message("workTime")) {
                cell {
                    spinner(model::workTime, minValue = 5, maxValue = 120, step = 5)
                    label(message("minute"))
                }
            }
            row(message("restTime")) {
                cell {
                    spinner(model::restTime, minValue = 1, maxValue = 20, step = 1)
                    label(message("minute"))
                }
            }
        }
        titledRow(message("imageSource")) {
            row {
                comment(message("imageSource.tip"))
            }
            row {
                remoteCheckbox =
                    checkBox(message("remoteImage"), model::remoteImages, comment = message("remoteImageComment"))
            }
            row(message("imagesUrl")) {
                textField(model::imageApi).enableIf(remoteCheckbox.selected)
            }
        }
    }
}
