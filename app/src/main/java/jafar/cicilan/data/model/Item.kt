package jafar.cicilan.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cicilan")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cicilan") val id_cicilan: Int?,
    @ColumnInfo(name = "dibuat_pada") val dibuat_pada: Long,
    @ColumnInfo(name = "lunas_pada") val lunas_pada: Long?,
    @ColumnInfo(name = "gambar_barang") val gambar_barang: String?,
    @ColumnInfo(name = "nama_penyicil") val nama_penyicil: String,
    @ColumnInfo(name = "nama_barang") val nama_barang: String,
    @ColumnInfo(name = "kategori") val kategori: String,
    @ColumnInfo(name = "harga_barang") val harga_barang: Int,
    @ColumnInfo(name = "uang_muka") val uang_muka: Int,
    @ColumnInfo(name = "nominal_bayar") val nominal_bayar: Int, // harga_barang - uang_muka
    @ColumnInfo(name = "nominal_lunas") val nominal_lunas: Int?, // nominal_per_bulan - nominal_bayar
    @ColumnInfo(name = "periode") val periode: Int,
    @ColumnInfo(name = "tenggat_bayar") val tenggat_bayar: Int,
    @ColumnInfo(name = "per_bulan") val per_bulan: Int, // nominal_bayar : periode (Pure utang tanpa laba)
    @ColumnInfo(name = "laba_per_bulan") val laba_per_bulan: Int, // nominal_bayar * 5%
    @ColumnInfo(name = "nominal_per_bulan") val nominal_per_bulan: Int, // per bulan + laba
    @ColumnInfo(name = "total_laba") val total_laba: Int,
    @ColumnInfo(name = "status") val status: String
) : Parcelable

@Parcelize
@Entity(tableName = "cicilanLog")
data class ItemLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_log") val id_log: Int?,
    @ColumnInfo(name = "id_cicilan") val id_cicilan: Int,
    @ColumnInfo(name = "tgl_transaksi") val tgl_transaksi: Long,
    @ColumnInfo(name = "nominal_transaksi") val nominal_transaksi: Int,
    @ColumnInfo(name = "catatan") val catatan: String?
) : Parcelable

@Parcelize
data class Modal(
    val id: Int?,
    val gambar_barang: String? = null,
    val nama_penyicil: String,
    val nama_barang: String,
    val kategori: String,
    val harga_barang: Int,
    val uang_muka: Int,
    val periode: Int,
    val tenggat_bayar: Int,
) : Parcelable

data class FormState(
    val harga: Int? = null,
    val dp: Int? = null,
    val periode: Int? = null,
    val tenggat: Int? = null,
    val isFormValid: Boolean = false
)

data class CalculateState(
    val hargaError: Int? = null,
    val dpError: Int? = null,
    val periodeError: Int? = null,
    val isResultThere: Int? = 0,
)