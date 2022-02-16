package cryptography

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color

fun main(args: Array<String>) {
    while(true) {
        println("Task (hide, show, exit):")
        val inputString = readLine()!!
        when (inputString) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> {
                hideMessage()
            }
            "show" -> {
                println("Obtaining message from image.")
            }
            else -> {
                println("Wrong task: $inputString")
            }
        }
    }
}

fun hideMessage() {
    println("Input image file:")
    val inputFileName = readLine()!!
    println("Output image file:")
    val outputFileName = readLine()!!
    try {
        val inputFile = File(inputFileName)
        val inputImage: BufferedImage = ImageIO.read(inputFile)
        val outputImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until inputImage.width) {
            for (y in 0 until inputImage.height) {
                val color = Color(inputImage.getRGB(x, y))
                val newColor = Color(color.red or 1, color.green or 1, color.blue or 1)
                outputImage.setRGB(x, y, newColor.rgb)
            }
        }
        val outputFile = File(outputFileName)
        ImageIO.write(outputImage, "png", outputFile)
        println("Input Image: $inputFileName")
        println("Output Image: $outputFileName")
        println("Image $outputFileName is saved.")
    } catch (e: Exception) {
        println("Can't read input file!")
    }
}