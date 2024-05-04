package com.example.test.Components

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log


class Downloader(private val context: Context) {

    fun downloadFromFirebaseStorage(downloadUrl: String, directory: String = "") {
        var filename = downloadUrl.substringAfterLast("/")

        if(directory.isNotEmpty()) {
            filename = "$directory/$filename"
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(downloadUrl)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            filename
        )
        Log.d("DOWNLOAD DIRECTORY",Environment.DIRECTORY_DOWNLOADS)

        downloadManager.enqueue(request)
    }
}