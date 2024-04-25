import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import ui.theme.AppTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ui.data.MongoDB
import ui.network.ApiService
import ui.presentation.screen.auth.signin.SignInScreenViewModel
import ui.presentation.screen.auth.signup.SignUpScreen
import ui.presentation.screen.auth.signup.SignUpScreenViewModel
import ui.presentation.screen.auth.splash.Splash
import ui.presentation.screen.auth.splash.SplashViewModel
import ui.presentation.screen.home.HomeScreenViewModel
import ui.presentation.screen.task.TaskScreenViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {

    initialiseKoin()

    AppTheme {
        Navigator(Splash()) {
            SlideTransition(it)
        }
    }
}

val mongoModule = module {
    single {
        MongoDB()
    }
    single {
        ApiService()
    }
    factory {
        SplashViewModel(get(), get())
    }
    factory {
        HomeScreenViewModel(get(), get())
    }
    factory {
        TaskScreenViewModel(get(), get())
    }
    factory {
        SignInScreenViewModel(get(), get())
    }
    factory {
        SignUpScreenViewModel(get(), get())
    }
}

fun initialiseKoin() {
    startKoin {
        modules(mongoModule)
    }
}