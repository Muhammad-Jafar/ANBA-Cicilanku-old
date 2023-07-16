package jafar.cicilan.ui.main.settings.about

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.databinding.MainSettingsAboutBinding

class AboutFragment : BaseFragment<MainSettingsAboutBinding>(MainSettingsAboutBinding::inflate) {

    override fun renderView(savedInstanceState: Bundle?) {
        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

}
