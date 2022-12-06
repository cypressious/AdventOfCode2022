fun main() {
    fun compute(line: String, size: Int): Int {
        var substring = line.take(size)
        var index = size

        while (true) {
            if (substring.toCharArray().distinct().size == size) {
                return index
            }

            substring = substring.substring(1) + line[index]
            index++
        }
    }

    fun part1(input: List<String>) = compute(input.single(), 4)

    fun part2(input: List<String>) = compute(input.single(), 14)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
