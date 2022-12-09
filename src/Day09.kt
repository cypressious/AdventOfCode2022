import kotlin.math.abs

fun main() {

    data class Position(val x: Int, val y: Int) {
        operator fun plus(dir: String) = when (dir) {
            "R" -> Position(x + 1, y)
            "L" -> Position(x - 1, y)
            "U" -> Position(x, y + 1)
            else -> Position(x, y - 1)
        }
    }

    data class Move(val dir: String, val steps: Int)

    val startingPos = Position(0, 0)

    fun parseMoves(input: List<String>): List<Move> {
        return input.map { it.split(" ").let { (dir, steps) -> Move(dir, steps.toInt()) } }
    }

    fun updateTail(head: Position, tail: Position): Position {
        val diffX = head.x - tail.x
        val absX = abs(diffX)
        val diffY = head.y - tail.y
        val absY = abs(diffY)

        return when {
            //diagonal step
            absX > 1 && absY > 0 || absY > 1 && absX > 0 -> Position(tail.x + diffX / absX, tail.y + diffY / absY)
            absX > 1 -> Position(tail.x + diffX / absX, tail.y)
            absY > 1 -> Position(tail.x, tail.y + diffY / absY)
            else -> tail
        }
    }

    fun part1(input: List<String>): Int {
        val moves = parseMoves(input)

        var head = startingPos
        var tail = startingPos
        val visited = mutableSetOf(tail)

        for (move in moves) {
            repeat(move.steps) {
                head += move.dir
                tail = updateTail(head, tail)
                visited += tail
            }
        }

        return visited.size
    }

    fun part2(input: List<String>): Int {
        val moves = parseMoves(input)

        val rope = MutableList(10) { startingPos }
        val visited = mutableSetOf(rope.last())

        for (move in moves) {
            repeat(move.steps) {
                rope[0] = rope[0] + move.dir

                for (i in 1..rope.lastIndex) {
                    rope[i] = updateTail(rope[i - 1], rope[i])
                }

                visited += rope.last()
            }
        }

        return visited.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val testInput2 = readInput("Day09_test2")
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
