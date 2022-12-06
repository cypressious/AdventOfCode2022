fun main() {
    fun Char.prio() = if (isUpperCase()) {
        52 - ('Z' - this)
    } else {
        26 - ('z' - this)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val first = it.substring(0, it.length / 2)
            val second = it.substring(it.length / 2)

            first.first { c -> c in second }.prio()
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { (a,b,c) ->
            a.first { it in b && it in c }.prio()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
