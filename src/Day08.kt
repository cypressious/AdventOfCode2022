fun main() {
    fun parseGrid(input: List<String>) = input.map {
        it.asIterable().map { c -> c - '0' }
    }

    fun <T> List<List<T>>.transpose() = this[0].indices.map { i -> map { it[i] } }

    fun List<Int>.runningMax(): List<Int> {
        return listOf(-1) + runningReduce(::maxOf).dropLast(1)
    }

    fun List<Int>.computeDistances(): List<IntArray> {
        var next = IntArray(10) { -1 }

        return map { value ->
            val distances = next
            distances.forEachIndexed { index, d ->
                if (d >= 0) distances[index]++
            }
            val prevOwnValueDistance = distances[value]
            distances[value] = 0
            next = distances.copyOf()
            distances[value] = prevOwnValueDistance
            distances
        }
    }

    fun part1(input: List<String>): Int {
        val grid = parseGrid(input)

        val maxima = buildList {
            add(grid.map { it.runningMax() })
            add(grid.map { it.reversed().runningMax().reversed() })
            add(grid.transpose().map { it.runningMax() }.transpose())
            add(grid.transpose().map { it.reversed().runningMax().reversed() }.transpose())
        }

        var count = 0

        for (x in grid.indices) {
            for (y in grid[x].indices) {
                if (grid[x][y] > maxima.minOf { it[x][y] }) {
                    count++
                }
            }
        }

        return count
    }


    fun part2(input: List<String>): Int {
        val grid = parseGrid(input)

        val allDistanceArrays = buildList {
            add(grid.map { row -> row.computeDistances() })
            add(grid.map { row -> row.reversed().computeDistances().reversed() })
            add(grid.transpose().map { row -> row.computeDistances() }.transpose())
            add(grid.transpose().map { row -> row.reversed().computeDistances().reversed() }.transpose())
        }

        val scores = grid.mapIndexed { y, row ->
            row.mapIndexed { x, value ->
                val directionScores = allDistanceArrays.mapIndexed { d, distanceArrays ->
                    val distances = distanceArrays[y][x]

                    distances.drop(value).filter { it >= 0 }.minOrNull() ?: when (d) {
                        0 -> x
                        1 -> row.lastIndex - x
                        2 -> y
                        else -> grid.lastIndex - y
                    }
                }
                directionScores.reduce(Int::times)
            }
        }

        return scores.maxOf { it.max() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
