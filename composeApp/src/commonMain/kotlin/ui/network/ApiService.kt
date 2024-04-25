package ui.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.json.Json
import ui.domain.Task
import ui.model.TaskDto
import ui.model.UserDetails
import ui.model.request.AuthRequest
import ui.model.response.ApiResponse
import ui.model.response.AuthResponseModel
import ui.model.response.isFailure
import ui.model.response.toUserDetails
import ui.model.toDto
import ui.model.toTaskList

class ApiService {

    private var httpClient: HttpClient? = null
    private val BAES_URL = "http://0.0.0.0:8080"

    init {
        configureHttpClient()
    }

    private fun configureHttpClient() {
        httpClient = HttpClient {
            install(ContentNegotiation) {
                register(
                    ContentType.Text.Plain, KotlinxSerializationConverter(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            explicitNulls = true
                        }
                    )
                )
                register(
                    ContentType.Application.Json, KotlinxSerializationConverter(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            explicitNulls = true
                        }
                    )
                )
            }
        }
    }

    fun getHttpClient(): HttpClient {
        if (httpClient == null) {
            configureHttpClient()
        }
        return httpClient!!
    }

    suspend fun signUp(email: String, password: String): UserDetails? {

        val response: ApiResponse<AuthResponseModel>? = httpClient?.post("$BAES_URL/signUp") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email, password))
        }?.body<ApiResponse<AuthResponseModel>>()

        if (response == null)
            return null

        if (!response.success)
            return null

        if (response.data == null)
            return null

        if (response.data.isFailure())
            return null

        val data = response.data
        val userDetails = data.toUserDetails(password)
        return userDetails
    }

    suspend fun signIn(email: String, password: String): UserDetails? {
        val response: ApiResponse<AuthResponseModel>? = httpClient?.post("$BAES_URL/signIn") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email, password))
        }?.body<ApiResponse<AuthResponseModel>>()

        if (response == null)
            return null

        if (!response.success)
            return null

        if (response.data == null)
            return null

        if (response.data.isFailure())
            return null

        val data = response.data
        val userDetails = data.toUserDetails(password)
        return userDetails
    }

    suspend fun getTasksForCurrentUser(token: String): List<Task> {
        val response: ApiResponse<List<TaskDto>>? = httpClient?.get("$BAES_URL/getAllTask") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }?.body<ApiResponse<List<TaskDto>>?>()

        if (response == null)
            return emptyList()

        if (!response.success)
            return emptyList()

        if (response.data == null)
            return emptyList()

        val data = response.data.toTaskList()
        return data
    }

    suspend fun addTask(task: Task, token: String) {
        try {
            val response: ApiResponse<String?>? = httpClient?.post("$BAES_URL/addTask") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(task.toDto())
            }?.body<ApiResponse<String?>>()
            println(response?.message)
        } catch (ex: Exception) {
            println(ex?.message)
        }
    }

    suspend fun updateTask(task: Task, token: String) {
        try {
            val response: ApiResponse<String?>? = httpClient?.post("$BAES_URL/updateTask") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(task.toDto())
            }?.body<ApiResponse<String?>>()
            println(response?.message)
        } catch (ex: Exception) {
            println(ex?.message)
        }
    }

    suspend fun setCompleted(token: String, task: Task, mCompleted: Boolean) {
        try {
            val updatedTask = task
            val response: ApiResponse<String?>? = httpClient?.post("$BAES_URL/updateTask") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(updatedTask.toDto().copy(completed = mCompleted))
            }?.body<ApiResponse<String?>>()
            println(response?.message)
        } catch (ex: Exception) {
            println(ex?.message)
        }
    }

    suspend fun setFavorite(token: String, task: Task, mFavorite: Boolean) {
        try {
            val updatedTask = task
            val response: ApiResponse<String?>? = httpClient?.post("$BAES_URL/updateTask") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(updatedTask.toDto().copy(pinned = mFavorite))
            }?.body<ApiResponse<String?>>()
            println(response?.message)
        } catch (ex: Exception) {
            println(ex?.message)
        }
    }

    suspend fun deleteTask(token: String, task: Task) {
        try {
            val response: ApiResponse<String?>? = httpClient?.post("$BAES_URL/updateTask") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                parameter("taskId", task._id)
            }?.body<ApiResponse<String?>>()
            println(response?.message)
        } catch (ex: Exception) {
            println(ex?.message)
        }
    }

    suspend fun authenticate(userDetails: UserDetails): UserDetails? {
        try {
            val response: ApiResponse<String?>? = httpClient?.get("$BAES_URL/authenticated") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${userDetails.token}")
            }?.body<ApiResponse<String?>>()
            if (response?.success == false) {
                if (response?.message.equals("User Unauthorized")) {
                    val updatedUserDetails = signIn(userDetails.email, userDetails.password)
                    return updatedUserDetails
                } else {
                    return null
                }
            } else {
                return null
            }
        } catch (ex: Exception) {
            println(ex?.message)
            return null
        }
    }

}