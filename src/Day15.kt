import kotlin.math.abs

fun main() {

    data class Point(val x: Int, val y: Int) {
        infix fun distanceTo(beacon: Point) = distanceTo(beacon.x, beacon.y)
        fun distanceTo(xx: Int, yy: Int) = abs(x - xx) + abs(y - yy)
    }

    data class Sensor(val position: Point, val closestBeacon: Point) {
        val radius = position distanceTo closestBeacon
    }

    val regex = "Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)".toRegex()

    fun parse(input: List<String>) = input.map {
        val (sx, sy, bx, by) = regex.matchEntire(it)!!.destructured.toList().map(String::toInt)
        Sensor(Point(sx, sy), Point(bx, by))
    }

    fun part1(input: List<String>, row: Int): Int {
        val sensors = parse(input)

        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE

        for (sensor in sensors) {
            minX = minOf(minX, sensor.position.x - sensor.radius)
            maxX = maxOf(maxX, sensor.position.x + sensor.radius)
            minY = minOf(minY, sensor.position.y - sensor.radius)
            maxY = maxOf(maxY, sensor.position.y + sensor.radius)
        }

        return (minX..maxX).count { x ->
            val p = Point(x, row)
            sensors.any { sensor ->
                p != sensor.closestBeacon && sensor.position distanceTo p <= sensor.radius
            }
        }
    }

    fun part2(input: List<String>, max: Int): Long {
        val sensors = parse(input)

        // idea: if there is only one point not covered by the signal of any sensor,
        // it must lie next to the edge of some sensor's signal. iterate over all sensors,
        // walk outside its edge and check (conventionally) if the point is outside the
        // radius of all sensors.
        for (sensor in sensors) {
            val (sx, sy) = sensor.position
            val walkingDistance = sensor.radius + 1
            val fromX = (sx - walkingDistance).coerceAtLeast(0)
            val toX = (sx + walkingDistance).coerceAtMost(max)

            for (x in fromX..toX) {
                val dx = abs(x - sx)
                val dy = walkingDistance - dx

                // y1 walks along the top half of the "circle"
                val y1 = sy - dy
                if (y1 in 0..max && sensors.all { it.position.distanceTo(x, y1) > it.radius }) {
                    return x * 4000000L + y1
                }

                // y2 walks along the bottom half of the "circle"
                val y2 = sy + dy
                if (y2 in 0..max && sensors.all { it.position.distanceTo(x, y2) > it.radius }) {
                    return x * 4000000L + y2
                }
            }
        }

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}
