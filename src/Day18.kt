fun main() {
    data class Cube(val x: Int, val y: Int, val z: Int)

    val sides = listOf(
        Cube(0, 0, 1),
        Cube(0, 0, -1),
        Cube(0, 1, 0),
        Cube(0, -1, 0),
        Cube(1, 0, 0),
        Cube(-1, 0, 0),
    )

    fun parse(input: List<String>): Pair<List<Cube>, List<List<BooleanArray>>> {
        val cubes = input.map { line ->
            line.split(",").map(String::toInt).let { (x, y, z) -> Cube(x, y, z) }
        }

        val maxZ = cubes.maxOf { it.z }
        val maxY = cubes.maxOf { it.y }
        val maxX = cubes.maxOf { it.x }

        val space = List(maxZ + 1) { List(maxY + 1) { BooleanArray(maxX + 1) } }

        for ((x, y, z) in cubes) {
            space[z][y][x] = true
        }

        return cubes to space
    }

    fun part1(input: List<String>): Int {
        val (cubes, space) = parse(input)

        return cubes.sumOf { cube ->
            sides.count { side ->
                space.getOrNull(cube.z + side.z)
                    ?.getOrNull(cube.y + side.y)
                    ?.getOrNull(cube.x + side.x) != true
            }
        }
    }

    fun part2(input: List<String>): Int {
        val (cubes, space) = parse(input)

        val pockets = List(space.size) {
            List(space[0].size) { BooleanArray(space[0][0].size) }
        }

        fun isPocketOrStone(z: Int, y: Int, x: Int, visited: MutableSet<Cube>): Boolean {
            if (z !in pockets.indices || y !in pockets[z].indices || x !in pockets[z][y].indices) {
                return false
            }

            if (space[z][y][x]) {
                return true
            }

            // outer edge
            if (z == 0 || z == pockets.lastIndex || y == 0 || y == pockets[z].lastIndex || x == 0 || x == pockets[z][y].lastIndex) {
                return false
            }

            val cube = Cube(x, y, z)

            if (cube in visited) {
                return true
            }

            visited += cube

            return sides.all { (dz, dy, dx) ->
                isPocketOrStone(z + dz, y + dy, x + dx, visited)
            }
        }


        for (z in pockets.indices) {
            for (y in pockets[z].indices) {
                for (x in pockets[z][y].indices) {
                    if (!space[z][y][x] && !pockets[z][y][x]) {
                        val adjacent = mutableSetOf<Cube>()
                        if (isPocketOrStone(z, y, x, adjacent)) {
                            for (pocket in adjacent) {
                                pockets[pocket.z][pocket.y][pocket.x] = true
                            }
                        }
                    }
                }
            }
        }

        return cubes.sumOf { cube ->
            sides.count { side ->
                space.getOrNull(cube.z + side.z)
                    ?.getOrNull(cube.y + side.y)
                    ?.getOrNull(cube.x + side.x) != true &&
                        pockets.getOrNull(cube.z + side.z)
                            ?.getOrNull(cube.y + side.y)
                            ?.getOrNull(cube.x + side.x) != true
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
