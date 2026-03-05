package io.github.orangain.prettyjsonlog.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode

private val pureJsonPattern = Regex("""^\s*(\{.*})(\s*)$""")
private val jsonWithSuffixPattern = Regex("""^(\{.*})(\s*)$""")
private val containerTimestampPrefixPattern = Regex(
    """^\s*\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d{1,9})?(?:Z|[+-]\d{2}:\d{2})\s+$"""
)

fun parseJson(text: String): Pair<JsonNode, String>? {
    val pureJsonMatch = pureJsonPattern.matchEntire(text)
    if (pureJsonMatch != null) {
        return parseMatchedJson(pureJsonMatch)
    }

    val firstJsonObjectStart = text.indexOf('{')
    if (firstJsonObjectStart < 0) {
        return null
    }

    val prefix = text.substring(0, firstJsonObjectStart)
    if (!containerTimestampPrefixPattern.matches(prefix)) {
        return null
    }

    val jsonWithSuffixMatch = jsonWithSuffixPattern.matchEntire(text.substring(firstJsonObjectStart)) ?: return null
    return parseMatchedJson(jsonWithSuffixMatch)
}

private fun parseMatchedJson(match: MatchResult): Pair<JsonNode, String>? {
    return try {
        Pair(mapper.readTree(match.groups[1]!!.value), match.groups[2]!!.value)
    } catch (e: JsonProcessingException) {
        null
    }
}
