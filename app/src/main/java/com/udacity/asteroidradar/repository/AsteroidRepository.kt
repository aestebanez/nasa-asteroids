package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

class AsteroidRepository(private val database: AsteroidDatabase) {

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map((database.asteroidDao.getAsteroids())) {
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