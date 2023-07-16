package jafar.cicilan.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jafar.cicilan.data.repository.CicilanRepository
import jafar.cicilan.utils.Dummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FormViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FormViewModel

    @Mock
    private lateinit var repo: CicilanRepository
    private val dummyData = Dummy.addData()


    @Before
    fun setUp() {
        viewModel = FormViewModel(repo)
    }

    @Test
    fun `Add data`() = runTest {
        `when`(repo.insertData(dummyData)).thenReturn(null)
        val actualData = viewModel.add(dummyData)

        verify(repo).insertData(dummyData)
        assertNull(actualData)
        assertEquals(actualData, dummyData)
    }

    @Test
    fun `Update data`() = runTest{
        `when`(repo.updateData(dummyData)).thenReturn(null)
        val actualData = viewModel.update(dummyData)

        verify(repo).updateData(dummyData)
        assertNull(actualData)
        assertEquals(actualData, dummyData)
    }
}