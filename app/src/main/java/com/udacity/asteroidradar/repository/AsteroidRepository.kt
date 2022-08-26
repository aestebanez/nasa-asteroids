package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import android.view.animation.Transformation
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.utils.Constants.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidDatabase) {


    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().plusDays(7)

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map((database.asteroidDao.getAsteroids())) {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroids : LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidsDay(startDate.format(
        DateTimeFormatter.ISO_DATE))
    ) {
            it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val weekAsteroids : LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidWeek(
            startDate.format(DateTimeFormatter.ISO_DATE),
            endDate.format(DateTimeFormatter.ISO_DATE)))
    {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = AsteroidApiService.AsteroidApi.retrofitService.getAsteroids(API_KEY)
                val restult = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(*restult.asDatabaseModel())
            }
            catch (err: Exception){
                Log.e("Fail: refreshAsteroids", err.message.toString())
            }
        }
    }
}