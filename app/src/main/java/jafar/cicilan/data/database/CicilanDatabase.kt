package jafar.cicilan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity

@Database(
    entities = [ItemEntity::class, ItemLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CicilanDatabase : RoomDatabase() {
    abstract fun cicilanDao(): CicilanDao
}
