fun main() {
    class Monkey(
        val itemStack: MutableList<Int>,
        val operation: (Long) -> Long,
        val divisibilityTest: Int,
        val throwTargetTrue: Int,
        val throwTargetFalse: Int,
    ) {
        fun throwTargetOf(item: Int) =
            if (item % divisibilityTest == 0) throwTargetTrue else throwTargetFalse
    }

    fun parse(input: List<String>) = input.chunked(7).map { chunk ->
        val (_, operator, val2) = chunk[2].substringAfter("new = ").split(" ")
        val fn: (Long, Long) -> Long = if (operator == "+") Long::plus else Long::times
        val operation = { old: Long ->
            val operand = if (val2 == "old") old else val2.toLong()
            fn(old, operand)
        }

        Monkey(
            chunk[1].substringAfter(": ").split(", ").mapTo(mutableListOf()) { it.toInt() },
            operation,
            chunk[3].substringAfter("by ").toInt(),
            chunk[4].substringAfter("monkey ").toInt(),
            chunk[5].substringAfter("monkey ").toInt(),
        )
    }

    fun part1(input: List<String>): Int {
        val monkeys = parse(input)
        val inspections = IntArray(monkeys.size)

        repeat(20) {
            for ((index, monkey) in monkeys.withIndex()) {
                while (monkey.itemStack.isNotEmpty()) {
                    var item = monkey.itemStack.removeFirst()
                    item = monkey.operation(item.toLong()).toInt()
                    item /= 3
                    val target = monkey.throwTargetOf(item)
                    monkeys[target].itemStack.add(item)
                    inspections[index]++
                }
            }
        }

        inspections.sortDescending()
        return inspections[0] * inspections[1]
    }

    fun part2(input: List<String>): Long {
        val monkeys = parse(input)
        val inspections = LongArray(monkeys.size)
        val commonDivisor = monkeys.fold(1) { acc, monkey -> acc * monkey.divisibilityTest }

        repeat(10000) {
            for ((index, monkey) in monkeys.withIndex()) {
                while (monkey.itemStack.isNotEmpty()) {
                    val item = monkey.itemStack.removeFirst().toLong()
                    val newItem = (monkey.operation(item) % commonDivisor).toInt()
                    check(newItem >= 0) { "value overflew" }
                    val target = monkey.throwTargetOf(newItem)
                    monkeys[target].itemStack.add(newItem)
                    inspections[index]++
                }
            }
        }

        inspections.sortDescending()
        return inspections[0] * inspections[1]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
