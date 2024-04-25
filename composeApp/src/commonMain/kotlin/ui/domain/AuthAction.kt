package ui.domain

sealed class AuthAction {

    data class SignIn(val email: String, val password: String): AuthAction()
    data class SignUp(val email: String, val password: String): AuthAction()

}