package jafar.cicilan.ui.main.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.databinding.MainSettingsBinding
import jafar.cicilan.ui.main.SettingsViewModel
import jafar.cicilan.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<MainSettingsBinding>(MainSettingsBinding::inflate) {

    override fun renderView(savedInstanceState: Bundle?) {
        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            if (savedInstanceState == null) childFragmentManager.beginTransaction()
                .replace(R.id.settingMenuContainer, MenuSettingsFragment()).commit()
        }
    }
}

class MenuSettingsFragment : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>(getString(R.string.key_theme))?.setOnPreferenceChangeListener { theme, newValue ->
            if (theme is ListPreference) {
                val index = theme.findIndexOfValue(newValue.toString())
                val entry = theme.entries[index]
                val entryValue = theme.entryValues[index]

                when {
                    entryValue.equals("Light") -> {
                        theme.value = entry.toString()
                        viewModel.saveThemeValue(1)
                        setDefaultNightMode(MODE_NIGHT_NO)
                    }
                    entryValue.equals("Dark") -> {
                        theme.value = entry.toString()
                        viewModel.saveThemeValue(2)
                        setDefaultNightMode(MODE_NIGHT_YES)
                    }
                    else -> {
                        theme.value = entry.toString()
                        viewModel.saveThemeValue(-1)
                        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)

                    }
                }
            }
            true
        }

        findPreference<Preference>(getString(R.string.key_language))?.setOnPreferenceChangeListener { lang, newValue ->
            if (lang is ListPreference) {
                val index = lang.findIndexOfValue(newValue.toString())
                val entry = lang.entries[index]
                val entryValue = lang.entryValues[index]

                fun setLanguage(lang: String?) =
                    setApplicationLocales(LocaleListCompat.forLanguageTags(lang))

                when {
                    entryValue.equals("in") -> {
                        lang.value = entry.toString()
                        viewModel.saveLangValue("in")
                        setLanguage("in")
                    }
                    else -> {
                        lang.value = entry.toString()
                        viewModel.saveLangValue("en-US")
                        setLanguage("en-US")
                    }
                }
            }
            true
        }

        findPreference<Preference>(getString(R.string.key_feedback))?.setOnPreferenceClickListener {
            showToast("Thank you for your feedback :)")
            true
        }

        findPreference<Preference>(getString(R.string.key_rating))?.setOnPreferenceClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData("https://play.google.com/store/apps/details?id=id.celenganku.app".toUri())
                    .setPackage("com.android.vending")
            )
            true
        }

        findPreference<Preference>(getString(R.string.key_about))?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_about)
            true
        }
    }
}
