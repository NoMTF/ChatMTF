package com.inspiredandroid.kai.tools

import com.inspiredandroid.kai.data.AppSettings
import com.inspiredandroid.kai.network.tools.ParameterSchema
import com.inspiredandroid.kai.network.tools.Tool
import com.inspiredandroid.kai.network.tools.ToolInfo
import com.inspiredandroid.kai.network.tools.ToolSchema

object SoulTools {
    val readSoulToolInfo = ToolInfo(
        id = "read_soul",
        name = "Read Soul",
        description = "Read the assistant's current Soul/system prompt.",
    )

    val updateSoulToolInfo = ToolInfo(
        id = "update_soul",
        name = "Update Soul",
        description = "Replace the assistant's Soul/system prompt.",
    )

    val appendSoulToolInfo = ToolInfo(
        id = "append_soul",
        name = "Append Soul",
        description = "Append guidance to the assistant's Soul/system prompt.",
    )

    val resetSoulToolInfo = ToolInfo(
        id = "reset_soul",
        name = "Reset Soul",
        description = "Reset the assistant's custom Soul to the bundled default.",
    )

    val toolDefinitions = listOf(readSoulToolInfo, updateSoulToolInfo, appendSoulToolInfo, resetSoulToolInfo)

    fun getTools(appSettings: AppSettings): List<Tool> = listOf(
        readSoulTool(appSettings),
        updateSoulTool(appSettings),
        appendSoulTool(appSettings),
        resetSoulTool(appSettings),
    )

    private fun readSoulTool(appSettings: AppSettings) = object : Tool {
        override val schema = ToolSchema(
            name = "read_soul",
            description = "Read your current custom Soul/system prompt. Returns an empty string when the bundled default Soul is active.",
            parameters = emptyMap(),
        )

        override suspend fun execute(args: Map<String, Any>): Any = mapOf(
            "success" to true,
            "soul" to appSettings.getSoulText(),
            "uses_default" to appSettings.getSoulText().isBlank(),
        )
    }

    private fun updateSoulTool(appSettings: AppSettings) = object : Tool {
        override val schema = ToolSchema(
            name = "update_soul",
            description = "Replace your custom Soul/system prompt. Use sparingly for durable identity, behavior, boundaries, or long-term operating instructions.",
            parameters = mapOf(
                "soul" to ParameterSchema(type = "string", description = "The complete new Soul/system prompt text.", required = true),
                "reason" to ParameterSchema(type = "string", description = "Short reason for this durable change.", required = false),
            ),
        )

        override suspend fun execute(args: Map<String, Any>): Any {
            val soul = args["soul"]?.toString()
                ?: return mapOf("success" to false, "error" to "Missing soul")
            appSettings.setSoulText(soul.trim())
            return mapOf("success" to true, "length" to soul.trim().length)
        }
    }

    private fun appendSoulTool(appSettings: AppSettings) = object : Tool {
        override val schema = ToolSchema(
            name = "append_soul",
            description = "Append a durable instruction to your custom Soul/system prompt. Prefer this for small, stable additions.",
            parameters = mapOf(
                "text" to ParameterSchema(type = "string", description = "Text to append to the Soul/system prompt.", required = true),
                "reason" to ParameterSchema(type = "string", description = "Short reason for this durable addition.", required = false),
            ),
        )

        override suspend fun execute(args: Map<String, Any>): Any {
            val addition = args["text"]?.toString()?.trim()
                ?: return mapOf("success" to false, "error" to "Missing text")
            if (addition.isBlank()) return mapOf("success" to false, "error" to "Text is blank")
            val current = appSettings.getSoulText().trim()
            val updated = if (current.isBlank()) addition else "$current\n\n$addition"
            appSettings.setSoulText(updated)
            return mapOf("success" to true, "length" to updated.length)
        }
    }

    private fun resetSoulTool(appSettings: AppSettings) = object : Tool {
        override val schema = ToolSchema(
            name = "reset_soul",
            description = "Reset your custom Soul/system prompt so the bundled default Soul is used.",
            parameters = mapOf(
                "reason" to ParameterSchema(type = "string", description = "Short reason for resetting the Soul.", required = false),
            ),
        )

        override suspend fun execute(args: Map<String, Any>): Any {
            appSettings.setSoulText("")
            return mapOf("success" to true, "uses_default" to true)
        }
    }
}
