package com.aimodelaggregator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aimodelaggregator.data.database.dao.ConversationDao
import com.aimodelaggregator.data.database.dao.MessageDao
import com.aimodelaggregator.data.database.dao.ProviderModelDao
import com.aimodelaggregator.data.database.entity.ConversationEntity
import com.aimodelaggregator.data.database.entity.MessageEntity
import com.aimodelaggregator.data.database.entity.ProviderModelEntity

@Database(
    entities = [
        ProviderModelEntity::class,
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun providerModelDao(): ProviderModelDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
