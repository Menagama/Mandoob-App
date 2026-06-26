package com.mandoob.mena

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun detectLogoColor() {
    val file = java.io.File("./src/main/res/drawable/ic_mandoob_logo.png")
    if (file.exists()) {
      val img = javax.imageio.ImageIO.read(file)
      val rgb = img.getRGB(10, 10)
      val color = java.awt.Color(rgb)
      val hex = String.format("#%02X%02X%02X", color.red, color.green, color.blue)
      println("LOGO_COLOR_HEX: $hex")
    } else {
      println("File not found!")
    }
  }
}







