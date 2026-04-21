package com.aura.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aura.app.data.local.entities.CompletedMission
import com.aura.app.data.local.entities.SocialLabSession
import com.aura.app.data.local.entities.UserProfile

@Database(
    entities = [
        UserProfile::class,
        CompletedMission::class,
        SocialLabSession::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
