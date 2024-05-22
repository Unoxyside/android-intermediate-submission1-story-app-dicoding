package com.bahasyim.mystoryapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bahasyim.mystoryapp.data.api.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM story_item")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story_item")
    suspend fun deleteAll()

}