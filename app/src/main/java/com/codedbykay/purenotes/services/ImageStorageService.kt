package com.codedbykay.purenotes.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class ImageStorageService(private val context: Context) {

    /**
     * Saves a Bitmap to the app's internal storage in a folder based on noteId and returns its file path.
     */
    fun saveImageToInternalStorage(bitmap: Bitmap, noteId: String): String? {
        val filename = "IMG_${UUID.randomUUID()}.jpg"
        var file: File? = null

        try {
            // Resize bitmap if it's too large (optional)
            val maxWidth = 1920 // Standard HD width
            val maxHeight = 1080 // Standard HD height
            val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val scale = minOf(
                    maxWidth.toFloat() / bitmap.width,
                    maxHeight.toFloat() / bitmap.height
                )
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                )
            } else bitmap

            // Create a directory for the noteId if it doesn't exist
            val noteDir = File(context.filesDir, noteId)
            if (!noteDir.exists()) {
                noteDir.mkdirs()
            }

            // Create a file in the note's directory
            file = File(noteDir, filename)
            FileOutputStream(file).use { fos ->
                // Use JPEG with 80% quality for better compression
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)

                // If the bitmap was scaled, recycle it
                if (scaledBitmap !== bitmap) {
                    scaledBitmap.recycle()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return file.absolutePath
    }

    /**
     * Deletes an image file given its file path. If the parent folder is empty after deletion, deletes the folder.
     */
    fun deleteImageFromInternalStorage(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            val parentDir = file.parentFile

            val deleted = file.delete()
            if (deleted && parentDir != null) {
                // Check if the directory is empty
                val files = parentDir.listFiles()
                if (files == null || files.isEmpty()) {
                    parentDir.delete()
                }
            }
            deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Helper function to load Bitmap from file path
    suspend fun loadBitmapFromFilePath(filePath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeFile(filePath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Deletes all images and the folder for a given todo ID.
     */
    fun deleteAllImagesByToDoId(todoId: String): Boolean {
        return try {
            // Get the directory for the todo ID
            val todoDir = File(context.filesDir, todoId)
            if (todoDir.exists()) {
                // Delete all files in the directory
                todoDir.deleteRecursively()
            } else {
                // Directory does not exist
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
