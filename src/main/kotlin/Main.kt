package cryptography

fun main(args: Array<String>) {
    println("Task (hide, show, exit):")
    while(true) {
        val inputString = readLine()!!
        when (inputString) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> {
                println("Hiding message in image.")
            }
            "show" -> {
                println("Obtaining message from image.")
            }
            else -> {
                println("Wrong task: $inputString")
            }
        }
    }

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}