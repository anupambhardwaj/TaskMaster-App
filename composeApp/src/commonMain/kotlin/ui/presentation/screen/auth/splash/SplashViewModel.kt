package ui.presentation.screen.auth.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ui.BLANK_STRING
import ui.data.MongoDB
import ui.network.ApiService
import ui.presentation.screen.auth.signup.SignUpScreen
import ui.presentation.screen.home.HomeScreen

class SplashViewModel(private val mongoDB: MongoDB, private val apiService: ApiService): ScreenModel {


    fun init(navigator: Navigator) {
        screenModelScope.launch(Dispatchers.IO) {
            val userDetails = mongoDB.getUserDetails()
            userDetails?.let {
                val updatedUserDetails = apiService.authenticate(it)
                updatedUserDetails?.let {
                    mongoDB.updateUserDetails(it)
                }
                if (it.token != BLANK_STRING) {
                    navigator.push(HomeScreen())
                } else {
                    navigator.push(SignUpScreen())
                }
            } ?: navigator.push(SignUpScreen())
        }
    }

}