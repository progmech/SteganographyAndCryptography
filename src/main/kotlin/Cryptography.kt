package cryptography

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color
import kotlin.math.pow

const val BITS_IN_BYTE = 8
const val BIT = 1

class Cryptography {

    val stopSequence = listOf<Byte>(0, 0, 3)
    var messageLength: Int = 0

    fun run() {
        do {
            println("Task (hide, show, exit):")
            val command = readLine()!!
            when (command) {
                "hide" -> hideCommand()
                "show" -> showCommand()
                "exit" -> println("Bye!")
                else -> println("Wrong task: $command")
            }
        } while (command != "exit")
    }

    fun hideCommand() {
        println("Input image file:")
        val inputFileName = readLine()!!
        println("Output image file:")
        val outputFileName = readLine()!!
        println("Message to hide:")
        val messageToHide = readLine()!!.encodeToByteArray()
        println("Password:")
        val password = readLine()!!.encodeToByteArray()
        val encryptedMessage = encodeMessage(messageToHide, password)
        messageLength = encryptedMessage.size
        try {
            if (hide(inputFileName, outputFileName, encryptedMessage)) {
                println("Message saved in $outputFileName image.")
            } else {
                println("The input image is not large enough to hold this message.")
            }
        } catch (e: Exception) {
            println("Can't read input file!")
        }

    }

    fun encodeMessage(messageToHide: ByteArray, password: ByteArray): ByteArray {
        val encryptedMessage = mutableListOf<Byte>()
        var iterator = 0
        for (i in messageToHide.indices) {
            if (iterator > password.lastIndex) {
                iterator = 0
            }
            val encryptedByte = messageToHide[i].toInt() xor password[iterator].toInt()
            iterator++
            encryptedMessage.add(encryptedByte.toByte())
        }
        return  encryptedMessage.toByteArray()
    }

    fun hide(inputFileName: String, outputFileName: String, encryptedMessage: ByteArray): Boolean {
        val dataToEncode = encodeByteArray(encryptedMessage)
        val inputFile = File(inputFileName)
        val inputImage: BufferedImage = ImageIO.read(inputFile)
        if (inputImage.width * inputImage.height < dataToEncode.size * BITS_IN_BYTE) {
            return false
        }
        val outputImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_RGB)
        var byteCount = 0
        var bitIndex =  0
        outOfLoops@ for (y in 0 until inputImage.height) {
            for (x in 0 until inputImage.width) {
                val color = Color(inputImage.getRGB(x, y))
                val newColor = if (byteCount >= dataToEncode.size) {
                    Color(color.red, color.green, color.blue)
                } else {
                    val bits = byteToBitString(dataToEncode[byteCount].toUByte())
                    val blueColor = replaceLastBit(color.blue, bits[bitIndex])
                    Color(color.red, color.green, blueColor)
                }
                outputImage.setRGB(x, y, newColor.rgb)
                bitIndex++
                if (bitIndex == BITS_IN_BYTE) {
                    bitIndex = 0
                    byteCount++
                }
            }
        }
        val outputFile = File(outputFileName)
        ImageIO.write(outputImage, "png", outputFile)
        return true
    }

    fun showCommand() {
        println("Input image file:")
        val inputFileName = readLine()!!
        println("Password:")
        val password = readLine()!!.encodeToByteArray()
        try {
            val message = show(inputFileName)
            val decodedMessage = decodeMessage(message, password)
            println("Message:")
            println(decodedMessage)
        } catch (e: Exception) {
            println("Can't read input file!")
        }
    }

    fun decodeMessage(message: ByteArray, password: ByteArray): String {
        val encryptedMessage = mutableListOf<Byte>()
        var iterator = 0
        for (i in message.indices) {
            if (iterator > password.lastIndex) {
                iterator = 0
            }
            val encryptedByte = message[i].toInt() xor password[iterator].toInt()
            iterator++
            encryptedMessage.add(encryptedByte.toByte())
        }
        return encryptedMessage.toByteArray().toString(Charsets.UTF_8)
    }

    fun show(inputFileName: String): ByteArray {
        val inputFile = File(inputFileName)
        val inputImage: BufferedImage = ImageIO.read(inputFile)
        val dataToDecode = mutableListOf<Byte>()
        var bitCount = 0
        var byteRow = ""
        outOfLoops@ for (y in 0 until inputImage.height) {
            for (x in 0 until inputImage.width) {
                val color = Color(inputImage.getRGB(x, y))
                val blueBits = byteToBitString(color.blue.toUByte())
                byteRow += blueBits.last()
                bitCount++
                if (bitCount == BITS_IN_BYTE) {
                    bitCount = 0
                    dataToDecode.add(bitStringToByte(byteRow).toByte())
                    byteRow = ""
                }

                if(dataToDecode.takeLast(3).containsAll(stopSequence) && dataToDecode.size >= messageLength) {
                    break@outOfLoops
                }
            }
        }
        return dataToDecode.dropLast(3).toByteArray()
    }

    fun encodeByteArray(inputArray: ByteArray): ByteArray {
        return inputArray + stopSequence
    }

    fun byteToBitString(inputByte: UByte): String {
        var bitString = inputByte.toString(2)
        for (p in bitString.length until BITS_IN_BYTE) {
            bitString = "0$bitString"
        }
        return bitString
    }

    fun replaceLastBit(number: Int, bit: Char): Int = number shr BIT shl BIT or bit.digitToInt()

    fun bitStringToByte(inputString: String): UByte {
        var result = 0
        val reversedString = inputString.reversed()
        for (i in reversedString.indices) {
            result += reversedString[i].digitToInt() * 2.toDouble().pow(i).toInt()
        }
        return result.toUByte()
    }
}