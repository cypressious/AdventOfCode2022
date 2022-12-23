fun main() {
    data class P(val x: Int, val y: Int) {
        operator fun plus(p: P) = P(x + p.x, y + p.y)
    }

    class Move(val check: List<P>, val move: P, val bit: Int)

    val n = P(0, -1)
    val s = P(0, 1)
    val w = P(-1, 0)
    val e = P(1, 0)

    val allDirections = listOf(
        n, n + e, e, s + e, s, s + w, w, n + w,
    )

    val movesMask = 0b1111
    val elfBit = 0b10000

    val bitMeanings = Array(16) { P(0, 0) }.apply {
        set(0b0001, s)
        set(0b0010, n)
        set(0b0100, e)
        set(0b1000, w)
    }

    val moves = listOf(
        Move(listOf(n, n + e, n + w), n, 0b0001),
        Move(listOf(s, s + e, s + w), s, 0b0010),
        Move(listOf(w, n + w, s + w), w, 0b0100),
        Move(listOf(e, n + e, s + e), e, 0b1000),
    )

    fun parse(
        input: List<String>,
        iterations: Int
    ): List<IntArray> {
        val width = input[0].length
        val height = input.size

        val map = List(height + iterations * 2) { y ->
            IntArray(width + iterations * 2) { x ->
                if (input.getOrNull(y - iterations)?.getOrNull(x - iterations) == '#') {
                    elfBit
                } else {
                    0
                }
            }
        }
        return map
    }

    fun part1(input: List<String>): Int {
        val iterations = 10
        val map = parse(input, iterations)

        for (i in 0 until iterations) {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] and elfBit == 0) continue
                    if (allDirections.all { (dx, dy) -> map[y + dy][x + dx] != elfBit }) continue

                    for (moveIndex in moves.indices) {
                        val move = moves[(moveIndex + i) % moves.size]

                        if (move.check.all { (dx, dy) -> map[y + dy][x + dx] != elfBit }) {
                            val (dx, dy) = move.move
                            map[y + dy][x + dx] = map[y + dy][x + dx] or move.bit
                            break
                        }
                    }
                }
            }
            for (y in map.indices) {
                for (x in map[y].indices) {
                    val value = map[y][x]
                    if (value and movesMask == 0) continue

                    if (value.countOneBits() == 1) {
                        val (dx, dy) = bitMeanings[value]
                        map[y + dy][x + dx] = 0
                        map[y][x] = elfBit
                    } else {
                        map[y][x] = 0
                    }
                }
            }
        }

        val elfCount = input.sumOf { it.count { c -> c == '#' } }
        val yMin = map.indexOfFirst { it.contains(elfBit) }
        val yMax = map.indexOfLast { it.contains(elfBit) }
        val xMin = map[0].indices.indexOfFirst { x -> map.any { it[x] == elfBit } }
        val xMax = map[0].indices.indexOfLast { x -> map.any { it[x] == elfBit } }

        return (yMax - yMin + 1) * (xMax - xMin + 1) - elfCount
    }

    fun part2(input: List<String>, maxIterations: Int): Int {
        val offset = P(maxIterations, maxIterations)
        val elves = mutableSetOf<P>()

        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == '#') elves += P(x, y) + offset
            }
        }

        val map = parse(input, maxIterations)
        val proposed = mutableSetOf<P>()

        for (i in 0 until maxIterations) {
            var hasMoved = false
            proposed.clear()

            for ((x, y) in elves) {
                if (map[y][x] and elfBit == 0) throw IllegalStateException("wrong elf location")
                if (allDirections.all { (dx, dy) -> map[y + dy][x + dx] != elfBit }) continue

                for (moveIndex in moves.indices) {
                    val move = moves[(moveIndex + i) % moves.size]

                    if (move.check.all { (dx, dy) -> map[y + dy][x + dx] != elfBit }) {
                        val (dx, dy) = move.move
                        map[y + dy][x + dx] = map[y + dy][x + dx] or move.bit
                        proposed += P(x + dx, y + dy)
                        break
                    }
                }
            }

            for ((x, y) in proposed) {
                val value = map[y][x]
                if (value and movesMask == 0) continue

                if (value.countOneBits() == 1) {
                    val (dx, dy) = bitMeanings[value]
                    map[y + dy][x + dx] = 0
                    elves -= P(x + dx, y + dy)

                    map[y][x] = elfBit
                    elves += P(x, y)

                    hasMoved = true
                } else {
                    map[y][x] = 0
                }
            }

            if (!hasMoved) return i + 1
        }

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
    check(part2(testInput, 20) == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input, 10000))
}
