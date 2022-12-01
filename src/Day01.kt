fun main() {
    fun part1(input: List<String>): Int {
        var max = 0
        var current = 0
        for (line in input) {
            if (line.isBlank()) {
                max = maxOf(max, current)
                current = 0
            } else {
                current += line.toInt()
            }
        }
        max = maxOf(max, current)

        return max
    }

    fun part2(input: List<String>): Int {
        val elves = mutableListOf<Int>()
        var current = 0

        for (line in input) {
            if (line.isBlank()) {
                elves += current
                current = 0
            } else {
                current += line.toInt()
            }
        }
        elves += current

        elves.sortDescending()
        return elves.take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)
    check(part2(testInput) == 45_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
