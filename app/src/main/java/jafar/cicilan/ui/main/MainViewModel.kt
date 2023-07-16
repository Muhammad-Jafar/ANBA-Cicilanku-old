package jafar.cicilan.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jafar.cicilan.R
import jafar.cicilan.data.model.CalculateState
import jafar.cicilan.data.model.ItemLogEntity
import jafar.cicilan.data.model.Modal
import jafar.cicilan.data.repository.CicilanRepository
import jafar.cicilan.utils.runInBackground

open class MainViewModel : ViewModel()

class HomeViewModel(private val repo: CicilanRepository) : MainViewModel() {
    val getTotalCurrent get() = repo.getTotalCurrent
    val getTotalDone get() = repo.getTotalDone

    val getCurrentList get() = repo.getCurrentList
    val getDoneList get() = repo.getDoneList

    private val _perBulanValue = MutableLiveData(CalculateState())
    val perBulanValue get() = _perBulanValue
    var labaValue = 0

    fun calculate(harga: Int, dp: Int, periode: Int) {
        when {
            harga < 1 -> _perBulanValue.value = CalculateState(hargaError = R.string.fill_data)
            dp < 1 -> _perBulanValue.value = CalculateState(dpError = R.string.fill_data)
            dp > harga -> _perBulanValue.value =
                CalculateState(dpError = R.string.form_dp_lessThan_harga)

            periode < 1 -> _perBulanValue.value = CalculateState(periodeError = R.string.fill_data)
            else -> {
                val nominalMembayar = harga - dp
                val nominalPerBulan = (nominalMembayar / periode)
                val laba = (nominalMembayar * 0.05).toInt()
                _perBulanValue.value = CalculateState(isResultThere = nominalPerBulan)
                labaValue = laba
            }
        }
    }
}

class FormViewModel(private val repo: CicilanRepository) : MainViewModel() {
    var imageUri: Uri? = null
    fun add(add: Modal) = runInBackground { repo.insertData(add) }
    fun update(item: Modal) = runInBackground { repo.updateData(item) }
}

class DetailViewModel(private val repo: CicilanRepository) : MainViewModel() {
    var cicilanId = 0
    val getDetail get() = repo.getCicilanById(cicilanId)
    val getLog get() = repo.getListLogCicilan(cicilanId)

    fun updateNominal(item: ItemLogEntity) = runInBackground { repo.updateNominal(item) }
    fun delete(cicilanId: Int) = runInBackground { repo.delete(cicilanId) }
}

class SettingsViewModel(private val repo: CicilanRepository) : MainViewModel() {
    val getThemeValue = repo.getThemeValue
    fun saveThemeValue(value: Int) = runInBackground { repo.saveThemeValue(value) }

    val getLangValue = repo.getLangValue
    fun saveLangValue(value: String) = runInBackground { repo.saveLangValue(value) }
}
