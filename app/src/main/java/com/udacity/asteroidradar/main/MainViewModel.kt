package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

class MainViewModel : ViewModel() {

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
}