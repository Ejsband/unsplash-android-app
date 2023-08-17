//package com.project.androidunsplash.background
//
//import android.content.Context
//import android.graphics.Bitmap
//import androidx.work.ArrayCreatingInputMerger
//import androidx.work.BackoffPolicy
//import androidx.work.Constraints
//import androidx.work.NetworkType
//import androidx.work.OneTimeWorkRequest
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import androidx.work.workDataOf
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.FutureTarget
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.util.concurrent.TimeUnit
//
//class SaveImageWorker(
//    context: Context,
//    workerParams: WorkerParameters
//) : Worker(context, workerParams) {
//
//    companion object {
//        private const val WORK_TAG = "SaveImageRequest"
//        private const val WORK_INPUT_KEY = "$WORK_TAG InputKey"
//
//        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
//            val networkConstraint = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresBatteryNotLow(true)
//                .build()
//            return OneTimeWorkRequestBuilder<SaveImageWorker>()
//                .setConstraints(networkConstraint)
//                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
//                .setInputMerger(ArrayCreatingInputMerger::class.java)
//                .setInitialDelay(1, TimeUnit.SECONDS)
//                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.SECONDS)
//                .addTag(WORK_TAG)
//                .build()
//        }
//    }
//
//    override fun doWork(): Result {
//        return if (saveImage()) Result.success() else Result.failure()
//    }
//
//    private fun saveImage(): Boolean {
//        val input = inputData.getStringArray(WORK_INPUT_KEY).toString()
//        return saveImageToInternalStorage(
//            getBitmapFutureTargetFromUrl(input),
//            "${System.currentTimeMillis()}.jpg"
//        )
//    }
//
//    private fun saveImageToInternalStorage(
//        bitmapFutureTarget: FutureTarget<Bitmap>,
//        fileName: String
//    ): Boolean {
//        val bitmap: Bitmap = bitmapFutureTarget.get()
//        val directory = File("/storage/emulated/0/Download")
//
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//
//        val file = File(directory, fileName)
//
//        try {
//            val outputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//
//            return true
//
//        } catch (e: IOException) {
//            return false
//        }
//    }
//
//    private fun getBitmapFutureTargetFromUrl(url: String): FutureTarget<Bitmap> {
//        return Glide.with(applicationContext).asBitmap().load(url).submit()
//    }
//}