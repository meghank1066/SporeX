package com.example.sporex_app.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun uriToMultipart(
    context: Context,
    uri: Uri,
    partName: String = "file"
): MultipartBody.Part {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?.let { ".$it" }
        ?: ".jpg"

    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open selected image")

    val tempFile = File.createTempFile("upload_", extension, context.cacheDir)

    FileOutputStream(tempFile).use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        partName,
        tempFile.name,
        requestFile
    )
}