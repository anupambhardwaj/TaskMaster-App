package ui.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)
