package jafar.cicilan.utils

import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity
import jafar.cicilan.data.model.Modal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

object Dummy {

    /* HOME VIEW MODEL */
    fun totalCurrent(): Flow<Int> = flowOf(1)
    fun totalDone(): Flow<Int> = flowOf(2)

    fun modal(): Flow<Int> = flowOf(10000)
    fun laba(): Flow<Int> = flowOf(5000)

    fun listCurrent(): Flow<List<ItemEntity>> = flow {
        emit(
            listOf(
                ItemEntity(
                    1,
                    15032023,
                    null,
                    null,
                    "Abang",
                    "MBP Pro 14",
                    "Laptop",
                    2000000,
                    500000,
                    375000,
                    0,
                    5,
                    10,
                    300000,
                    75000,
                    375000,
                    375000,
                    "NO"
                )
            )
        )
    }

    fun listDone(): Flow<List<ItemEntity>> = flow {
        emit(
            listOf(
                ItemEntity(
                    2,
                    15032023,
                    null,
                    null,
                    "Abang",
                    "MBP Pro 14",
                    "Laptop",
                    2000000,
                    500000,
                    375000,
                    0,
                    5,
                    10,
                    300000,
                    75000,
                    375000,
                    375000,
                    "YES"
                )
            )
        )
    }

    /* SETTINGS VIEW MODEL */
    fun themeValue(): Flow<Int> = flowOf(1)
    fun langValue(): Flow<String> = flowOf("in")

    /* FORM VIEW MODEL */
    fun addData() = Modal(
        3,
        null,
        "Abang",
        "MBP Pro 14",
        "Laptop",
        2000000,
        500000,
        5,
        10,
    )

    /* DETAIL VIEW MODEL */
    fun getDetail(): Flow<ItemEntity> = flow {
        emit(
            ItemEntity(
                5,
                15032023,
                null,
                null,
                "Abang",
                "MBP Pro 14",
                "Laptop",
                2000000,
                500000,
                375000,
                0,
                5,
                10,
                300000,
                75000,
                375000,
                375000,
                "NO"
            )

        )
    }

    fun getLog(): Flow<List<ItemLogEntity>> = flow {
        emit(
            listOf(
                ItemLogEntity(
                    5,
                    15032023,
                    3182023,
                    10000,
                    null,
                )
            )
        )
    }
}