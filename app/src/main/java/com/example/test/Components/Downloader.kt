package com.example.test.Components

import android.app.DownloadManager
import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResult
import com.example.test.utils.RECORDS_STORAGE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class Downloader(private val context: Context) {

    fun downloadFromFirebaseStorage(downloadUrl: String, directory: String = "") {
        var filename = downloadUrl.substringAfterLast("/")

        if (directory.isNotEmpty()) {
            filename = "$directory/$filename"
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(downloadUrl)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, filename
        )
        Log.d("DOWNLOAD DIRECTORY", Environment.DIRECTORY_DOWNLOADS)

        downloadManager.enqueue(request)
    }
}

class FilePicker {

    fun pickFile(
        result: ActivityResult,
        onLoadingChange: () -> Unit,
        onRetrieval: (uri: Uri) -> Unit,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        directory: String
    ) {
        if (result.data != null) {
            onLoadingChange()
            val storageRef =
                storage.reference.child("${directory}/${result.data!!.data?.lastPathSegment}_${System.currentTimeMillis()}")
            result.data!!.data?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val snapshot = storageRef.putFile(it).await()
                        val downloadUri = snapshot.storage.downloadUrl.await()
                        onRetrieval(downloadUri)
                        onLoadingChange()
                        addUrlToFirestore(downloadUri.toString(), db)
                    } catch (e: Exception) {
                        onLoadingChange()
                        println("Error uploading file: $e")
                    }
                }
            }
        }
    }

    fun pickMultipleFiles(
        result: ActivityResult,
        onLoadingChange: () -> Unit,
        onRetrieval: (Uri) -> Unit,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        directory: String
    ) {
        if (result.data != null) {
            onLoadingChange()
            Log.d("Dataaaa: ", result.data!!.clipData.toString())
            val uris = result.data!!.clipData
            if (uris != null) {
                for (i in 0 until uris.itemCount) {
                    val item = uris.getItemAt(i).uri
                    item.let {
                        val storageReference =
                            storage.reference.child("${directory}/${item.lastPathSegment}_${System.currentTimeMillis()}")
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Log.d("HERE", "here")
                                val snapshot = storageReference.putFile(item).await()
                                val resultUri = snapshot.storage.downloadUrl.await()
                                onRetrieval(resultUri)
                                onLoadingChange()
                                addUrlToFirestore(resultUri.toString(), db)
                            } catch (e: Exception) {
                                onLoadingChange()
                                println("Error uploading file: $e")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addUrlToFirestore(fileUrl: String, db: FirebaseFirestore) {
        db.collection(RECORDS_STORAGE)
            .add(hashMapOf("fileUrl" to fileUrl)) // Add document with "fileUrl" field
            .addOnSuccessListener { documentReference ->
                println("Document written with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

}