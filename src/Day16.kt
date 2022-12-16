import java.util.*
import java.util.stream.IntStream
import kotlin.math.pow

fun main() {
    val regex = """Valve (\w+) has flow rate=(\d+); \w+ \w+ to \w+ (.+)""".toRegex()

    class Valve(
        val name: String,
        val rate: Int,
        val tunnels: MutableList<Valve> = mutableListOf()
    ) {
        val distances = mutableMapOf<Valve, Int>()

        override fun toString(): String {
            return "Valve(name='$name', rate=$rate, tunnels=${tunnels.joinToString { it.name }})"
        }
    }

    fun calculateDistances(valve: Valve) {
        val distances = mutableMapOf(valve to 0).withDefault { Int.MAX_VALUE }
        val unvisited = PriorityQueue(compareBy(distances::getValue)).apply { add(valve) }
        val visited = mutableSetOf<Valve>()

        while (unvisited.isNotEmpty()) {
            val v = unvisited.poll()

            for (neighbor in v.tunnels) {
                if (neighbor !in visited && distances.getValue(neighbor) > distances.getValue(v) + 1) {
                    unvisited -= neighbor
                    distances[neighbor] = distances.getValue(v) + 1
                    unvisited += neighbor
                }
            }

            visited += v
        }

        valve.distances.putAll(distances)
    }

    fun parse(input: List<String>): Map<String, Valve> {
        val valves = mutableMapOf<String, Valve>()
        val allTunnels = mutableMapOf<String, List<String>>()

        for (line in input) {
            val (name, rate, tunnels) = regex.matchEntire(line)!!.destructured
            valves[name] = Valve(name, rate.toInt())
            allTunnels[name] = tunnels.split(", ")
        }

        for ((name, tunnels) in allTunnels) {
            for (tunnel in tunnels) {
                valves.getValue(name).tunnels.add(valves.getValue(tunnel))
            }
        }

        for (valve in valves.values) {
            calculateDistances(valve)
        }

        return valves
    }

    fun calculateMaxRelease(start: Valve, maxTime: Int, nonZeroValves: List<Valve>): Int {
        val visited = mutableSetOf<String>()
        val cache = mutableMapOf<Triple<String, Int, String>, Int>()

        fun getMaxRelease(current: Valve, minute: Int): Int {
            if (visited.size == nonZeroValves.size) return 0
            if (minute >= maxTime) return 0

            val key = Triple(current.name, minute, visited.sorted().joinToString(","))
            cache[key]?.let { return it }

            var max = 0

            for (valve in nonZeroValves) {
                if (valve.name in visited) continue

                val distance = current.distances.getValue(valve)

                if (minute + distance >= maxTime) continue

                visited += valve.name

                val release = valve.rate * (maxTime - minute - distance)
                val candidate = release + getMaxRelease(valve, minute + distance + 1)

                max = maxOf(max, candidate)

                visited -= valve.name
            }

            if (max > 0) cache[key] = max

            return max
        }

        return getMaxRelease(start, 1)
    }

    fun part1(input: List<String>): Int {
        val valves = parse(input)
        val start = valves.getValue("AA")
        return calculateMaxRelease(start, 30, valves.values.filter { it.rate > 0 })
    }

    fun part2(input: List<String>): Int {
        val valves = parse(input)
        val start = valves.getValue("AA")
        val nonZeroValves = valves.values.filter { it.rate > 0 }

        // https://stackoverflow.com/a/6999554/615306
        return IntStream.range(1, 2.0.pow(nonZeroValves.size - 1).toInt())
            .parallel()
            .map { mask ->
                val yours = mutableListOf<Valve>()
                val elephant = mutableListOf<Valve>()

                for ((index, valve) in nonZeroValves.withIndex()) {
                    if (mask and (1 shl index) != 0) {
                        yours.add(valve)
                    } else {
                        elephant.add(valve)
                    }
                }

                calculateMaxRelease(start, 26, yours) + calculateMaxRelease(start, 26, elephant)
            }
            .max()
            .asInt
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
