// LocalDataSourceImpl.kt
package com.youssef.weatherforcast.Data.LocalDataSource

import com.youssef.weatherforcast.Data.RemoteDataSource.ApiService
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.HomeData
import com.youssef.weatherforcast.WeatherAlert.WeatherAlert
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val favoriteDao: FavoriteDao
) : LocalDataSource {

    companion object {
        private var localDataSource: LocalDataSource? = null

        fun getInstance(favoriteDao: FavoriteDao): LocalDataSource {
            return localDataSource ?: synchronized(this) {
                val instance = LocalDataSourceImpl(favoriteDao)
                localDataSource = instance
                instance
            }
        }
    }

    override suspend fun insertHomeData(homeData: HomeData) {
        favoriteDao.insertHomeData(homeData)
    }

    override fun getHomeData(): Flow<HomeData?> {
 return favoriteDao.getHomeData()
    }

    override suspend fun insertFavorite(favoriteLocation: FavoriteLocation) {
        favoriteDao.insertFavorite(favoriteLocation)
    }

    override suspend fun deleteFavorite(favoriteLocation: FavoriteLocation) {
        favoriteDao.deleteFavorite(favoriteLocation)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun insertAlert(weatherAlert: WeatherAlert) {
        favoriteDao.insertAlert(weatherAlert)
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        favoriteDao.deleteAlert(weatherAlert)
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> {
        return favoriteDao.getAllAlerts()
    }
}
