fun main() {
    fun parseStacks(input: List<String>): List<ArrayList<Char>> {
        val splitIndex = input.indexOf("")

        val initialLines = input.take(splitIndex - 1).asReversed()

        val stacksCount = (initialLines[0].length + 1) / 4
        val stacks = (0 until stacksCount).map { arrayListOf<Char>() }

        for (line in initialLines) {
            for (index in stacks.indices) {
                val char = line.getOrNull(index * 4 + 1)
                if (char != null && char != ' ') {
                    stacks[index].add(char)
                }
            }
        }

        return stacks
    }

    fun parseCommands(line: String) = line
        .split(" ")
        .filter { it.all(Char::isDigit) }
        .map(String::toInt)

    fun part1(input: List<String>): String {
        val stacks = parseStacks(input)

        for (line in input.drop(input.indexOf("") + 1)) {
            val (count, from, to) = parseCommands(line)

            repeat(count) {
                stacks[to - 1].add(stacks[from - 1].removeLast())
            }
        }

        return stacks.joinToString("") { it.last().toString() }
    }

    fun part2(input: List<String>): String {
        val stacks = parseStacks(input)

        for (line in input.drop(input.indexOf("") + 1)) {
            val (count, from, to) = parseCommands(line)

            val buffer = mutableListOf<Char>()

            repeat(count) {
                buffer.add(stacks[from - 1].removeLast())
            }

            buffer.reverse()
            stacks[to - 1].addAll(buffer)
        }

        return stacks.joinToString("") { it.last().toString() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
