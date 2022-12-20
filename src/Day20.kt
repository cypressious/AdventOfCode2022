import kotlin.math.absoluteValue

fun main() {
    data class Node(var number: Long) {
        lateinit var prev: Node
        lateinit var next: Node

        fun remove() {
            this.prev.next = this.next
            this.next.prev = this.prev
        }

        fun insertAfter(a: Node) {
            val b = a.next
            prev = a
            next = b

            a.next = this
            b.prev = this
        }

        fun get(diff: Int): Node {
            var current = this
            val forward = diff > 0
            repeat(diff.absoluteValue) {
                current = if (forward) current.next else current.prev
            }
            return current
        }

        fun toList(): List<Node> = buildList {
            add(this@Node)

            var current = next

            while (current !== this@Node) {
                add(current)
                current = current.next
            }
        }
    }

    fun parse(input: List<String>): List<Node> {
        val nodes = input.map { Node(it.toLong()) }

        for ((a, b) in nodes.windowed(2, 1)) {
            a.next = b
            b.prev = a
        }

        nodes.first().prev = nodes.last()
        nodes.last().next = nodes.first()
        return nodes
    }

    fun part1(input: List<String>): Long {
        val nodes = parse(input)

        for (node in nodes) {
            if (node.number == 0L) continue

            val prev = node.prev
            node.remove()
            node.insertAfter(prev.get(node.number.toInt()))
        }

        val zero = nodes.first { it.number == 0L }
        val a = zero.get(1000)
        val b = a.get(1000)
        val c = b.get(1000)

        return a.number + b.number + c.number
    }

    fun part2(input: List<String>): Long {
        val nodes = parse(input)
        for (node in nodes) {
            node.number *= 811589153
        }

        repeat(10) {
            for (node in nodes) {
                if (node.number == 0L) continue

                val prev = node.prev
                node.remove()
                val list = prev.toList()
                check(list.size == nodes.size - 1)
                val insertAfter = list[Math.floorMod(node.number, list.size)]
                node.insertAfter(insertAfter)
            }
        }

        val list = nodes.first { it.number == 0L }.toList()

        return list[1000 % list.size].number + list[2000 % list.size].number + list[3000 % list.size].number
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
