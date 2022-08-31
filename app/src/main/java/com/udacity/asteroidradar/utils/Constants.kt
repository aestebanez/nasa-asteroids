package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.BuildConfig

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = BuildConfig.NASA_API_KEY
    const val FILTER_TODAY = "today"
    const val FILTER_WEEK = "week"
    const val FILTER_ALL = "all"
}