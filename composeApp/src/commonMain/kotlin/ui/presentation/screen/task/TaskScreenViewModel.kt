package ui.presentation.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ui.data.MongoDB
import ui.domain.Task
import ui.domain.TaskAction
import ui.network.ApiService

class TaskScreenViewModel(
    private val mongoDB: MongoDB,
    private val apiService: ApiService
): ScreenModel {

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Add -> {
                println("action.task: ${action.task.toString()}")
                addTask(action.task)
                addTaskToNetwork(action.task)
            }

            is TaskAction.Update -> {
                updateTask(action.task)
            }

            else -> {}
        }
    }

    private fun addTaskToNetwork(task: Task) {
        screenModelScope.launch(Dispatchers.IO) {
            val user = mongoDB.getUserDetails()
            user?.let {
                apiService.addTask(task, it.token)
            }
        }
    }

    private fun addTask(task: Task) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.addTask(task)
        }
    }

    private fun updateTask(task: Task) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.updateTask(task)
            val user = mongoDB.getUserDetails()
            user?.let {
                apiService.updateTask(task, it.token)
            }
        }
    }
}