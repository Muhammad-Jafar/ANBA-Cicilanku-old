package jafar.cicilan.data.repository

import android.net.Uri
import androidx.room.withTransaction
import jafar.cicilan.data.database.CicilanDao
import jafar.cicilan.data.database.CicilanDatabase
import jafar.cicilan.data.local.StoreData
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity
import jafar.cicilan.data.model.Modal
import jafar.cicilan.utils.currentDate
import kotlinx.coroutines.runBlocking
import java.io.File

class CicilanRepository(
    private val dao: CicilanDao,
    private val db: CicilanDatabase,
    private val store: StoreData
) {
    val getTotalCurrent = dao.getTotalCicilanBerjalan()
    val getTotalDone = dao.getTotalCicilanLunas()

    val getCurrentList = dao.getCicilanBerjalan()
    val getDoneList = dao.getCicilanLunas()

    fun getCicilanById(id: Int) = dao.getCicilanById(id)
    fun getListLogCicilan(id: Int) = dao.getListLogCicilan(id)

    suspend fun insertData(add: Modal) {
        val nominalMembayar = add.harga_barang - add.uang_muka
        val perBulan = nominalMembayar / add.periode
        val laba = (nominalMembayar * 0.05).toInt()
        val totalLaba = laba * add.periode
        val nominalPerBulan = (perBulan + laba)
        val item = ItemEntity(
            add.id, currentDate, null, add.gambar_barang, add.nama_penyicil,
            add.nama_barang, add.kategori, add.harga_barang, add.uang_muka, nominalMembayar,
            0, add.periode, add.tenggat_bayar, perBulan, laba, nominalPerBulan, totalLaba, "NO"
        )
        dao.insertItem(item)
    }

    suspend fun updateData(update: Modal) {
        if (update.id != null) {
            val set = dao.getCicilanById(update.id)
            set.collect {
                val image = if (update.gambar_barang == it?.gambar_barang) it?.gambar_barang
                else update.gambar_barang
                val nominalMembayar = update.harga_barang - update.uang_muka
                val perBulan = nominalMembayar / update.periode
                val laba = (nominalMembayar * 0.05).toInt()
                val totalLaba = laba * update.periode
                val nominalPerBulan = (perBulan + laba)

                val item = ItemEntity(
                    it?.id_cicilan, it?.dibuat_pada!!, null, image, update.nama_penyicil,
                    update.nama_barang, update.kategori, update.harga_barang, update.uang_muka,
                    nominalMembayar, it.nominal_lunas, update.periode, update.tenggat_bayar,
                    perBulan, laba, nominalPerBulan, totalLaba, "NO"
                )
                dao.updateItem(item)
            }
        }
    }

    suspend fun updateNominal(itemLog: ItemLogEntity) {
        val nominalBayar = dao.getCurrentNominalBayar(itemLog.id_cicilan)
        val nominalLunas = dao.getCurrentNominalLunas(itemLog.id_cicilan)
        val currentNominaLunas = itemLog.nominal_transaksi + nominalLunas
        if (currentNominaLunas == nominalBayar) {
            runBlocking {
                db.withTransaction {
                    dao.setNominalLunas(itemLog.id_cicilan, itemLog.nominal_transaksi)
                    dao.addCicilanLog(itemLog)
                }
                db.withTransaction {
                    dao.setStatusLunas(itemLog.id_cicilan, "YES")
                    dao.setDateLunas(itemLog.id_cicilan, currentDate)
                }
            }
        } else {
            db.withTransaction {
                dao.setNominalLunas(itemLog.id_cicilan, itemLog.nominal_transaksi)
                dao.addCicilanLog(itemLog)
            }
        }
    }

    suspend fun delete(id: Int) {
        db.withTransaction {
            dao.deleteFromCicilan(id)
            dao.deleteFromCicilanLog(id)
        }
        dao.getImagePathById(id)?.also {
            Uri.parse(it).path?.let { path -> File(path).delete() }
        }
    }

    val getThemeValue = store.getThemeValue()
    suspend fun saveThemeValue(value: Int) = store.saveThemeValue(value)

    val getLangValue = store.getLangValue()
    suspend fun saveLangValue(value: String) = store.saveLangValue(value)
}
