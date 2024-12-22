package com.example.rajahacker

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room
import org.jsoup.Jsoup

class ApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val apiService = ApiService.create() // Assume you have an ApiService for HTTP calls
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build()

    override suspend fun doWork(): Result {
        Log.d("ApiWorker", "Starting work")

        return try {
            val response = apiService.getApiResponse() // Make the API call

            val dao = db.apiResponseDao()
            val oldResponse = dao.getResponse(1)

            if (oldResponse?.response?.content != response.content) {
                dao.insertResponse(ApiResponse(1, response))
                Log.d("ApiWorker", "New response inserted into database")

                val firstJobTitle = extractFirstJobTitle(response.content)
                val firstGrade = extractFirstGrade(response.content)
                val primarySpeciality = extractPrimarySpeciality(response.content)
                val salary = extractSalary(response.content)
                val workingPeriodDesc = extractWorkingPeriodDesc(response.content)

//                showNotification("New Job Posting Detected", "Job: $firstJobTitle")
                showNotification("New Job: ▶ $firstJobTitle", "Grade: $firstGrade\n$primarySpeciality\n$salary\n$workingPeriodDesc")

            } else {
                Log.d("ApiWorker", "API response has not changed")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ApiWorker", "Error during work", e)
            Result.failure()
        }
    }

    private fun extractFirstJobTitle(content: String): String {
        val document = Jsoup.parse(content)
        val firstJobElement = document.selectFirst(".hj-jobtitle")
        return firstJobElement?.text() ?: "No job title found"
    }

    private fun extractFirstGrade(content: String): String {
        val document = Jsoup.parse(content)
        val firstGradeElement = document.selectFirst(".hj-grade")
        return firstGradeElement?.text() ?: "No grade found"
    }

    private fun extractPrimarySpeciality(content: String): String {
        val document = Jsoup.parse(content)
        val primarySpecialityElement = document.selectFirst(".hj-primaryspeciality")
        return primarySpecialityElement?.text() ?: "No primary speciality found"
    }

    private fun extractSalary(content: String): String {
        val document = Jsoup.parse(content)
        val salaryElement = document.selectFirst(".hj-salary")
        return salaryElement?.text() ?: "No salary found"
    }

    private fun extractWorkingPeriodDesc(content: String): String {
        val document = Jsoup.parse(content)
        val workingPeriodDescElement = document.selectFirst(".hj-workingperioddesc")
        return workingPeriodDescElement?.text() ?: "No working period description found"
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notification = NotificationCompat.Builder(applicationContext, "api_channel")
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_notification)
                .build()
            notificationManager.notify(1, notification)
            Log.d("ApiWorker", "Notification shown: $title - $message")
        } else {
            Log.w("ApiWorker", "Permission to post notifications not granted")
            // Handle the case where the permission is not granted
            // You might want to request the permission here or notify the user
        }
    }
}
