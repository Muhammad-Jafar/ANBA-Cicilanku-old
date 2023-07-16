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
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel

    @Mock
    private lateinit var repo: CicilanRepository
    private val dummyTotalCurrent = Dummy.totalCurrent()
    private val dummyTotalDone = Dummy.totalDone()
    private val dummyModal = Dummy.modal()
    private val dummyLaba = Dummy.laba()
    private val dummyCurrentList = Dummy.listCurrent()
    private val dummyDoneList = Dummy.listDone()

    @Before
    fun setUp() {
        viewModel = HomeViewModel(repo)
    }

    @Test
    fun `Return value from total current`() {
        `when`(repo.getTotalCurrent).thenReturn(dummyTotalCurrent)
        val actualData = viewModel.getTotalCurrent

        verify(repo).getTotalCurrent
        assertNotNull(actualData)
        assertEquals(actualData, dummyTotalCurrent)
    }

    @Test
    fun `Return value from total done`() {
        `when`(repo.getTotalDone).thenReturn(dummyTotalDone)
        val actualData = viewModel.getTotalDone

        verify(repo).getTotalDone
        assertNotNull(actualData)
        assertEquals(actualData, dummyTotalDone)
    }

    @Test
    fun `Return value from modal`() {
        `when`(repo.getModal).thenReturn(dummyModal)
        val actualData = viewModel.getModal

        verify(repo).getModal
        assertNotNull(actualData)
        assertEquals(actualData, dummyModal)
    }

    @Test
    fun `Return value from laba`() {
        `when`(repo.getLaba).thenReturn(dummyLaba)
        val actualData = viewModel.getLaba

        verify(repo).getLaba
        assertNotNull(actualData)
        assertEquals(actualData, dummyLaba)
    }

    @Test
    fun `Return list when status NO`() {
        `when`(repo.getCurrentList).thenReturn(dummyCurrentList)
        val actualData = viewModel.getCurrentList

        verify(repo).getCurrentList
        assertNotNull(actualData)
        assertEquals(actualData, dummyCurrentList)
    }

    @Test
    fun `Return list when status YES`() {
        `when`(repo.getDoneList).thenReturn(dummyDoneList)
        val actualData = viewModel.getDoneList

        verify(repo).getDoneList
        assertNotNull(actualData)
        assertEquals(actualData, dummyDoneList)
    }
}
