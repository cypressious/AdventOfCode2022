enum class Rps(val score: Int) {
    Rock(1), Paper(2), Scissors(3)
}

fun main() {
    val theirRps = mapOf("A" to Rps.Rock, "B" to Rps.Paper, "C" to Rps.Scissors)
    val mineRps = mapOf("X" to Rps.Rock, "Y" to Rps.Paper, "Z" to Rps.Scissors)
    val dominators =
        mapOf(Rps.Rock to Rps.Scissors, Rps.Paper to Rps.Rock, Rps.Scissors to Rps.Paper)

    fun calculateScore(their: Rps, mine: Rps): Int {
        val score = when (their) {
            mine -> 3
            dominators[mine] -> 6
            else -> 0
        }

        return score + mine.score
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val parts = it.split(' ')
            val their = theirRps[parts[0]]!!
            val mine = mineRps[parts[1]]!!

            calculateScore(their, mine)
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val parts = it.split(' ')
            val their = theirRps[parts[0]]!!

            val outcome = parts[1]
            val mine = when (outcome) {
                "X" -> dominators[their]!!
                "Y" -> their
                else -> dominators.entries.first { it.value == their }.key
            }

            calculateScore(their, mine)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
