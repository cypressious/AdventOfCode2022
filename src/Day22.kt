fun main() {
    data class Point(val x: Int, val y: Int)

    val mapChars = charArrayOf('.', '#')

    data class Direction(val dx: Int, val dy: Int, val value: Int) {
        fun next(x: Int, y: Int, map: List<String>): Point {
            var newX = x + dx
            var newY = y + dy

            if (dx == 1 && (newX > map[newY].lastIndex || map[newY][newX] == ' '))
                newX = map[y].indexOfAny(mapChars)
            else if (dx == -1 && (newX < 0 || map[newY][newX] == ' '))
                newX = map[y].lastIndexOfAny(mapChars)
            else if (dy == 1 && (newY > map.lastIndex || x !in map[newY].indices || map[newY][newX] == ' '))
                newY = map.indexOfFirst { x in it.indices && it[newX] != ' ' }
            else if (dy == -1 && (newY < 0 || x !in map[newY].indices || map[newY][newX] == ' '))
                newY = map.indexOfLast { x in it.indices && it[newX] != ' ' }

            return Point(newX, newY)
        }
    }

    data class Instruction(val steps: Int, val rotation: Int)

    val regex = """(\d+)(\w)?""".toRegex()

    val r = Direction(1, 0, 0)
    val d = Direction(0, 1, 1)
    val l = Direction(-1, 0, 2)
    val u = Direction(0, -1, 3)

    val directions = listOf(r, d, l, u)

    data class Edge(val face: Int, val direction: Direction)

    data class MapConfig(
        val tileSize: Int,
        val faces: Map<Int, Point>,
        val transitions: Map<Edge, Edge>
    )

    fun parse(input: List<String>): Pair<List<String>, List<Instruction>> {
        val map = input.dropLast(2)
        val instructions = regex
            .findAll(input.last())
            .map {
                val rotation = when (it.groupValues[2]) {
                    "R" -> 1
                    "L" -> -1
                    else -> 0
                }
                Instruction(it.groupValues[1].toInt(), rotation)
            }.toList()
        return Pair(map, instructions)
    }

    fun part1(input: List<String>): Int {
        val (map, instructions) = parse(input)

        var y = 0
        var x = map[y].indexOf('.')
        var dirIndex = 0

        for ((steps, rotation) in instructions) {
            val dir = directions[dirIndex]

            for (s in 1..steps) {
                val (newX, newY) = dir.next(x, y, map)
                if (map[newY][newX] == '#') break
                x = newX
                y = newY
            }

            dirIndex = Math.floorMod(dirIndex + rotation, directions.size)
        }

        return 1000 * (y + 1) + 4 * (x + 1) + directions[dirIndex].value
    }

    fun part2(input: List<String>, mapConfig: MapConfig): Int {
        val (size, faces, transitions) = mapConfig
        val (map, instructions) = parse(input)
        val cube = (1..6).associateWith { face ->
            val (x, y) = faces.getValue(face)
            map.subList(y * size, (y + 1) * size)
                .map { it.substring(x * size, (x + 1) * size) }
        }

        var y = 0
        var x = 0
        var face = 1
        var dirIndex = 0

        fun convert(x: Int, y: Int, oldDir: Direction, newDir: Direction): Point {
            var newX = Math.floorMod(x, size)
            var newY = Math.floorMod(y, size)

            @Suppress("CascadeIf")
            if (newDir == oldDir) {
                // do nothing
            } else if (newDir == r) {
                newY = when (oldDir) {
                    l -> size - newY - 1
                    d -> size - newX - 1
                    u -> newX
                    else -> throw IllegalArgumentException()
                }
                newX = 0
            } else if (newDir == l) {
                newY = when (oldDir) {
                    r -> size - newY - 1
                    d -> newX
                    u -> size - newX - 1
                    else -> throw IllegalArgumentException()
                }
                newX = size - 1
            } else if (newDir == d) {
                newX = when (oldDir) {
                    r -> size - newY - 1
                    l -> newY
                    u -> size - newX - 1
                    else -> throw IllegalArgumentException()
                }
                newY = 0
            } else if (newDir == u) {
                newX = when (oldDir) {
                    r -> newY
                    d -> size - newX - 1
                    l -> size - newY - 1
                    else -> throw IllegalArgumentException()
                }
                newY = size - 1
            } else {
                throw IllegalArgumentException()
            }

            return Point(newX, newY)
        }

        for ((steps, rotation) in instructions) {
            val dir = directions[dirIndex]
            var (dx, dy) = dir

            for (s in 1..steps) {
                var newY = y + dy
                var newX = x + dx
                var newFace = face
                var newDirIndex = dirIndex

                if (newX !in 0 until size || newY !in 0 until size) {
                    val transition = transitions.getValue(Edge(face, dir))
                    newDirIndex = directions.indexOf(transition.direction)
                    newFace = transition.face

                    val p = convert(newX, newY, dir, transition.direction)
                    newX = p.x
                    newY = p.y
                }

                if (cube.getValue(newFace)[newY][newX] == '#') break
                x = newX
                y = newY
                face = newFace
                dirIndex = newDirIndex
                directions[dirIndex].let { (dxx, dyy) -> dx = dxx; dy = dyy }
            }

            dirIndex = Math.floorMod(dirIndex + rotation, directions.size)
        }

        val offset = faces.getValue(face)

        return 1000 * (offset.y * size + y + 1) + 4 * (offset.x * size + x + 1) + directions[dirIndex].value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 6032)
    check(
        part2(
            testInput, MapConfig(
                4,
                mapOf(
                    1 to Point(2, 0),
                    2 to Point(0, 1),
                    3 to Point(1, 1),
                    4 to Point(2, 1),
                    5 to Point(2, 2),
                    6 to Point(3, 2),
                ),
                mapOf(
                    Edge(1, r) to Edge(6, l),
                    Edge(1, d) to Edge(4, d),
                    Edge(1, l) to Edge(3, d),
                    Edge(1, u) to Edge(2, d),

                    Edge(2, r) to Edge(3, r),
                    Edge(2, d) to Edge(5, u),
                    Edge(2, l) to Edge(6, u),
                    Edge(2, u) to Edge(1, d),

                    Edge(3, r) to Edge(4, r),
                    Edge(3, d) to Edge(5, r),
                    Edge(3, l) to Edge(2, l),
                    Edge(3, u) to Edge(1, r),

                    Edge(4, r) to Edge(6, d),
                    Edge(4, d) to Edge(5, d),
                    Edge(4, l) to Edge(3, l),
                    Edge(4, u) to Edge(1, u),

                    Edge(5, r) to Edge(6, r),
                    Edge(5, d) to Edge(2, u),
                    Edge(5, l) to Edge(3, u),
                    Edge(5, u) to Edge(4, u),

                    Edge(6, r) to Edge(1, l),
                    Edge(6, d) to Edge(2, r),
                    Edge(6, l) to Edge(5, l),
                    Edge(6, u) to Edge(4, l),
                )
            )
        ) == 5031
    )

    val input = readInput("Day22")
    println(part1(input))
    println(
        part2(
            input, MapConfig(
                50,
                mapOf(
                    1 to Point(1, 0),
                    2 to Point(2, 0),
                    3 to Point(1, 1),
                    4 to Point(0, 2),
                    5 to Point(1, 2),
                    6 to Point(0, 3),
                ),
                mapOf(
                    Edge(1, r) to Edge(2, r),
                    Edge(1, d) to Edge(3, d),
                    Edge(1, l) to Edge(4, r),
                    Edge(1, u) to Edge(6, r),

                    Edge(2, r) to Edge(5, l),
                    Edge(2, d) to Edge(3, l),
                    Edge(2, l) to Edge(1, l),
                    Edge(2, u) to Edge(6, u),

                    Edge(3, r) to Edge(2, u),
                    Edge(3, d) to Edge(5, d),
                    Edge(3, l) to Edge(4, d),
                    Edge(3, u) to Edge(1, u),

                    Edge(4, r) to Edge(5, r),
                    Edge(4, d) to Edge(6, d),
                    Edge(4, l) to Edge(1, r),
                    Edge(4, u) to Edge(3, r),

                    Edge(5, r) to Edge(2, l),
                    Edge(5, d) to Edge(6, l),
                    Edge(5, l) to Edge(4, l),
                    Edge(5, u) to Edge(3, u),

                    Edge(6, r) to Edge(5, u),
                    Edge(6, d) to Edge(2, d),
                    Edge(6, l) to Edge(1, d),
                    Edge(6, u) to Edge(4, u),
                ),
            )
        )
    )
}
