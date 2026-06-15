package com.example.project_sketch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

suspend fun subirACloudinary(context: Context, imageUri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            val maxSize = 1080
            val ratio = minOf(maxSize.toFloat() / originalBitmap.width, maxSize.toFloat() / originalBitmap.height)
            val resizedBitmap = originalBitmap.scale(
                (originalBitmap.width * ratio).toInt(),
                (originalBitmap.height * ratio).toInt()
            )
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val imageBytes = outputStream.toByteArray()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.jpg", imageBytes.toRequestBody("image/*".toMediaType()))
                .addFormDataPart("upload_preset", "android_upload")
                .build()

            val response = OkHttpClient().newCall(
                Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dxymgqjb8/image/upload")
                    .post(requestBody)
                    .build()
            ).execute()

            JSONObject(response.body.string()).optString("secure_url").ifBlank { null }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}