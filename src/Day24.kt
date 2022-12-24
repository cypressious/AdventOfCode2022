import java.util.*

fun main() {
    val wall = 0b10000
    val u = 0b0001
    val r = 0b0010
    val d = 0b0100
    val l = 0b1000

    fun getStates(input: List<String>): List<Array<IntArray>> {
        val states = mutableListOf<Array<IntArray>>()
        val height = input.size
        val width = input[0].length
        states += Array(height) { y ->
            IntArray(width) { x ->
                when (input[y][x]) {
                    '#' -> wall
                    '^' -> u
                    'v' -> d
                    '<' -> l
                    '>' -> r
                    else -> 0
                }
            }
        }

        while (true) {
            val previous = states.last()

            val newState = Array(height) { y ->
                IntArray(width) { x ->
                    if (previous[y][x] == wall) return@IntArray wall
                    if (y == 0 && x == 1 || y == height - 1 && x == width - 2) return@IntArray 0

                    var value = 0

                    if (previous[y + 1][x] and u != 0) value = value or u
                    if (y == height - 2 && previous[1][x] and u != 0) value = value or u

                    if (previous[y - 1][x] and d != 0) value = value or d
                    if (y == 1 && previous[height - 2][x] and d != 0) value = value or d

                    if (previous[y][x + 1] and l != 0) value = value or l
                    if (x == width - 2 && previous[y][1] and l != 0) value = value or l

                    if (previous[y][x - 1] and r != 0) value = value or r
                    if (x == 1 && previous[y][width - 2] and r != 0) value = value or r

                    value
                }
            }

            if (states[0] contentDeepEquals newState) {
                break
            }

            states += newState
        }

        return states
    }

    data class Node(val x: Int, val y: Int, val tick: Int) {
        var previous: Node? = null

        fun neighbors(state: Array<IntArray>, maxTicks: Int): List<Node> = buildList {
            val nextTick = (tick + 1) % maxTicks

            if (state.getOrNull(y - 1)?.getOrNull(x) == 0) add(copy(y = y - 1, tick = nextTick))
            if (state.getOrNull(y + 1)?.getOrNull(x) == 0) add(copy(y = y + 1, tick = nextTick))
            if (state.getOrNull(y)?.getOrNull(x - 1) == 0) add(copy(x = x - 1, tick = nextTick))
            if (state.getOrNull(y)?.getOrNull(x + 1) == 0) add(copy(x = x + 1, tick = nextTick))
            if (state.getOrNull(y)?.getOrNull(x) == 0) add(copy(tick = nextTick))
        }
    }

    fun shortestPath(
        start: Node,
        states: List<Array<IntArray>>,
        targetY: Int,
        targetX: Int
    ): Pair<Node, Int> {
        val distances = mutableMapOf(start to 0).withDefault { Int.MAX_VALUE }
        val unvisited = PriorityQueue(compareBy<Node>(distances::getValue)).apply {
            add(start)
        }
        val visited = mutableSetOf<Node>()
        val maxTicks = states.size

        while (true) {
            val node = unvisited.poll()
            val state = states[(node.tick + 1) % maxTicks]
            val newDistance = distances.getValue(node) + 1

            val neighbors = node.neighbors(state, maxTicks)

            for (neighbor in neighbors) {

                if (neighbor.y == targetY && neighbor.x == targetX) {
                    return neighbor to newDistance
                }

                if (neighbor !in visited && distances.getValue(neighbor) > newDistance) {
                    unvisited.remove(neighbor)
                    distances[neighbor] = newDistance
                    unvisited.add(neighbor.apply { previous = node })
                }
            }

            visited += node
        }
    }

    fun part1(input: List<String>): Int {
        val states = getStates(input)
        val start = Node(1, 0, 0)
        return shortestPath(start, states, input.lastIndex, input[0].lastIndex - 1).second
    }

    fun part2(input: List<String>): Int {
        val states = getStates(input)
        val start = Node(1, 0, 0)

        val (end, firstDuration) = shortestPath(
            start,
            states,
            input.lastIndex,
            input[0].lastIndex - 1
        )
        val (newStart, secondDuration) = shortestPath(end, states, start.y, start.x)
        val (_, thirdDuration) = shortestPath(newStart, states, end.y, end.x)

        return firstDuration + secondDuration + thirdDuration
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}
