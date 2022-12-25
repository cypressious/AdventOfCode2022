import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

fun main() {
    val digits = mapOf(
        '2' to 2,
        '1' to 1,
        '0' to 0,
        '-' to -1,
        '=' to -2,
    )

    fun snafuToInt(snafu: String) = snafu
        .reversed()
        .mapIndexed { i, c ->
            5.0.pow(i.toDouble()).toLong() * digits.getValue(c)
        }
        .sum()

    val chars = charArrayOf('=', '-', '0', '1', '2')
    val multipliers = listOf(-2, -1, 0, 1, 2)

    fun intToSnafu(x: Long): String {
        var remainder = x
        val exponent = (ln(x.toDouble()) / ln(5.0)).toInt() + 1
        val result = mutableListOf<Char>()

        for (e in exponent downTo 0) {
            val factor = 5.0.pow(e).toLong()
            val multiplier = multipliers.minBy { abs(remainder - it * factor) }
            remainder -= multiplier * factor
            result.add(chars[multiplier + 2])
        }

        check(remainder == 0L)

        while (result.first() == '0') result.removeAt(0)

        val snafu = String(result.toCharArray())

        check(snafuToInt(snafu) == x)

        return snafu
    }

    fun part1(input: List<String>): String {
        val value = input.sumOf(::snafuToInt)
        return intToSnafu(value)
    }

    check(intToSnafu(1) == "1")
    check(intToSnafu(2) == "2")
    check(intToSnafu(3) == "1=")
    check(intToSnafu(4) == "1-")
    check(intToSnafu(5) == "10")
    check(intToSnafu(6) == "11")
    check(intToSnafu(7) == "12")
    check(intToSnafu(8) == "2=")
    check(intToSnafu(9) == "2-")
    check(intToSnafu(10) == "20")
    check(intToSnafu(15) == "1=0")
    check(intToSnafu(20) == "1-0")
    check(intToSnafu(2022) == "1=11-2")
    check(intToSnafu(12345) == "1-0---0")
    check(intToSnafu(314159265) == "1121-1110-1=0")

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
}
