import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Point(val x: Int, val y: Int) {
        fun isHorizontalLine(other: Point) = y == other.y
    }

    class Grid(width: Int, height: Int, val offset: Int) {
        private val grid = List(height) { CharArray(width) { '.' } }

        operator fun get(y: Int, x: Int) = grid[y][x - offset]
        operator fun set(y: Int, x: Int, c: Char) = grid[y].set(x - offset, c)
        fun contains(y: Int, x: Int) = y in grid.indices && (x - offset) in grid[y].indices

        override fun toString() = grid.joinToString("\n") { it.joinToString("") }
    }

    fun parseStructures(input: List<String>) = input.map { line ->
        line
            .split(" -> ")
            .map { it.split(",").map(String::toInt) }
            .map { (x, y) -> Point(x, y) }
    }

    fun buildGrid(structures: List<List<Point>>, hasFloor: Boolean): Grid {
        var minX = Int.MAX_VALUE
        var maxX = 0
        var maxY = 0

        for ((x, y) in structures.flatten()) {
            minX = min(minX, x)
            maxX = max(maxX, x)
            maxY = max(maxY, y)
        }

        if (hasFloor) {
            maxY += 2

            // ¯\_(ツ)_/¯
            val minWidth = maxY * 3
            val diffX = minWidth - (maxX - minX + 1)
            minX -= diffX / 2
            maxX += diffX / 2
        }

        val grid = Grid(maxX - minX + 1, maxY + 1, minX)

        for (structure in structures) {
            for ((from, to) in structure.windowed(2, 1)) {
                if (from.isHorizontalLine(to)) {
                    for (x in min(from.x, to.x)..max(from.x, to.x)) {
                        grid[from.y, x] = '#'
                    }
                } else {
                    for (y in min(from.y, to.y)..max(from.y, to.y)) {
                        grid[y, from.x] = '#'
                    }
                }
            }
        }

        if (hasFloor) {
            for (x in minX..maxX) {
                grid[maxY, x] = '#'
            }
        }

        return grid
    }

    val moveDirections = listOf(Point(0, 1), Point(-1, 1), Point(1, 1))

    fun settle(grid: Grid, hasBottom: Boolean): Int {
        var settled = 0

        outerLoop@ while (grid[0, 500] == '.') {
            var x = 500
            var y = 0

            moveLoop@ while (true) {
                for ((dx, dy) in moveDirections) {
                    val newY = y + dy
                    val newX = x + dx

                    if (!grid.contains(newY, newX)) {
                        // sand leaves grid, no sand will settle anymore
                        if (hasBottom) throw IllegalStateException("sand can't leave grid with floor")
                        return settled
                    }

                    if (grid[newY, newX] == '.') {
                        y = newY
                        x = newX
                        continue@moveLoop
                    }
                }

                //sand settled
                settled++
                grid[y, x] = 'o'
                break@moveLoop
            }
        }

        return settled
    }

    fun part1(input: List<String>): Int {
        val structures = parseStructures(input)
        val grid = buildGrid(structures, false)

        return settle(grid, false)
    }

    fun part2(input: List<String>): Int {
        val structures = parseStructures(input)
        val grid = buildGrid(structures, true)

        return settle(grid, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
