package cryptography

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color
import kotlin.math.pow

const val PRE_LAST_INDEX = 1
const val PRE_PRE_LAST_INDEX = 2
const val STOP_NUMBER = 3
const val BITS_IN_BYTES = 8
class Cryptography {
    fun run() {
        while(true) {
            println("Task (hide, show, exit):")
            when (val inputString = readLine()!!) {
                "exit" -> {
                    println("Bye!")
                    break
                }
                "hide" -> {
                    hideMessage()
                }
                "show" -> {
                    showMessage()
                }
                else -> {
                    println("Wrong task: $inputString")
                }
            }
        }
    }

    fun showMessage() {
        println("Input image file:")
        val inputFileName = readLine()!!
        try {
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
                    if (bitCount == BITS_IN_BYTES) {
                        bitCount = 0
                        dataToDecode.add(bitStringToByte(byteRow).toByte())
                        byteRow = ""
                    }
                    if (dataToDecode.size >= STOP_NUMBER.toInt()
                        && dataToDecode[dataToDecode.lastIndex] == STOP_NUMBER.toByte()
                        && dataToDecode[dataToDecode.lastIndex - PRE_LAST_INDEX] == 0.toByte()
                        && dataToDecode[dataToDecode.lastIndex - PRE_PRE_LAST_INDEX] == 0.toByte()) break@outOfLoops
                }
            }
            repeat(STOP_NUMBER) {
                dataToDecode.removeLast()
            }
            println("Message:")
            println(dataToDecode.toByteArray().toString(Charsets.UTF_8))
        } catch (e: Exception) {
            println("Can't read input file!")
        }
    }

    fun hideMessage() {
        println("Input image file:")
        val inputFileName = readLine()!!
        println("Output image file:")
        val outputFileName = readLine()!!
        println("Message to hide:")
        val messageToHide = readLine()!!.encodeToByteArray()
        val dataToEncode = encodeByteArray(messageToHide)
        try {
            val inputFile = File(inputFileName)
            val inputImage: BufferedImage = ImageIO.read(inputFile)
            if (inputImage.width * inputImage.height < dataToEncode.size * 8) {
                println("The input image is not large enough to hold this message.")
                return
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
                    if (bitIndex == BITS_IN_BYTES) {
                        bitIndex = 0
                        byteCount++
                    }
                    //if (byteCount >= dataToEncode.size) break@outOfLoops

                }
            }
            val outputFile = File(outputFileName)
            ImageIO.write(outputImage, "png", outputFile)
            println("Input Image: $inputFileName")
            println("Output Image: $outputFileName")
            println("Message saved in $outputFileName image.")
        } catch (e: Exception) {
            println("Can't read input file!")
        }
    }

    fun encodeByteArray(inputArray: ByteArray): ByteArray {
        val outputArray = ByteArray(inputArray.size + STOP_NUMBER)
        for (i in inputArray.indices) {
            outputArray[i] = inputArray[i]
        }
        outputArray[outputArray.lastIndex - PRE_LAST_INDEX] = 0
        outputArray[outputArray.lastIndex - PRE_PRE_LAST_INDEX] = 0
        outputArray[outputArray.lastIndex] = STOP_NUMBER.toByte()
        return outputArray
    }

    fun byteToBitString(inputByte: UByte): String {
        var bitString = inputByte.toString(2)
        for (p in bitString.length until BITS_IN_BYTES) {
            bitString = "0$bitString"
        }
        return bitString
    }

    fun replaceLastBit(number: Int, bit: Char): Int {
        var resultString = ""
        val numberString = number.toString(2)
        for (i in numberString.indices) {
            if (i != numberString.lastIndex) {
                resultString += numberString[i]
            } else {
                resultString += bit
            }
        }
        return bitStringToByte(resultString).toInt()
    }

    fun bitStringToByte(inputString: String): UByte {
        var result = 0
        val reversedString = inputString.reversed()
        for (i in reversedString.indices) {
            result += reversedString[i].digitToInt() * 2.toDouble().pow(i).toInt()
        }
        return result.toUByte()
    }
}

fun main() {
    val crypt = Cryptography()
    crypt.run()
}