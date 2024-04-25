package ui.presentation.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.data.MongoDB
import ui.domain.RequestState
import ui.domain.Task
import ui.domain.TaskAction
import ui.network.ApiService

typealias MutableTasks = MutableState<RequestState<List<Task>>>
typealias Tasks = MutableState<RequestState<List<Task>>>

class HomeScreenViewModel(private val mongoDB: MongoDB, private val apiService: ApiService) : ScreenModel {
    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTasks: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading
        screenModelScope.launch(Dispatchers.IO) {
            val user = mongoDB.getUserDetails()
            user?.let {
                val tasks: List<Task> = apiService.getTasksForCurrentUser(user.token)
                mongoDB.saveTasks(tasks)
            }
            withContext(Dispatchers.Main) {
                delay(500)
                mongoDB.readActiveTasks().collectLatest {
                    _activeTasks.value = it
                }
            }
        }
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDB.readCompletedTasks().collectLatest {
                _completedTasks.value = it
            }
        }
    }

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Delete -> {
                deleteTask(action.task)
            }

            is TaskAction.SetCompleted -> {
                setCompleted(action.task, action.completed)
            }

            is TaskAction.SetFavorite -> {
                setFavorite(action.task, action.isFavorite)
            }
            is TaskAction.Logout -> {
                performLogout()
            }

            else -> {}
        }
    }

    private fun performLogout() {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.logoutUser()
        }
    }

    private fun setCompleted(task: Task, completed: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.setCompleted(task, completed)
            val user = mongoDB.getUserDetails()
            user?.let {
                apiService.setCompleted(it.token, task, completed)
            }
        }
    }

    private fun setFavorite(task: Task, isFavorite: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.setFavorite(task, isFavorite)
            val user = mongoDB.getUserDetails()
            user?.let {
                apiService.setFavorite(it.token, task, isFavorite)
            }
        }
    }

    private fun deleteTask(task: Task) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.deleteTask(task)
            val user = mongoDB.getUserDetails()
            user?.let {
                apiService.deleteTask(it.token, task)
            }
        }
    }
}