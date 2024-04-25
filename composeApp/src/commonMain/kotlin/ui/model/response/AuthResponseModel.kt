package ui.model.response

import kotlinx.serialization.Serializable
import ui.BLANK_STRING
import ui.model.UserDetails

@Serializable
data class AuthResponseModel(
    val kind: String = BLANK_STRING,
    val idToken: String = BLANK_STRING,
    val email: String = BLANK_STRING,
    val refreshToken: String = BLANK_STRING,
    val expiresIn: String = BLANK_STRING,
    val localId: String = BLANK_STRING,
    val error: AuthErrorResponseModel? = null
)

fun AuthResponseModel.isSuccess() = error == null

fun AuthResponseModel.isFailure() = error != null

fun AuthResponseModel.toUserDetails(mPassword: String): UserDetails {
    val response = this
    return UserDetails().apply {
        userId = response.localId
        email = response.email
        password = mPassword
        token = idToken
    }
}

