package com.example.diarydepresiku

/**
 * Represents whether a diary entry was successfully uploaded to the server
 * (ONLINE) or only stored locally due to a network failure (OFFLINE).
 */
enum class EntryStatus {
    ONLINE,
    OFFLINE
}
