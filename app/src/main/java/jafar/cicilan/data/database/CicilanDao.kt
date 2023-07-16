package jafar.cicilan.data.database

import androidx.room.*
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CicilanDao {

    /* CARD IN HOME FRAGMENT */
    @Query("SELECT COUNT (id_cicilan) FROM cicilan WHERE status = 'NO'")
    fun getTotalCicilanBerjalan(): Flow<Int?>

    @Query("SELECT COUNT (id_cicilan) FROM cicilan WHERE status = 'YES'")
    fun getTotalCicilanLunas(): Flow<Int?>

    /* TAB VIEW CICILAN FRAGMENT */
    @Query("SELECT * FROM cicilan WHERE status = 'NO' ORDER BY dibuat_pada DESC")
    fun getCicilanBerjalan(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM cicilan WHERE status = 'YES' ORDER BY dibuat_pada DESC")
    fun getCicilanLunas(): Flow<List<ItemEntity>>

    /* FORM ADD CICILAN  */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: ItemEntity)

    /* DETAIL CICILAN BERJALAN FRAGMENT */
    @Query("SELECT * FROM cicilan WHERE id_cicilan = :id")
    fun getCicilanById(id: Int): Flow<ItemEntity?>

    @Query("SELECT * FROM cicilanLog WHERE id_cicilan = :id ORDER BY tgl_transaksi DESC")
    fun getListLogCicilan(id: Int): Flow<List<ItemLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCicilanLog(itemLog: ItemLogEntity)

    @Query("SELECT nominal_bayar FROM cicilan WHERE id_cicilan = :id")
    fun getCurrentNominalBayar(id: Int): Int

    @Query("SELECT nominal_lunas FROM cicilan WHERE id_cicilan = :id")
    fun getCurrentNominalLunas(id: Int): Int

    @Query("UPDATE cicilan SET nominal_lunas = nominal_lunas + :nominal WHERE id_cicilan = :id")
    suspend fun setNominalLunas(id: Int, nominal: Int)

    @Query("UPDATE cicilan SET status = :status WHERE id_cicilan = :id")
    suspend fun setStatusLunas(id: Int, status: String)

    @Query("UPDATE cicilan SET lunas_pada = :lunasPada WHERE id_cicilan = :id")
    suspend fun setDateLunas(id: Int, lunasPada: Long)

    @Query("SELECT gambar_barang FROM cicilan WHERE id_cicilan = :id")
    suspend fun getImagePathById(id: Int): String?

    @Query("DELETE FROM cicilan WHERE id_cicilan = :id")
    suspend fun deleteFromCicilan(id: Int)

    @Query("DELETE FROM cicilanLog WHERE id_cicilan = :id")
    suspend fun deleteFromCicilanLog(id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)
}
