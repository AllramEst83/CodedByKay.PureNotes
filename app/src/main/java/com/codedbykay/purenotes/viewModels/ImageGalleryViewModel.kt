package com.codedbykay.purenotes.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codedbykay.purenotes.MainApplication
import com.codedbykay.purenotes.db.todo.ToDoImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageGalleryViewModel : ViewModel() {

    // Services
    private val imageStorageService = MainApplication.imageStorageService

    // DAO´s
    private val imageDao = MainApplication.toDoDatabase.getImageDao()
    private val todoDao = MainApplication.toDoDatabase.getTodoDao()

    /**
     * Fetches images associated with a specific ToDo.
     */
    fun getImageByToDoId(toDoId: Int): LiveData<List<ToDoImage>> {
        return imageDao.getImagesForToDo(toDoId)
    }

    /**
     * Adds an image to a ToDo item by saving the bitmap, obtaining its URI, and storing it in the DB.
     */
    fun addImageToToDo(toDoId: Int, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            // Save the bitmap to internal storage and get the URI
            val uri = imageStorageService.saveImageToInternalStorage(bitmap, toDoId.toString())
            uri?.let {
                // Insert the image URI into the database
                imageDao.insertImage(ToDoImage(toDoId = toDoId, imageUri = it.toString()))
            }
        }
    }

    /**
     * Removes an image from a ToDo item by deleting it from the DB and internal storage.
     */
    fun removeImageFromToDo(image: ToDoImage) {
        viewModelScope.launch(Dispatchers.IO) {
            // Delete the image from internal storage
            imageStorageService.deleteImageFromInternalStorage(image.imageUri)
            // Delete the image record from the database
            imageDao.deleteImage(image)
        }
    }

    /**
     * Removes all images for todos that are marked as done within a specific group.
     */
    suspend fun removeAllImagesForCompletedTodosInGroup(groupId: Int) {
        withContext(Dispatchers.IO) {
            try {
                // Fetch all todos in the group that are marked as done
                val completedTodos = todoDao.getToDosDoneByGroupId(groupId)

                // For each todo, delete all images and the folder
                for (todo in completedTodos) {
                    // Delete images from internal storage by todo id
                    imageStorageService.deleteAllImagesByToDoId(todo.id.toString())

                    // Delete image records from the database by todo id
                    imageDao.deleteAllImageByToDoId(todo.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception as needed
            }
        }
    }

    suspend fun removeAllImagesFromGroup(groupId: Int) {
        withContext(Dispatchers.IO) {
            try {

                val allTodosInGroup = todoDao.getToDoByGroupId(groupId)

                for (todo in allTodosInGroup) {

                    imageStorageService.deleteAllImagesByToDoId(todo.id.toString())

                    imageDao.deleteAllImageByToDoId(todo.id)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception as needed
            }
        }
    }
}