package ui.presentation.screen.auth.signin

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ui.BLANK_STRING
import ui.data.MongoDB
import ui.domain.AuthAction
import ui.network.ApiService
import ui.presentation.screen.auth.signup.FAILURE
import ui.presentation.screen.auth.signup.SUCCESS

class SignInScreenViewModel(
    private val mongoDB: MongoDB,
    private val apiService: ApiService
): ScreenModel {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    val signInSuccess = mutableStateOf("")

    /*val isUserAlreadySignedIn = mutableStateOf(false)

    init {
        screenModelScope.launch(Dispatchers.IO) {
            val token = mongoDB.getUserDetails()?.token
            if (token != BLANK_STRING) {
                isUserAlreadySignedIn.value = true
            }
        }
    }*/

    fun setAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignUp -> {

            }
            is AuthAction.SignIn -> {
                screenModelScope.launch(Dispatchers.IO) {
                    val user = apiService.signIn(action.email, action.password)
                    if (user == null) {
                        signInSuccess.value = FAILURE
                    } else {
                        mongoDB.updateUserDetails(user)
                        signInSuccess.value = SUCCESS
                    }
                }
            }
        }
    }

}