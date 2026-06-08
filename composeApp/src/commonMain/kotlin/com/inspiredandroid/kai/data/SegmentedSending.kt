package com.inspiredandroid.kai.data

internal const val SEGMENT_MARKER = "<kai-segment/>"

private const val MAX_SEGMENTS = 4
private const val MAX_SEGMENT_CHARS = 700

internal fun parseSegmentedAssistantContent(content: String): List<String> {
    if (!content.contains(SEGMENT_MARKER)) return listOf(content)
    if (containsCodeFence(content) || content.contains("```kai-ui")) return listOf(content)
    val parts = content.split(SEGMENT_MARKER)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    if (parts.size < 2 || parts.size > MAX_SEGMENTS) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    if (parts.any { it.length > MAX_SEGMENT_CHARS }) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    if (parts.any { looksStructured(it) }) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    return parts
}

private fun containsCodeFence(content: String): Boolean = content.contains("```")

private fun looksStructured(content: String): Boolean {
    val trimmed = content.trimStart()
    return trimmed.startsWith("|") ||
        trimmed.startsWith("#") ||
        trimmed.startsWith("- ") ||
        trimmed.startsWith("* ") ||
        trimmed.startsWith("1.") ||
        trimmed.startsWith("{") ||
        trimmed.startsWith("[")
}
