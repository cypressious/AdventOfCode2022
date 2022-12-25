import java.util.*

fun main() {
    class Node(
        val height: Int,
        val neighbors: MutableSet<Node> = mutableSetOf(),
    ) : Comparable<Node> {
        var distance = Int.MAX_VALUE

        override fun compareTo(other: Node) = distance.compareTo(other.distance)
    }

    fun convertHeight(height: Char) = when (height) {
        'S' -> 0
        'E' -> 'z' - 'a'
        else -> height - 'a'
    }

    fun parse(input: List<String>, invert: Boolean): Triple<Node, Node, List<Node>> {
        lateinit var start: Node
        lateinit var end: Node
        val graph = input.map { line ->
            line.toCharArray().map { height ->
                Node(convertHeight(height)).also {
                    if (height == 'S') start = it; if (height == 'E') end = it
                }
            }
        }

        for (y in graph.indices) {
            for (x in graph[y].indices) {
                val node = graph[y][x]

                fun addEdgeIfAllowed(y: Int, x: Int) {
                    val neighbor = graph.getOrNull(y)?.getOrNull(x) ?: return

                    if (neighbor.height <= node.height + 1) {
                        if (!invert) {
                            node.neighbors += neighbor
                        } else {
                            neighbor.neighbors += node
                        }
                    }
                }

                addEdgeIfAllowed(y - 1, x)
                addEdgeIfAllowed(y + 1, x)
                addEdgeIfAllowed(y, x - 1)
                addEdgeIfAllowed(y, x + 1)
            }
        }

        return Triple(start, end, graph.flatten())
    }

    fun part1(input: List<String>): Int {
        val (start, end) = parse(input, false)
        start.distance = 0
        val unvisited = PriorityQueue<Node>().apply { add(start) }
        val visited = mutableSetOf<Node>()

        while (unvisited.isNotEmpty()) {
            val node = unvisited.poll()
            for (neighbor in node.neighbors) {
                if (neighbor !in visited && neighbor.distance > node.distance + 1) {
                    unvisited.remove(neighbor)
                    neighbor.distance = node.distance + 1
                    unvisited.add(neighbor)

                    if (neighbor == end) return neighbor.distance
                }
            }
            visited += node
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val (_, end, graph) = parse(input, true)
        end.distance = 0
        val unvisited = PriorityQueue<Node>().apply { add(end) }
        val visited = mutableSetOf<Node>()

        while (unvisited.isNotEmpty()) {
            val node = unvisited.poll()
            for (neighbor in node.neighbors) {
                if (neighbor !in visited && neighbor.distance > node.distance + 1) {
                    unvisited.remove(neighbor)
                    neighbor.distance = node.distance + 1
                    unvisited.add(neighbor)
                }
            }
            visited += node
        }

        return graph.filter { it.height == 0 }.minOf { it.distance }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
