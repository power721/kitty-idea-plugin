package cn.har01d.plugin.kitty.ui

import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

class ImagePanel(private var image: BufferedImage) : JPanel() {

    init {
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    }

    fun setImage(image: BufferedImage) {
        this.image = image
        invalidate()
        repaint()
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(this.image.getWidth(this), this.image.getHeight(this))
    }

    override fun paint(g: Graphics) {
        g.drawImage(image, 0, 0, this)
    }
}
