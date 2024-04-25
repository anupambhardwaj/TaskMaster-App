package ui.model

import kotlinx.serialization.Serializable
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import ui.domain.Task

@Serializable
data class TaskDto(
    var _id: String = "",
    var title: String = "",
    var description: String = "",
    var completed: Boolean = false,
    var pinned: Boolean = false,
)

fun TaskDto.toTaskList(): Task {
    val dto = this
    return Task().apply {
        _id = dto._id
        title = dto.title
        description = dto.description
        completed = dto.completed
        pinned = dto.pinned
    }
}

fun List<TaskDto>.toTaskList(): List<Task> {
    val dtoList = this
    val taskList = mutableListOf<Task>()
    dtoList.forEach { dto ->
        val task = Task().apply {
            _id = dto._id //TODO Handle this.
            title = dto.title
            description = dto.description
            completed = dto.completed
            pinned = dto.pinned
        }
        taskList.add(task)
    }
    return taskList
}

fun Task.toDto() = TaskDto(
    _id = this._id,
    title = this.title,
    description = this.description,
    completed = this.completed,
    pinned = this.pinned
)