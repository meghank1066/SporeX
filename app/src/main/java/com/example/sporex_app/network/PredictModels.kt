package com.example.sporex_app.network

data class DetectionBoxDto(
    val class_name: String,
    val confidence: Double,
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double
)

data class PredictResponseDto(
    val success: Boolean,
    val mould_detected: Boolean,
    val max_confidence: Double?,
    val detections: List<DetectionBoxDto>,
    val image_url: String?,
    val message: String
)