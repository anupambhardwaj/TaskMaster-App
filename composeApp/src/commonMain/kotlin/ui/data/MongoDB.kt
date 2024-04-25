package ui.data

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ui.domain.RequestState
import ui.domain.Task
import ui.model.UserDetails

class MongoDB {

    private var realm: Realm? = null;

    init {
        configureRealmDb()
    }

    private fun configureRealmDb() {
        if (realm == null || realm?.isClosed() == true) {
            val config = RealmConfiguration
                .Builder(
                    schema = setOf(Task::class, UserDetails::class)
                )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    fun readActiveTasks(): Flow<RequestState<List<Task>>> {
        return realm?.query<Task>(query = "completed == $0", false)
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list.sortedByDescending { task -> task.pinned }
                )
            } ?: flow { RequestState.Error(message = "Realm is not available.") }
    }

    fun readCompletedTasks(): Flow<RequestState<List<Task>>> {
        return realm?.query<Task>(query = "completed == $0", true)
            ?.asFlow()
            ?.map { result -> RequestState.Success(data = result.list) }
            ?: flow { RequestState.Error(message = "Realm is not available.") }
    }

    suspend fun addTask(task: Task) {
        realm?.write { copyToRealm(task) }
    }

    suspend fun updateTask(task: Task) {
        realm?.write {
            try {
                val queriedTask = query<Task>("_id == $0", task._id)
                    .first()
                    .find()
                queriedTask?.let {
                    findLatest(it)?.let { currentTask ->
                        currentTask.title = task.title
                        currentTask.description = task.description
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun setCompleted(task: Task, taskCompleted: Boolean) {
        realm?.write {
            try {
                val queriedTask = query<Task>(query = "_id == $0", task._id)
                    .find()
                    .first()
                queriedTask.apply { completed = taskCompleted }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun setFavorite(task: Task, isPinned: Boolean) {
        realm?.write {
            try {
                val queriedTask = query<Task>(query = "_id == $0", task._id)
                    .find()
                    .first()
                queriedTask.apply { pinned = isPinned }
                val task = query<Task>(query = "_id == $0", task._id)
                    .find()
                    .first()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun deleteTask(task: Task) {
        realm?.write {
            try {
                val queriedTask = query<Task>(query = "_id == $0", task._id)
                    .first()
                    .find()
                queriedTask?.let {
                    findLatest(it)?.let { currentTask ->
                        delete(currentTask)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun saveUserDetails(user: UserDetails) {
        realm?.write {
            copyToRealm(user)
        }
    }

    suspend fun getUserDetails(): UserDetails? {
        val userDetails = realm?.query<UserDetails>()?.first()?.find()
        return userDetails
    }

    suspend fun updateUserDetails(user: UserDetails) {
        val existingUser = getUserDetails()
        if (existingUser == null) {
            saveUserDetails(user)
            return
        } else {
            realm?.write {
                val userDetails = realm?.query<UserDetails>()?.first()?.find()
                userDetails?.let {
                    findLatest(it)?.apply {
                        token = user.token
                    }
                }
            }
        }
    }

    suspend fun logoutUser() {
        realm?.write {
            val userDetails = realm?.query<UserDetails>()?.first()?.find()
            userDetails?.let {
                findLatest(it)?.apply {
                    token = ""
                }
            }
        }
    }

    suspend fun saveTasks(tasks: List<Task>) {
        tasks.forEach {
            val ifTaskExist = realm?.query<Task>(query = "_id == $0", it._id)
                ?.first()
                ?.find()
            if (ifTaskExist == null)
                realm?.write { copyToRealm(it) }
        }
    }

}