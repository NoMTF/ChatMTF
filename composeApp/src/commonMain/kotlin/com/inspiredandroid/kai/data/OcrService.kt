package com.inspiredandroid.kai.data

import com.inspiredandroid.kai.network.Requests
import com.inspiredandroid.kai.network.ServiceCredentials

data class OcrResult(
    val text: String,
    val sourceFileName: String?,
    val confidence: Float? = null,
    val error: String? = null,
)

class OcrService(
    private val requests: Requests,
    private val appSettings: AppSettings,
) {
    suspend fun extractText(
        service: Service,
        instanceId: String,
        modelId: String,
        baseUrlOverride: String,
        mimeType: String,
        base64Data: String,
        fileName: String?,
    ): OcrResult {
        if (modelId.isBlank()) {
            return OcrResult(text = "", sourceFileName = fileName, error = "OCR model is not configured")
        }
        val credentials = ServiceCredentials(
            apiKey = appSettings.getInstanceApiKey(instanceId),
            modelId = modelId,
            baseUrl = baseUrlOverride.ifBlank { appSettings.getInstanceBaseUrl(instanceId) },
        )
        val result = if (service == Service.Mistral || modelId.contains("mistral-ocr", ignoreCase = true)) {
            requests.mistralOcr(credentials, mimeType, base64Data)
        } else {
            requests.openAICompatibleOcr(credentials, mimeType, base64Data, fileName)
        }
        return result.fold(
            onSuccess = { text ->
                if (text.isBlank()) {
                    OcrResult(text = "", sourceFileName = fileName, error = "OCR returned no text")
                } else {
                    OcrResult(text = text, sourceFileName = fileName)
                }
            },
            onFailure = { error ->
                OcrResult(text = "", sourceFileName = fileName, error = error.message ?: error::class.simpleName)
            },
        )
    }
}
