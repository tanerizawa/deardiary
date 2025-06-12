package com.example.diarydepresiku

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entity representing a badge/achievement earned by the user. */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val name: String, // unique badge name
    val earnedTimestamp: Long
)
