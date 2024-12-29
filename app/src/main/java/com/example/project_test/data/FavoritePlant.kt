package com.example.project_test.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project_test.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type

@SuppressLint("MutableCollectionMutableState")
object FavoritePlants {
    private const val filePath = "favorites.json"
    private lateinit var file: File
    private val gson = Gson()
    private lateinit var database: AppDatabase
    private val json = Json { ignoreUnknownKeys = true }

    // List of all favorite plants
    var plants: SnapshotStateList<FavoritePlant> = mutableStateListOf()
        private set
    var removedPlants: MutableList<FavoritePlant> by mutableStateOf(mutableListOf())
        private set

    // List of all common names, nicknames, and latin names of favorite plants
    val favoriteNames: MutableList<String> by lazy {
        buildList {
            for (plant in plants) {
                add(plant.name)
                add(plant.nickname)
                add(plant.latinName)
                for (otherName in plant.otherNames) {
                    add(otherName)
                }
            }
        }.toMutableList() // Convert to MutableList
    }

    // Read favorite plants from JSON file
    fun initFavorites(context: Context): Error? {
        file = File(context.getExternalFilesDir(null), filePath)

        return if (file.exists()) {
            try {
                // Load favorite plants from JSON file
                BufferedReader(FileReader(file)).use { reader ->
                    val json = reader.readText()
                    val type: Type = object : TypeToken<MutableList<FavoritePlant>>() {}.type
                    val loadedPlants: List<FavoritePlant> = gson.fromJson(json, type) ?: mutableListOf()
                    plants.clear()
                    plants.addAll(loadedPlants)
                }

                //plants.clear()
                //plants.addAll(testPlants) // TODO remove when adding plants has been implemented
                favoriteNames // Initialise favoriteNames

                writeToFile()
                null
            } catch (e: IOException) {
                Error("Failed to fetch your favorite plants.")
            }
        } else {
            try { // Create new file if one does not already exist
                file.createNewFile()
                null
            } catch (e: IOException) { // Return error if file cannot be created
                Error("Failed to fetch your favorite plants.")
            }
        }
    }

    // Entity that represents a db table,
    // where favourite plants are permanently stored
    @Entity(tableName = "favorite_plants_store")
    data class FavoritePlantEntity(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "data") val data: String // JSON string of plant data
    )

    // DAO for database operations
    @Dao
    interface FavoritePlantDao {
        @Query("SELECT * FROM favorite_plants_store WHERE id = :id")
        suspend fun getFavoritePlantStore(id: Int): FavoritePlantEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertFavoritePlantStore(plant: FavoritePlantEntity)

        @Query("DELETE FROM favorite_plants_store WHERE id = :id")
        suspend fun deleteFavoritePlantStore(id: Int)
    }

    // Room database to permanently store plant data of favourited plants
    @Database(entities = [FavoritePlantEntity::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun favoritePlantDao(): FavoritePlantDao

        companion object {
            private var instance: AppDatabase? = null

            fun getInstance(context: Context): AppDatabase {
                return instance ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }
            }

            private fun buildDatabase(context: Context): AppDatabase {
                return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "favorite_plants_store.db"
                ).build()
            }
        }
    }

    // Add new favorite plant
    /*
    suspend fun addFavorite(plant: PlantDetailResponse): Error? {
        // Limit favorites to 100 plants
        return if (plants.size < 100) {
            val newFavorite = FavoritePlant(
                id = plant.id,
                otherNames = plant.other_name ?: listOf("None"),
                family = plant.family.orEmpty(),
                name = plant.common_name.orEmpty(), // Handle null common names
                latinName = plant.scientific_name?.firstOrNull().orEmpty(), // Handle null or empty list
                imageURL = plant.default_image?.thumbnail.toString(),
                origin = plant.origin,
                flowerColor = plant.flower_color?.toString() ?: "",
                leafColor = plant.leaf_color?.toString() ?: "",
                edible = plant.edible_fruit == true || plant.edible_leaf == true, // Default to false if null
            )

            try { // Try to add the new plants to the list
                if (removedPlants.any { it.id == newFavorite.id }) {
                    removedPlants.removeIf { it.id == newFavorite.id }
                } else {
                    plants.add(newFavorite)

                    // Adding names
                    favoriteNames.add(newFavorite.name)
                    favoriteNames.add(newFavorite.nickname)
                    favoriteNames.add(newFavorite.latinName)
                    for (otherName in newFavorite.otherNames) {
                        favoriteNames.add(otherName)
                    }
                }

                if (plant.id != null) {
                    // Try adding plant data to the database as well
                    val dao = database.favoritePlantDao()
                    dao.insertFavoritePlantStore(FavoritePlantEntity(
                        plant.id,
                        json.encodeToString(PlantDetailResponse.serializer(), plant)
                    ))
                    Log.i("PLANT_DEBUG", "Added plant with id ${plant.id} to database")
                }

            } catch (e: Exception) {
                return Error("Failed to add the plant to favorites.")
            }

            // Write updated list to file
            writeToFile()?.let { error ->
                plants.remove(newFavorite) // Rollback on failure
                return error
            }
            null // Return null if no errors
        } else {
            Error("You have reached the limit of 100 favorite plants.")
        }
    }*/


    suspend fun removeFavorite(plant: FavoritePlant): Error? {
        try {
            removedPlants.add(plant)
            writeToFile() // Write to file upon success
        } catch (e: IOException) {
            return Error("Failed to remove plant")
        }

        try {
            if (plant.id != null) {
                val dao = database.favoritePlantDao()
                dao.deleteFavoritePlantStore(plant.id!!)
            }
            Log.i("PLANT_DEBUG", "Removed plant with id ${plant.id} from database")
        } catch (e: Exception) {
            return Error("Failed to remove plant from favourites database")
        }

        return null
    }

    fun editPlantName(plant: FavoritePlant, nickname: String): Error? {
        return try {
            plant.nickname = nickname
            writeToFile()
            null
        } catch (e: IOException){
            Error("Failed to update the plant name...")
        }
    }

    fun isPlantFavorite(id: Int): Boolean {
        return plants.any { it.id == id } && removedPlants.none { it.id == id }
    }

    // Write all favorite plants to app-specific storage
    fun writeToFile(): Error? {
        return try {
            val favorites = plants - removedPlants.toSet() // Filter removed plants
            // Convert [plants] to JSON string and write to favorites.json
            BufferedWriter(FileWriter(file)).use { writer ->
                val gson = Gson()
                val json = gson.toJson(favorites)
                writer.write(json)
            }
            null
        } catch (e: IOException) {
            Error("Failed to save plants to file")
        }
    }
}

class FavoritePlant(
    // TODO update fields to match API
    var name: String = "",
    var nickname: String = "",
    var family: String = "",
    var latinName: String = "",
    var imageURL: Int = -1,
    var thumbnailURL: String = "",
    var otherNames: List<String> = listOf(),
    var edible: Boolean?,
    var origin: List<String>? = listOf(),
    val flowerColor:  String? = "",
    val leafColor: String? = "",
    var id: Int? = null
)

// Test data generated by ChatGPT
val testPlants = mutableListOf(
    FavoritePlant(
        name = "Rose",
        nickname = "",
        family = "Rosaceae",
        latinName = "Rosa",
        imageURL = R.drawable.rose,
        otherNames = listOf("Rosa", "Queen of Flowers"),
        edible = true,
        origin = listOf("Asia", "Europe", "North America"),
        flowerColor = "Red, Pink, White, Yellow",
        id = 1
    ),
    FavoritePlant(
        name = "Sunflower",
        nickname = "",
        family = "Asteraceae",
        latinName = "Helianthus annuus",
        imageURL = R.drawable.sunflower,
        otherNames = listOf("Common Sunflower"),
        edible = true,
        origin = listOf("North America"),
        flowerColor = "Yellow",
        id = 2
    ),
    FavoritePlant(
        name = "Tulip",
        nickname = "",
        family = "Liliaceae",
        latinName = "Tulipa",
        imageURL = R.drawable.tulip,
        otherNames = listOf("Tulipan", "Tulipe"),
        edible = false,
        origin = listOf("Central Asia", "Turkey"),
        flowerColor = "Red, Pink, Yellow, White, Purple",
        id = 3
    ),
    FavoritePlant(
        name = "Daffodil",
        nickname = "",
        family = "Amaryllidaceae",
        latinName = "Narcissus",
        imageURL = R.drawable.daffodill,
        otherNames = listOf("Jonquil", "Paperwhite"),
        edible = false,
        origin = listOf("Europe", "North Africa"),
        flowerColor = "Yellow, White",
        id = 4
    ),
    FavoritePlant(
        name = "Lily",
        nickname = "",
        family = "Liliaceae",
        latinName = "Lilium",
        imageURL = R.drawable.lilly,
        otherNames = listOf("True Lily"),
        edible = false,
        origin = listOf("Northern Hemisphere"),
        flowerColor = "White, Yellow, Orange, Pink, Red",
        id = 5
    ),
    FavoritePlant(
        name = "Orchid",
        nickname = "",
        family = "Orchidaceae",
        latinName = "Orchidaceae",
        imageURL = R.drawable.orchid,
        otherNames = listOf("Orchid"),
        edible = false,
        origin = listOf("Worldwide"),
        flowerColor = "Pink, Purple, White, Yellow",
        id = 6
    ),
    FavoritePlant(
        name = "Daisy",
        nickname = "",
        family = "Asteraceae",
        latinName = "Bellis perennis",
        imageURL = R.drawable.daisy,
        otherNames = listOf("Common Daisy", "Lawn Daisy"),
        edible = true,
        origin = listOf("Europe", "North America"),
        flowerColor = "White, Yellow",
        id = 7
    ),
    FavoritePlant(
        name = "Marigold",
        nickname = "",
        family = "Asteraceae",
        latinName = "Tagetes",
        imageURL = R.drawable.marigold,
        otherNames = listOf("Calendula"),
        edible = true,
        origin = listOf("North America", "South America"),
        flowerColor = "Orange, Yellow",
        id = 8
    ),
    FavoritePlant(
        name = "Lavender",
        nickname = "",
        family = "Lamiaceae",
        latinName = "Lavandula",
        imageURL = R.drawable.lavender,
        otherNames = listOf("Lavandula", "Lavandin"),
        edible = true,
        origin = listOf("Mediterranean", "Middle East", "India"),
        flowerColor = "Purple",
        id = 9
    ),
    FavoritePlant(
        name = "Peony",
        nickname = "",
        family = "Paeoniaceae",
        latinName = "Paeonia",
        imageURL = R.drawable.peony,
        otherNames = listOf("Peony Rose"),
        edible = false,
        origin = listOf("Europe", "Asia", "Western North America"),
        flowerColor = "Pink, Red, White",
        id = 10
    )
)
