package jafar.cicilan.base

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.animation.doOnEnd
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding
import jafar.cicilan.databinding.ActivityMainBinding
import jafar.cicilan.ui.main.SettingsViewModel
import jafar.cicilan.utils.runWhenCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var navHost: NavHostFragment
    private val viewModel: SettingsViewModel by viewModel()
    private val gravity: Int = Gravity.BOTTOM
    private val isSystemBarUsed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWindowInsets()
//        navHost = supportFragmentManager.findFragmentById(R.id.navHostMain) as NavHostFragment? ?: return
    }

    private fun initSplashScreen() {
        installSplashScreen().setKeepOnScreenCondition {
            runWhenCreated {
                val value = viewModel.getLangValue.first()
                withContext(Dispatchers.Main) {
                    setApplicationLocales(LocaleListCompat.forLanguageTags(value))
                }
            }
            runWhenCreated {
                val mode = when (viewModel.getThemeValue.first()) {
                    1 -> MODE_NIGHT_NO
                    2 -> MODE_NIGHT_YES
                    else -> MODE_NIGHT_FOLLOW_SYSTEM
                }
                withContext(Dispatchers.Main) { setDefaultNightMode(mode) }
            }
            false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { viewProvider ->
                val slideUp = ObjectAnimator.ofFloat(
                    viewProvider, View.TRANSLATION_Y,
                    0f, -viewProvider.height.toFloat()
                )
                with(slideUp) {
                    interpolator = AccelerateInterpolator()
                    duration = 600L
                    doOnEnd { viewProvider.remove() }
                    start()
                }
            }
        }
    }

    private fun initWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val imeVisible = windowInsets.isVisible(Type.ime())
            val imeHeight = windowInsets.getInsets(Type.ime()).bottom

            val insetsSystemBar = windowInsets.getInsets(Type.systemBars())
            val insetsSystemGesture = windowInsets.getInsets(Type.systemGestures())

            if (isSystemBarUsed) when (gravity) {
                Gravity.TOP -> view.updatePadding(top = insetsSystemBar.top)
                Gravity.BOTTOM -> {
                    if (imeVisible) view.updatePadding(bottom = insetsSystemBar.bottom + imeHeight)
                    else view.updatePadding(bottom = insetsSystemBar.bottom)
                }
            }
            else when (gravity) {
                Gravity.TOP -> view.updatePadding(top = insetsSystemGesture.top)
                Gravity.BOTTOM -> {
                    if (imeVisible) view.updatePadding(bottom = insetsSystemBar.bottom + imeHeight)
                    else view.updatePadding(bottom = insetsSystemBar.bottom)
                }
            }
            windowInsets
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        navHost.navController.navigateUp()
//        return super.onSupportNavigateUp()
//    }
}
