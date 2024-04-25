package ui.presentation.screen.auth.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ui.BLANK_STRING
import ui.data.MongoDB
import ui.domain.AuthAction
import ui.network.ApiService
import ui.presentation.screen.home.HomeScreen

const val SUCCESS = "Success"
const val FAILURE = "Failure"

class SignUpScreenViewModel(
    private val mongoDB: MongoDB,
    private val apiService: ApiService
): ScreenModel {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    val signUpSuccess = mutableStateOf("")

    fun setAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignUp -> {
                screenModelScope.launch(Dispatchers.IO) {
                    val user = apiService.signUp(action.email, action.password)
                    if (user == null) {
                        signUpSuccess.value = FAILURE
                    } else {
                        mongoDB.saveUserDetails(user)
                        signUpSuccess.value = SUCCESS
                    }
                }
            }
            is AuthAction.SignIn -> {

            }
        }
    }


}