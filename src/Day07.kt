import kotlin.LazyThreadSafetyMode.NONE

fun main() {
    class File(
        val name: String,
        val size: Int
    ) {
        override fun toString() = name
    }

    class Directory(
        val name: String,
        val parent: Directory?,
        val dirs: MutableList<Directory> = mutableListOf(),
        val files: MutableList<File> = mutableListOf(),
    ) {
        override fun toString() = name

        fun walkDirs(cb: (Directory) -> Unit) {
            cb(this)
            for (dir in dirs) {
                dir.walkDirs(cb)
            }
        }

        val size: Int by lazy(NONE) { files.sumOf { it.size } + dirs.sumOf { it.size } }
    }

    fun parseFs(input: List<String>): Directory {
        val root = Directory("/", null)
        var cd = root
        var i = 0

        while (i in input.indices) {
            val line = input[i]
            val parts = line.split(' ')
            check(parts[0] == "$")

            when {
                parts[1] == "ls" -> {
                    while (++i in input.indices && !input[i].startsWith("$")) {
                        val (sizeOrDir, name) = input[i].split(" ")
                        if (sizeOrDir == "dir") {
                            cd.dirs += Directory(name, cd)
                        } else {
                            cd.files += File(name, sizeOrDir.toInt())
                        }
                    }
                }

                parts[1] == "cd" -> {
                    cd = when (val name = parts[2]) {
                        "/" -> root
                        ".." -> cd.parent!!
                        else -> cd.dirs.first { it.name == name }
                    }
                    i++
                }

                else -> throw IllegalStateException()
            }
        }
        return root
    }

    fun part1(input: List<String>): Int {
        val root = parseFs(input)

        var sum = 0

        root.walkDirs {
            val size = it.size
            if (size < 100000) sum += size
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val root = parseFs(input)
        val usedSpace = root.size
        val freeSpace = 70_000_000 - usedSpace
        val reqFreeSpace = 30_000_000
        val requiredSize = reqFreeSpace - freeSpace

        val candidates = mutableListOf<Directory>()

        root.walkDirs {
            if (it.size >= requiredSize) candidates += it
        }

        return candidates.minOf { it.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
