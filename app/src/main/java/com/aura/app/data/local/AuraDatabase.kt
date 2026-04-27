package com.aura.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aura.app.data.local.entities.CompletedMission
import com.aura.app.data.local.entities.JournalEntry
import com.aura.app.data.local.entities.MirrorSession
import com.aura.app.data.local.entities.SocialLabSession
import com.aura.app.data.local.entities.UserProfile

@Database(
    entities = [
        UserProfile::class,
        CompletedMission::class,
        SocialLabSession::class,
        JournalEntry::class,
        MirrorSession::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun journalDao(): JournalDao
    abstract fun mirrorDao(): MirrorDao
}
