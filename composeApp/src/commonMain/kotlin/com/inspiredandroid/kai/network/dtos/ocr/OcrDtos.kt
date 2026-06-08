package com.inspiredandroid.kai.network.dtos.ocr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class OcrRequestDto(
    val model: String,
    val document: JsonObject,
    @SerialName("include_image_base64")
    val includeImageBase64: Boolean = false,
)

@Serializable
data class OcrResponseDto(
    val pages: List<OcrPageDto> = emptyList(),
    val model: String? = null,
)

@Serializable
data class OcrPageDto(
    val markdown: String = "",
)
