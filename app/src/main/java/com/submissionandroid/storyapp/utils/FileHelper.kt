package com.submissionandroid.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File

object FileHelper {

    fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFile = File.createTempFile("story", ".jpg", context.cacheDir)
        contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    fun saveBitmapToFile(bitmap: Bitmap, context: Context): Uri {
        val tempFile = File.createTempFile("camera_story", ".jpg", context.cacheDir)
        tempFile.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(tempFile)
    }
}
