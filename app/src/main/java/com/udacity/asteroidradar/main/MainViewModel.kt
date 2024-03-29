package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.utils.Constants.API_KEY
import com.udacity.asteroidradar.utils.Constants.FILTER_ALL
import com.udacity.asteroidradar.utils.Constants.FILTER_TODAY
import com.udacity.asteroidradar.utils.Constants.FILTER_WEEK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.System.err

class MainViewModel(application : Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay : LiveData<PictureOfDay>
        get() {
            return _pictureOfDay
        }

    private val _goToDetailAsteroid = MutableLiveData<Asteroid>()
    val goToDetailAsteroid : LiveData<Asteroid>
        get() {
            return _goToDetailAsteroid
        }
    private var _filterAsteroids = MutableLiveData(FILTER_ALL)

    val asteroidList = Transformations.switchMap(_filterAsteroids) {
        when (it!!) {
            FILTER_WEEK -> asteroidRepository.weekAsteroids
            FILTER_TODAY -> asteroidRepository.todayAsteroids
            else -> asteroidRepository.allAsteroids
        }
    }


    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            refreshPicOfTheDay()
        }
    }

    fun onChangeFilter(filter : String) {
        _filterAsteroids.postValue(filter)
    }

    fun onAsteroidClicked(asteroid : Asteroid) {
        _goToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _goToDetailAsteroid.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Failed to build ViewModel")
        }
    }

    private suspend fun refreshPicOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureOfDay.postValue(
                    AsteroidApiService.AsteroidApi.retrofitService.getPictureOfTheDay(API_KEY)
                )
            }catch (err : Exception) {
                Log.e("refreshPictureOfTheDay", err.printStackTrace().toString())
            }

        }
    }


}