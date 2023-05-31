package cn.har01d.plugin.kitty.ui

import cn.har01d.plugin.kitty.MyBundle.message
import cn.har01d.plugin.kitty.model.SettingData
import cn.har01d.plugin.kitty.services.KittyApplicationService
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.Gaps
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.KeyStroke

class KittyDialog(private val service: KittyApplicationService) : JDialog() {
    private val setting: SettingData = service.getSettings()
    private lateinit var timerLabel: Cell<JLabel>
    private lateinit var remindLabel: Cell<JLabel>
    private lateinit var imagePanel: ImagePanel

    init {
        title = message("kitty.dialog.name")
        isModal = true
        setLocation(500, 400)
        contentPane = panel {
            row {
                label(message("message.remind")).customize(Gaps(left = 16))
                remindLabel = label(getMessage())
            }
            row {
                val image = getImage()
                resize(image)
                imagePanel = ImagePanel(image)
                cell(imagePanel)
            }
            row {
                label(message("setting.rest.time")).customize(Gaps(left = 16))
                timerLabel = label("05:00")
            }
        }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                onCancel()
            }
        })

        imagePanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                refresh()
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
    }

    fun refreshAndShow() {
        refresh()
        remindLabel.component.text = getMessage()
        isVisible = true
    }

    private fun resize(image: BufferedImage) {
        setSize(image.width, image.height + 120)
    }

    private fun getImage(): BufferedImage {
        return if (!setting.localImages && setting.imageApi.isNotEmpty()) {
            readRemoteImage()
        } else if (setting.imageFolder.isNotEmpty()) {
            readLocalImage()
        } else {
            readDefaultImage()
        }
    }

    private fun readRemoteImage(): BufferedImage {
        return ImageIO.read(URL(setting.imageApi))
    }

    private fun readLocalImage(): BufferedImage {
        val folder = File(setting.imageFolder)
        if (folder.exists()) {
            val images = folder.listFiles { file ->
                val name = file.name.lowercase()
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
            }
            if (images.isNotEmpty()) {
                val id = ThreadLocalRandom.current().nextInt(images.size)
                return ImageIO.read(images[id])
            }
        }
        return readDefaultImage()
    }

    private fun readDefaultImage(): BufferedImage {
        val id = ThreadLocalRandom.current().nextInt(20) + 1
        val input = KittyDialog::class.java.classLoader.getResourceAsStream("images/cat$id.jpeg")
        return ImageIO.read(input)
    }

    private fun getMessage(): String {
        return when (ThreadLocalRandom.current().nextInt(4)) {
            1 -> message("message.move")
            2 -> message("message.drink")
            3 -> message("message.breathe")
            else -> message("message.rest")
        }
    }

    private fun onCancel() {
        service.cancel()
        dispose()
    }
}
