fun main() {
    fun parse(line: String) = line
        .split(',')
        .map {
            val (start, end) = it.split('-').map(String::toInt)
            start..end
        }

    fun IntRange.includes(range: IntRange) = range.first in this && range.last in this
    fun IntRange.overlaps(range: IntRange) = range.first in this || range.last in this

    fun part1(input: List<String>): Int {
        return input.count { line ->
            val (a, b) = parse(line)

            b.includes(a) || a.includes(b)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { line ->
            val (a, b) = parse(line)

            b.overlaps(a) || b.includes(a) || a.includes(b)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
