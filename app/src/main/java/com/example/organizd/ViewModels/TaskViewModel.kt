package com.example.organizd.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.organizd.db.Task
import com.example.organizd.db.TaskDatabase
import com.example.organizd.db.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application, date: String): AndroidViewModel(application) {

    val redAllDoData: LiveData<List<Task>>
    val redAllDoneData: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        redAllDoData = repository.readDayDoTask(date)
        redAllDoneData = repository.readDayDoneTask(date)
    }

    fun addTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }
}