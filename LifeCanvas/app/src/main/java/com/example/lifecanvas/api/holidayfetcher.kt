package com.example.lifecanvas.api

import android.content.Context
import android.util.Log
import com.example.lifecanvas.R
import com.example.lifecanvas.model.HolidayModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun fetchHolidaysByYear(context: Context, year: String): List<HolidayModel> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://public-holiday.p.rapidapi.com/$year/TR")
        .get()
        .addHeader("X-RapidAPI-Key", context.getString(R.string.calendar_api_key))
        .addHeader("X-RapidAPI-Host", context.getString(R.string.calendar_host))
        .build()

    try {
        val response = client.newCall(request).execute()
        Log.d("fetchHolidaysByYear", "Response: ${response.code}")

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            responseBody?.let {
                Log.d("fetchHolidaysByYear", "Response Body: $it")
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val type = Types.newParameterizedType(List::class.java, HolidayModel::class.java)
                val adapter = moshi.adapter<List<HolidayModel>>(type)
                return@withContext adapter.fromJson(it) ?: emptyList()
            } ?: run {
                Log.e("fetchHolidaysByYear", "Response body is null")
                emptyList()
            }
        } else {
            Log.e("fetchHolidaysByYear", "Unsuccessful response")
            emptyList()
        }
    } catch (e: Exception) {
        Log.e("fetchHolidaysByYear", "Exception: ${e.message}", e)
        emptyList()
    }
}

suspend fun getHolidayForDay(context: Context,date: Date): List<HolidayModel> {
    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    val year = yearFormat.format(date)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateString = dateFormat.format(date)

    return withContext(Dispatchers.IO) {
        val holidays = fetchHolidaysByYear(context,year)
        holidays.filter { it.date == dateString }
    }
}