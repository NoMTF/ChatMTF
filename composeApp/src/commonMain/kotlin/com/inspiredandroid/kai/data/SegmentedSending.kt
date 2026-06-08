package com.inspiredandroid.kai.data

internal const val SEGMENT_MARKER = "<kai-segment/>"

private const val MAX_SEGMENTS = 4
private const val MAX_SEGMENT_CHARS = 700
private const val MAX_AUTO_SEGMENT_CHARS = 90
private const val MAX_AUTO_TOTAL_CHARS = 320

internal fun parseSegmentedAssistantContent(content: String): List<String> {
    if (content.isBlank()) return listOf(content)
    if (containsCodeFence(content) || content.contains("```kai-ui")) return listOf(content)
    if (!content.contains(SEGMENT_MARKER)) return parseNaturalLineSegments(content)
    val parts = normalizeNewlines(content).split(SEGMENT_MARKER)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    if (parts.size < 2 || parts.size > MAX_SEGMENTS) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    if (parts.any { it.length > MAX_SEGMENT_CHARS }) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    if (parts.any { looksStructured(it) }) return listOf(content.replace(SEGMENT_MARKER, "\n\n").trim())
    return parts
}

private fun containsCodeFence(content: String): Boolean = content.contains("```")

private fun parseNaturalLineSegments(content: String): List<String> {
    val normalized = normalizeNewlines(content).trim()
    if (!normalized.contains('\n')) return listOf(content)
    if (normalized.contains("\n\n")) return listOf(content)
    if (normalized.length > MAX_AUTO_TOTAL_CHARS) return listOf(content)

    val lines = normalized.lines().map { it.trim() }
    if (lines.size < 2 || lines.size > MAX_SEGMENTS) return listOf(content)
    if (lines.any { it.isEmpty() || it.length > MAX_AUTO_SEGMENT_CHARS }) return listOf(content)
    if (lines.any { looksStructured(it) }) return listOf(content)

    return lines
}

private fun normalizeNewlines(content: String): String =
    content.replace("\r\n", "\n").replace('\r', '\n')

private fun looksStructured(content: String): Boolean {
    val trimmed = content.trimStart()
    return trimmed.startsWith("|") ||
        trimmed.startsWith("#") ||
        trimmed.startsWith("- ") ||
        trimmed.startsWith("* ") ||
        trimmed.startsWith("+ ") ||
        trimmed.startsWith("1.") ||
        trimmed.startsWith(">") ||
        trimmed.startsWith("<") ||
        trimmed.startsWith("```") ||
        trimmed.startsWith("~~~") ||
        trimmed.startsWith("{") ||
        trimmed.startsWith("[") ||
        trimmed.matches(Regex("""\d+[.)]\s+.*"""))
}
