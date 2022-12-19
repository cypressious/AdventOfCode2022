fun main() {
    data class Blueprint(
        val number: Int,
        val oreOreCost: Int,
        val clayOreCost: Int,
        val obsidianOreCost: Int,
        val obsidianClayCost: Int,
        val geodeOreCost: Int,
        val geodeObsidianCost: Int
    ) {
        val maxNecessaryOreRobots = maxOf(geodeOreCost, obsidianOreCost, clayOreCost, oreOreCost)
        val maxNecessaryClayRobots = obsidianClayCost
    }


    fun parse(input: List<String>) = input.map { line ->
        line.split(":? ".toRegex()).mapNotNull(String::toIntOrNull)
            .let { Blueprint(it[0], it[1], it[2], it[3], it[4], it[5], it[6]) }
    }

    data class State(
        val oreRobots: Int = 1,
        val ore: Int = 0,
        val clayRobots: Int = 0,
        val clay: Int = 0,
        val obsidianRobots: Int = 0,
        val obsidian: Int = 0,
    ) {
        fun buildOreRobot(bp: Blueprint) = copy(
            oreRobots = oreRobots + 1,
            ore = ore - bp.oreOreCost
        )

        fun canBuildOreRobot(bp: Blueprint) = ore >= bp.oreOreCost

        fun buildClayRobot(bp: Blueprint) = copy(
            clayRobots = clayRobots + 1,
            ore = ore - bp.clayOreCost
        )

        fun canBuildClayRobot(bp: Blueprint) = ore >= bp.clayOreCost

        fun buildObsidianRobot(bp: Blueprint) = copy(
            obsidianRobots = obsidianRobots + 1,
            ore = ore - bp.obsidianOreCost,
            clay = clay - bp.obsidianClayCost
        )

        fun canBuildObsidianRobot(bp: Blueprint) =
            ore >= bp.obsidianOreCost && clay >= bp.obsidianClayCost

        fun buildGeodeRobot(bp: Blueprint) = copy(
            ore = ore - bp.geodeOreCost,
            obsidian = obsidian - bp.geodeObsidianCost
        )

        fun canBuildGeodeRobot(bp: Blueprint) =
            ore >= bp.geodeOreCost && obsidian >= bp.geodeObsidianCost

        fun collect() = copy(
            ore = ore + oreRobots,
            clay = clay + clayRobots,
            obsidian = obsidian + obsidianRobots,
        )
    }

    fun getMaxGeodes(
        bp: Blueprint,
        state: State,
        minutesLeft: Int,
        cache: MutableMap<Pair<State, Int>, Int>
    ): Int {
        if (minutesLeft == 0) return 0

        val cacheKey = state to minutesLeft
        cache[cacheKey]?.let {
            return it
        }

        if (state.canBuildGeodeRobot(bp)) {
            val result = (minutesLeft - 1) +
                    getMaxGeodes(
                        bp,
                        state.buildGeodeRobot(bp).collect(),
                        minutesLeft - 1,
                        cache
                    )

            cache[cacheKey] = result

            return result
        }

        val possibleStates = buildList {
            val afterCollection = state.collect()

            val maxNecessaryObsidian = (minutesLeft - 1) * bp.geodeObsidianCost
            val projectedObsidian = (minutesLeft - 1) * state.obsidianRobots + state.obsidian

            if (projectedObsidian >= maxNecessaryObsidian && state.oreRobots >= bp.maxNecessaryOreRobots) {
                add(afterCollection)
                return@buildList
            }

            if (
                state.canBuildObsidianRobot(bp)
            ) {
                add(afterCollection.buildObsidianRobot(bp))
            }

            if (
                state.clayRobots < bp.maxNecessaryClayRobots &&
                state.canBuildClayRobot(bp)
            ) {
                add(afterCollection.buildClayRobot(bp))
            }

            if (
                state.oreRobots < bp.maxNecessaryOreRobots &&
                state.canBuildOreRobot(bp)
            ) {
                add(afterCollection.buildOreRobot(bp))
            }

            add(afterCollection)
        }

        val result =
            possibleStates.maxOf { getMaxGeodes(bp, it, minutesLeft - 1, cache) }

        cache[cacheKey] = result

        return result
    }

    fun part1(input: List<String>): Int {
        val blueprints = parse(input)

        return blueprints.parallelStream()
            .map { it.number * getMaxGeodes(it, State(), 24, mutableMapOf()) }
            .reduce(0, Int::plus)
    }

    fun part2(input: List<String>): Int {
        val blueprints = parse(input)

        return blueprints.take(3).parallelStream()
            .map { getMaxGeodes(it, State(), 32, mutableMapOf()) }.reduce(1, Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 56 * 62)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
