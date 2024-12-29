package com.example.project_test.utility

fun fuzzySearch(query: String, items: List<String>, threshold: Int = 3): List<String> {
    return items.filter { levenshteinDistance(query, it) <= threshold }
}

fun levenshteinDistance(lhs: String, rhs: String): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length
    var costArray = Array(lhsLength + 1) { it }
    var newCostArray = Array(lhsLength + 1) { 0 }

    for (i in 1..rhsLength) {
        newCostArray[0] = i

        for (j in 1..lhsLength) {
            val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1

            newCostArray[j] = minOf(
                newCostArray[j - 1] + 1,
                costArray[j] + 1,
                costArray[j - 1] + match
            )
        }

        val swap = costArray
        costArray = newCostArray
        newCostArray = swap
    }

    return costArray[lhsLength]
}

