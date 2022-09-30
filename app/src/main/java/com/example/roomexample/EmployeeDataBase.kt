package com.example.roomexample

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EmployeeEntity::class], version = 1)
abstract class EmployeeDataBase : RoomDatabase() {

    abstract  fun employeeDao():EmployeeDao

    companion object{
        //KEEP A REFERENCE OF THE DATABASE
        @Volatile
        private var INSTANCE: EmployeeDataBase? = null

        fun getInstance(context: Context): EmployeeDataBase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EmployeeDataBase::class.java,
                        "employee_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance;
            }
        }
    }
}