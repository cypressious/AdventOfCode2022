fun main() {
    data class P(val x: Int, val y: Int)

    val rocks = listOf(
        // -
        listOf(P(0, 0), P(1, 0), P(2, 0), P(3, 0)),
        // +
        listOf(P(1, 0), P(0, 1), P(1, 1), P(2, 1), P(1, 2)),
        // reverse L
        listOf(P(0, 0), P(1, 0), P(2, 0), P(2, 1), P(2, 2)),
        // reverse I
        listOf(P(0, 0), P(0, 1), P(0, 2), P(0, 3)),
        // square
        listOf(P(0, 0), P(1, 0), P(0, 1), P(1, 1)),
    )

    fun compute(jets: String, maxRocks: Long): Long {
        var jet = 0

        val settled = mutableListOf<BooleanArray>()
        var rockIndex = 0
        var dx = 2
        var dy = 3
        var fallenRocks = 0L


        var lastRock = 0
        var lastSize = 0
        var lastRocks = 0L
        var skipped = 0L


        while (fallenRocks < maxRocks) {

            // very sorry
            if (jet == 0 && rockIndex == 3) {
                val remainingRocks = maxRocks - fallenRocks
                val iterations = remainingRocks / 1715
                skipped = iterations * 2574
                fallenRocks += iterations * 1715

                println("rock: $lastRock, fallen: ${fallenRocks - lastRocks}, growth: ${settled.size - lastSize}")
                lastRock = rockIndex
                lastSize = settled.size
                lastRocks = fallenRocks
            }

            val rock = rocks[rockIndex]

            // jet
            val offset = if (jets[jet] == '>') 1 else -1
            jet = (jet + 1) % jets.length

            var collision = rock.any { p ->
                val newX = p.x + dx + offset
                newX < 0 || newX >= 7 || settled.getOrNull(p.y + dy)?.get(newX) ?: false
            }

            if (!collision) dx += offset

            // falling
            val lowestRockY = rock[0].y + dy - 1

            collision = lowestRockY == -1 || (lowestRockY in settled.indices && rock.any { p ->
                val newY = p.y + dy - 1
                newY in settled.indices && settled[newY][p.x + dx]
            })

            if (!collision) {
                dy--
            } else {
                // settling
                val targetHeight = rock.last().y + dy

                while (settled.lastIndex < targetHeight) {
                    settled += BooleanArray(7)
                }

                for (p in rock) {
                    settled[p.y + dy][p.x + dx] = true
                }


                fallenRocks++
                rockIndex = (rockIndex + 1) % rocks.size
                dx = 2
                dy = settled.lastIndex + 4
            }
        }

        return settled.size + skipped
    }

    fun part1(input: List<String>): Long {
        return compute(input[0], 2022)
    }

    fun part2(input: List<String>): Long {
        return compute(input[0], 1000000000000)
    }


    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day17_test")
//    check(part1(testInput) == 3068L)
//    check(part2(testInput) == 1_514_285_714_288)
//
    val input = readInput("Day17")
//    println(part1(input))
    println(part2(input))
}
