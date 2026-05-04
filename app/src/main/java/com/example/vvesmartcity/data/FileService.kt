package com.example.vvesmartcity.data

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileService {

    fun createImageDirectory(context: Context): File {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageDir = File(picturesDir, "SmartCityImages")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        return imageDir
    }

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        return try {
            val directory = createImageDirectory(context)
            val file = File(directory, "$fileName.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveScreenshot(context: Context, bitmap: Bitmap) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "screenshot_$timestamp"
        val path = saveBitmapToStorage(context, bitmap, fileName)
        if (path != null) {
            Toast.makeText(context, "截图保存成功，路径：$path", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "截图保存失败", Toast.LENGTH_SHORT).show()
        }
    }
}
