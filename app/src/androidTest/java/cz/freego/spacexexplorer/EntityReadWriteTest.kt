package cz.freego.spacexexplorer

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cz.freego.spacexexplorer.local.db.AppDatabase
import cz.freego.spacexexplorer.local.db.FavoriteCrewDao
import cz.freego.spacexexplorer.remote.data.response.Crew
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EntityReadWriteTest {
    private lateinit var favoriteCrewDao: FavoriteCrewDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        favoriteCrewDao = db.FavoriteCrewDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val crew = Crew(
            "00001",
            "John Doe",
            "NASA",
            "https://foo.com/img.jpg",
            "https://en.wikipedia.org/wiki/Main_Page",
            "active"
        )
        val crew2 = Crew(
            "00002",
            "James Brown",
            "ESA",
            "https://foo.com/img2.jpg",
            "https://en.wikipedia.org/wiki/Main_Page2",
            "unknown"
        )
        favoriteCrewDao.insertTest(crew)
        val crew3 = favoriteCrewDao.findByIdTest("00001")
        //assertEquals(crew, crew2) // FAILED
        assertEquals(crew, crew3) // PASSED
    }
}