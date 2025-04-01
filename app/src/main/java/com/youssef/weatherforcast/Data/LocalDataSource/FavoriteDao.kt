package com.youssef.weatherforcast.Data.LocalDataSource
import androidx.room.*
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.HomeData
import com.youssef.weatherforcast.WeatherAlert.WeatherAlert
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteLocation: FavoriteLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeData(homeData: HomeData)

    @GET
    @Query("SELECT * FROM Home_Data")
    fun getHomeData(): Flow<HomeData?>

    @Delete
    suspend fun deleteFavorite(favoriteLocation: FavoriteLocation)
    @Query("SELECT * FROM favorite_locations")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(weatherAlert: WeatherAlert)

    @Delete
    suspend fun deleteAlert(weatherAlert: WeatherAlert)

    @Query("SELECT * FROM weather_alerts ORDER BY id DESC")
    fun getAllAlerts(): Flow<List<WeatherAlert>>


}



