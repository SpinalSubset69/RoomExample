package com.example.roomexample

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employeeEntity: EmployeeEntity)

    @Update
    suspend fun update(employeeEntity: EmployeeEntity)

    @Delete
    suspend fun delete(employeeEntity: EmployeeEntity)

    //Flow changes at runtime, its not necessary to notify de Recycle Viewer there was changes
    @Query("SELECT * FROM `employee-table`")
    fun getEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM `employee-table`  WHERE id=:id")
    fun getEmployeeById(id: Int): Flow<EmployeeEntity>

}