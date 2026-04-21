package com.deakin.llmchatbot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM messages ORDER BY id ASC")
    suspend fun getAllMessages(): List<Message>
}
