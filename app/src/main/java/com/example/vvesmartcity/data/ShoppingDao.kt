package com.example.vvesmartcity.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Insert
    suspend fun insert(item: ShoppingEntity)

    @Update
    suspend fun update(item: ShoppingEntity)

    @Delete
    suspend fun delete(item: ShoppingEntity)

    @Query("SELECT * FROM Info")
    fun getAll(): Flow<List<ShoppingEntity>>

    @Query("SELECT * FROM Info WHERE id = :id")
    suspend fun getById(id: Int): ShoppingEntity?
}
