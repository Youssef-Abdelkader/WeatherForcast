package com.youssef.weatherforcast.Data.LocalDataSource
import androidx.room.*
import com.youssef.weatherforcast.Model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteLocation: FavoriteLocation)

    @Delete
    suspend fun deleteFavorite(favoriteLocation: FavoriteLocation)
    @Query("SELECT * FROM favorite_locations")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
}

