package ui.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponseModel(
    val code: Int,
    val message: String,
    val errors: List<Errors>
)

@Serializable
data class Errors(
    val message: String,
    val domain: String,
    val reason: String
)
