package ui.domain

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Task: RealmObject {
    @PrimaryKey
    var _id: String = ObjectId().toHexString()
    var title: String = ""
    var description: String = ""
    var completed: Boolean = false
    var pinned: Boolean = false
}