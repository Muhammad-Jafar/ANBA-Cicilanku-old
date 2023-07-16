package jafar.cicilan.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jafar.cicilan.data.repository.CicilanRepository
import jafar.cicilan.utils.Dummy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel

    @Mock
    private lateinit var repo: CicilanRepository
    private val dummyTheme = Dummy.themeValue()
    private val dummyLang = Dummy.langValue()

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(repo)
    }

    @Test
    fun `Get theme value`() = runTest {
        `when`(repo.getThemeValue).thenReturn(dummyTheme)
        val actualData = viewModel.getThemeValue

        verify(repo).getThemeValue
        assertNotNull(false)
        assertEquals(actualData, dummyTheme)
    }

}