import java.lang.StringBuilder

fun main() {
    class Cpu(val input: List<String>) {
        var cycle = 0
        var instructionPointer = 0
        var instruction: String? = null
        var register = 1
        var cyclesToExecute = 0

        fun beforeCycle(): Boolean {
            cycle++

            if (instruction == null) {
                if (instructionPointer in input.indices) {
                    instruction = input[instructionPointer++]
                    cyclesToExecute = if (instruction!!.startsWith("addx")) {
                        2
                    } else {
                        1
                    }
                } else {
                    return false
                }
            }

            return true
        }

        fun afterCycle() {
            if (--cyclesToExecute == 0) {
                if (instruction!!.startsWith("addx")) {
                    register += instruction!!.substringAfter(" ").toInt()
                }
                instruction = null
            }
        }
    }


    fun part1(input: List<String>): Int {
        val cpu = Cpu(input)
        var sum = 0

        while (cpu.beforeCycle()) {
            if (cpu.cycle % 40 == 20) {
                sum += cpu.cycle * cpu.register
            }

            cpu.afterCycle()
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val cpu = Cpu(input)
        val image = StringBuilder()
        var x = 0

        while (cpu.beforeCycle()) {
            if (x++ in cpu.register - 1..cpu.register + 1) {
                image.append("#")
            } else {
                image.append(".")
            }

            if (x >= 40) {
                image.appendLine()
                x = 0
            }

            cpu.afterCycle()
        }

        print(image)

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    check(part2(testInput) == 0)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
