package jafar.cicilan.base

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import jafar.cicilan.data.database.CicilanDatabase
import jafar.cicilan.data.local.StoreData
import jafar.cicilan.data.repository.CicilanRepository
import jafar.cicilan.ui.main.DetailViewModel
import jafar.cicilan.ui.main.FormViewModel
import jafar.cicilan.ui.main.HomeViewModel
import jafar.cicilan.ui.main.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        val databaseModule = module {
            single {
                Room.databaseBuilder(
                    androidContext(), CicilanDatabase::class.java, "cicilan_database"
                ).build()
            }
            single { get<CicilanDatabase>().cicilanDao() }
        }

        val dataStoreModule = module {
            single {
                PreferenceDataStoreFactory.create {
                    androidContext().preferencesDataStoreFile("CicilanDataStore")
                }
            }
            single { StoreData(get()) }
        }

        val repositoryModule = module {
            single { CicilanRepository(get(), get(), get()) }
        }

        val viewModelModule = module {
            viewModel { HomeViewModel(get()) }
            viewModel { FormViewModel(get()) }
            viewModel { DetailViewModel(get()) }
            viewModel { SettingsViewModel(get()) }
        }

        startKoin {
            androidContext(this@BaseApp)
            modules(listOf(databaseModule, dataStoreModule, repositoryModule, viewModelModule))
        }
    }
}
