sealed class PacketData : Comparable<PacketData> {
    override fun compareTo(other: PacketData): Int {
        if (this is PacketInt && other is PacketInt) {
            return this.value.compareTo(other.value)
        }

        if (this is PacketList && other is PacketList) {
            for ((l, r) in this.values.zip(other.values)) {
                val comparison = l.compareTo(r)
                if (comparison != 0) return comparison
            }

            return this.values.size.compareTo(other.values.size)
        }

        return this.wrapIfNecessary().compareTo(other.wrapIfNecessary())
    }

    private fun wrapIfNecessary() = when (this) {
        is PacketInt -> PacketList(null, mutableListOf(this))
        is PacketList -> this
    }
}

class PacketList(val parent: PacketList?, val values: MutableList<PacketData> = mutableListOf()) :
    PacketData() {
    override fun toString() = values.joinToString(", ", prefix = "[", postfix = "]")
}

class PacketInt(val value: Int) : PacketData() {
    override fun toString() = "$value"
}

fun main() {
    fun parse(line: String): PacketData {
        var current = PacketList(null)

        var i = 1
        while (i < line.lastIndex) {
            when (val c = line[i++]) {
                '[' -> current = PacketList(current).also { current.values += it }
                ',' -> continue
                ']' -> current = current.parent!!

                in '0'..'9' -> {
                    if (line[i].isDigit()) {
                        current.values += PacketInt(line.substring(i - 1, i + 1).toInt())
                        i++
                    } else {
                        current.values += PacketInt(c - '0')
                    }
                }
            }
        }

        return current
    }

    fun part1(input: List<String>): Int {
        val pairs = input
            .chunked(3)
            .map { (left, right) -> parse(left) to (parse(right)) }

        return pairs
            .withIndex()
            .filter { (_, pair) ->
                val comparison = pair.first.compareTo(pair.second)
                check(comparison != 0)
                comparison < 0
            }
            .sumOf { (i) -> i + 1 }
    }

    fun part2(input: List<String>): Int {
        val p1 = parse("[[2]]")
        val p2 = parse("[[6]]")

        val sorted = input
            .filter { it.isNotBlank() }
            .map(::parse)
            .plus(listOf(p1, p2))
            .sorted()

        return (sorted.indexOf(p1) + 1) * (sorted.indexOf(p2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
