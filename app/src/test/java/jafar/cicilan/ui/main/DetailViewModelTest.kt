package jafar.cicilan.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jafar.cicilan.data.repository.CicilanRepository
import jafar.cicilan.utils.Dummy
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
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel

    @Mock
    private lateinit var repo: CicilanRepository
    private val dummyDetail = Dummy.getDetail()
    private val dummyLog = Dummy.getLog()

    @Before
    fun setUp() {
        viewModel = DetailViewModel(repo)
    }

    @Test
    fun `Return detail data when id != null`() {
        `when`(repo.getCicilanById(0)).thenReturn(dummyDetail)
        val actualData = viewModel.getDetail

        verify(repo).getCicilanById(0)
        assertNotNull(true)
        assertEquals(actualData, dummyDetail)
    }

    @Test
    fun `Return log detail data when id != null`() {
        `when`(repo.getListLogCicilan(0)).thenReturn(dummyLog)
        val actualData = viewModel.getLog

        verify(repo).getListLogCicilan(0)
        assertNotNull(true)
        assertEquals(actualData, dummyLog)
    }

    @Test
    fun updateNominal() {

    }

    @Test
    fun delete() {
    }
}