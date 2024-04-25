package ui.presentation.screen.auth.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ui.theme.DarkColors

class Splash: Screen {

    val colors = DarkColors

    @Composable
    override fun Content() {


        val viewModel = getScreenModel<SplashViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            viewModel.init(navigator)
        }

        Surface(modifier = Modifier.fillMaxSize().background(
            color = colors.background
        )) {

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = colors.primary,
                    backgroundColor = colors.background,
                    strokeWidth = 2.dp,
                    strokeCap = StrokeCap.Round
                )
            }
        }

    }

}