package com.kasirpoint.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageCompressor {

    fun compressUriToBase64(context: Context, uri: Uri, maxDimension: Int = 800, quality: Int = 75): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            inputStream?.close()

            // Rotate bitmap according to EXIF orientation if needed
            val rotatedBitmap = fixOrientation(context, uri, originalBitmap)

            // Calculate scaled dimensions
            val width = rotatedBitmap.width
            val height = rotatedBitmap.height
            val bitmapRatio = width.toFloat() / height.toFloat()

            var newWidth = width
            var newHeight = height

            if (width > maxDimension || height > maxDimension) {
                if (bitmapRatio > 1) {
                    newWidth = maxDimension
                    newHeight = (maxDimension / bitmapRatio).toInt()
                } else {
                    newHeight = maxDimension
                    newWidth = (maxDimension * bitmapRatio).toInt()
                }
            }

            val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)

            // Compress to JPEG byte array
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()

            // Encode to Base64 data URL
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compressBitmapToBase64(bitmap: Bitmap, maxDimension: Int = 800, quality: Int = 75): String {
        val width = bitmap.width
        val height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()

        var newWidth = width
        var newHeight = height

        if (width > maxDimension || height > maxDimension) {
            if (bitmapRatio > 1) {
                newWidth = maxDimension
                newHeight = (maxDimension / bitmapRatio).toInt()
            } else {
                newHeight = maxDimension
                newWidth = (maxDimension * bitmapRatio).toInt()
            }
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()

        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64String"
    }

    private fun fixOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exifInterface = ExifInterface(inputStream)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Exception) {
            bitmap
        }
    }
}
