package dev.willfarris.llmchat.data.chathistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [ChatMessageEntity::class, ChatConversationEntity::class], version = 1, exportSchema = false)
abstract class ChatHistoryDatabase : RoomDatabase() {

    abstract fun chatHistoryDao(): ChatHistoryDAO

    companion object {
        @Volatile
        private var Instance: ChatHistoryDatabase? = null

        fun getDatabase(context: Context): ChatHistoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ChatHistoryDatabase::class.java, "chat_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
