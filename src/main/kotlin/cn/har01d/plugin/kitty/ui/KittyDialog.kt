package cn.har01d.plugin.kitty.ui

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.services.KittyApplicationService
import cn.har01d.plugin.kitty.services.Status
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.panel
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.KeyStroke

class KittyDialog(private val service: KittyApplicationService, private val setting: SettingData) : JDialog() {
    private lateinit var timerLabel: CellBuilder<JLabel>
    private lateinit var imagePanel: ImagePanel

    init {
        title = message("kitty.dialog.name")
        isModal = true
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        contentPane = panel {
            row {
                label("")
                label(message("takeBreak"))
            }
            row {
                val image = getImage()
                resize(image)
                imagePanel = ImagePanel(image)
                component(imagePanel)
            }
            row {
                label("")
                label(message("restTime"))
                timerLabel = label("05:00")
            }
        }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                onCancel()
            }
        })

        rootPane.registerKeyboardAction(
            { onCancel() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
    }

    fun setTime(text: String) {
        timerLabel.component.text = text
    }

    fun refresh() {
        val image = getImage()
        imagePanel.setImage(image)
        resize(image)
        isVisible = true
    }

    private fun resize(image: BufferedImage) {
        setSize(image.width, image.height)
    }

    private fun getImage(): BufferedImage {
        return if (setting.remoteImages) {
            ImageIO.read(URL(setting.imageApi))
        } else {
            val id = ThreadLocalRandom.current().nextInt(20) + 1
            val input = KittyDialog::class.java.classLoader.getResourceAsStream("/images/cat$id.jpeg")
            ImageIO.read(input)
        }
    }

    private fun onCancel() {
        if (service.status != Status.REST) {
            dispose()
        }
    }
}