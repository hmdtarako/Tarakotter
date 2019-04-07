package jp.tarako.hmd.tarakotter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloadTask(private val imageView: ImageView) : AsyncTask<URL, Unit, Bitmap?> () {

    companion object {
        private const val TAG = "Tarakotter@ImageDownloadTask"
    }

    override fun doInBackground(vararg urls: URL?): Bitmap? {
        Log.d(TAG, "doInBackground")
        if (urls.isEmpty()) {
            return null
        }
        return downloadImage(urls[0]!!)
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        Log.d(TAG, "onPostExecute")

        imageView.setImageBitmap(result)
    }

    private fun downloadImage(url: URL): Bitmap? {
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.responseCode

            when (response) {
                HttpURLConnection.HTTP_OK -> {
                    val inputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    Log.d(TAG, "$bitmap")
                    return bitmap
                }
                else -> {
                    Log.d(TAG, "failed to connect")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}