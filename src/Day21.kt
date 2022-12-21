fun main() {
    abstract class Monkey(val name: String)
    class NumberMonkey(name: String, val number: Long) : Monkey(name) {
        override fun toString() = "NumberMonkey(name='$name', number=$number)"
    }

    class OperationMonkey(
        name: String,
        val leftName: String,
        val operation: Char,
        val rightName: String
    ) : Monkey(name) {
        lateinit var left: Monkey
        lateinit var right: Monkey

        fun apply(left: Long, right: Long) = when (operation) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            else -> left / right
        }

        fun invertLeft(right: Long, result: Long) = when (operation) {
            '+' -> result - right
            '-' -> result + right
            '*' -> result / right
            else -> result * right
        }

        fun invertRight(left: Long, result: Long) = when (operation) {
            '+' -> result - left
            '-' -> left - result
            '*' -> result / left
            else -> left / result
        }

        override fun toString() =
            "OperationMonkey(name='$name', leftName='$leftName', operation=$operation, rightName='$rightName')"

    }

    val regex = ":? ".toRegex()

    fun parse(input: List<String>): Map<String, Monkey> {
        val monkeys = input.map { line ->
            val parts = line.split(regex)
            if (parts.size == 2) {
                NumberMonkey(parts[0], parts[1].toLong())
            } else {
                OperationMonkey(parts[0], parts[1], parts[2].first(), parts[3])
            }
        }.associateBy { it.name }

        for (monkey in monkeys.values.filterIsInstance<OperationMonkey>()) {
            monkey.left = monkeys.getValue(monkey.leftName)
            monkey.right = monkeys.getValue(monkey.rightName)
        }

        return monkeys
    }

    fun Monkey.getNumber(): Long {
        if (this is NumberMonkey) return number
        this as OperationMonkey

        return apply(left.getNumber(), right.getNumber())
    }

    fun part1(input: List<String>): Long {
        val monkeys = parse(input)
        return monkeys.getValue("root").getNumber()
    }

    fun part2(input: List<String>): Long {
        val monkeys = parse(input)
        val root = monkeys.getValue("root") as OperationMonkey

        fun Monkey.findHuman(path: MutableSet<Monkey>): Monkey? {
            if (name == "humn") {
                path += this
                return this
            }

            if (this !is OperationMonkey) return null

            left.findHuman(path)?.let { path += this; return it }
            right.findHuman(path)?.let { path += this; return it }

            return null
        }

        val pathToHuman = mutableSetOf<Monkey>().also { root.findHuman(it) }

        fun Monkey.getHumanNumber(result: Long): Long {
            if (name == "humn") return result
            this as OperationMonkey

            return if (left in pathToHuman) {
                left.getHumanNumber(invertLeft(right.getNumber(), result))
            } else {
                right.getHumanNumber(invertRight(left.getNumber(), result))
            }
        }

        return if (root.left in pathToHuman) {
            root.left.getHumanNumber(root.right.getNumber())
        } else {
            root.right.getHumanNumber(root.left.getNumber())
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
