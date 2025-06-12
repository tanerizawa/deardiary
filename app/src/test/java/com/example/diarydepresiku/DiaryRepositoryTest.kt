package com.example.diarydepresiku

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody

// Status flag for uploads
import com.example.diarydepresiku.EntryStatus

class FakeDiaryDao : DiaryDao {
    val entries = mutableListOf<DiaryEntry>()
    override suspend fun insertEntry(entry: DiaryEntry) { entries.add(entry) }
    override suspend fun updateEntry(entry: DiaryEntry) {}
    override suspend fun deleteEntry(entry: DiaryEntry) { entries.remove(entry) }
    override suspend fun getEntryById(id: Int): DiaryEntry? = entries.find { it.id == id }
    override fun getAllEntries(): Flow<List<DiaryEntry>> = flowOf(entries)
    override fun getEntriesInRange(start: Long, end: Long): Flow<List<DiaryEntry>> =
        flowOf(entries.filter { it.creationTimestamp in start..end })
}

class FakeAchievementDao : AchievementDao {
    val achievements = mutableListOf<Achievement>()
    override fun getAllAchievements(): Flow<List<Achievement>> = flowOf(achievements)
    override suspend fun insertAchievement(achievement: Achievement) { achievements.add(achievement) }
}

class FakeDiaryApi : DiaryApi {
    var posted: DiaryEntryRequest? = null
    override suspend fun postEntry(entry: DiaryEntryRequest): Response<DiaryEntryResponse> {
        posted = entry
        return Response.success(DiaryEntryResponse(1, entry.content, entry.mood, entry.timestamp, entry.activities))
    }
    override suspend fun getMoodStats(): Response<MoodStatsResponse> {
        return Response.success(MoodStatsResponse(mapOf("Senang" to 1)))
    }
}

class FailingDiaryApi : DiaryApi {
    override suspend fun postEntry(entry: DiaryEntryRequest): Response<DiaryEntryResponse> {
        return Response.error(500, okhttp3.ResponseBody.create(null, ""))
    }

    override suspend fun getMoodStats(): Response<MoodStatsResponse> {
        return Response.success(MoodStatsResponse(emptyMap()))
    }
}

class DiaryRepositoryTest {
    @Test
    fun addEntry_savesLocallyAndCallsApi() = runBlocking {
        val dao = FakeDiaryDao()
        val api = FakeDiaryApi()
        val repository = DiaryRepository(dao, api, FakeAchievementDao())

        val status = repository.addEntry("Hello", "Senang", listOf("Bekerja"))

        assertEquals(1, dao.entries.size)
        assertEquals("Hello", dao.entries[0].content)
        assertEquals(listOf("Bekerja"), dao.entries[0].activities)
        assertEquals("Senang", api.posted?.mood)
        assertEquals(EntryStatus.ONLINE, status)
    }

    @Test
    fun addEntry_returnsOfflineOnFailure() = runBlocking {
        val dao = FakeDiaryDao()
        val api = FailingDiaryApi()
        val repository = DiaryRepository(dao, api, FakeAchievementDao())

        val status = repository.addEntry("Hi", "Sedih", emptyList())

        assertEquals(1, dao.entries.size)
        assertEquals(EntryStatus.OFFLINE, status)
    }

    @Test
    fun getMoodStats_returnsApiData() = runBlocking {
        val repository = DiaryRepository(FakeDiaryDao(), FakeDiaryApi(), FakeAchievementDao())

        val stats = repository.getMoodStats()

        assertEquals(mapOf("Senang" to 1), stats)
    }

    @Test
    fun getEntriesForDay_filtersByTimestamp() = runBlocking {
        val dao = FakeDiaryDao()
        val api = FakeDiaryApi()
        val repository = DiaryRepository(dao, api, FakeAchievementDao())

        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()
        dao.entries.add(
            DiaryEntry(1, "A", "Senang", emptyList(), today.atStartOfDay(zone).toInstant().toEpochMilli())
        )
        dao.entries.add(
            DiaryEntry(2, "B", "Sedih", emptyList(), today.minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli())
        )

        val result = repository.getEntriesForDay(today)

        assertEquals(1, result.first().size)
        assertEquals("A", result.first()[0].content)
    }
}
