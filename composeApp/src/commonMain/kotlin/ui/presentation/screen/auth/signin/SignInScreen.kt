package ui.presentation.screen.auth.signin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import taskmaster.composeapp.generated.resources.Res
import taskmaster.composeapp.generated.resources.baseline_visibility_24
import taskmaster.composeapp.generated.resources.baseline_visibility_off_24
import ui.domain.AuthAction
import ui.presentation.screen.auth.signup.FAILURE
import ui.presentation.screen.auth.signup.SUCCESS
import ui.presentation.screen.auth.signup.SignUpScreen
import ui.presentation.screen.home.HomeScreen
import ui.theme.DarkColors

class SignInScreen : Screen {

    val colors = DarkColors

    @OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SignInScreenViewModel>()

        val showDialog = remember {
            mutableStateOf(false)
        }

        val invalidCredentials = remember {
            mutableStateOf(false)
        }

        val showPassword = remember {
            mutableStateOf(false)
        }

        LaunchedEffect(viewModel.signInSuccess.value) {
            if (viewModel.signInSuccess.value == SUCCESS) {
                navigator.push(HomeScreen())
                viewModel.signInSuccess.value = ""
            } else if (viewModel.signInSuccess.value == FAILURE) {
                showDialog.value = true
            } else {
                // do nothing
            }
        }

        if (showDialog.value) {
            AlertDialog(
                text = { Text("SignIn Failed! Please try again later") },
                onDismissRequest = { showDialog.value = false },
                buttons = {
                    Button(
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text("Ok")
                    }
                }
            )
        }

        if (invalidCredentials.value) {
            AlertDialog(
                title = { Text("Invalid email or password!") },
                text = { Text("Please enter valid credentials and try again.") },
                onDismissRequest = { invalidCredentials.value = false },
                buttons = {
                    Row(horizontalArrangement = Arrangement.End) {
                        TextButton(
                            onClick = {
                                invalidCredentials.value = false
                            }
                        ) {
                            Text("Ok")
                        }
                    }
                }
            )
        }


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colors.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(50.dp)
            ) {

                Text(
                    "Sign In",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = TextStyle.Default.copy(
                        fontSize = 60.sp,
                        color = colors.primary,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    value = viewModel.email.value,
                    onValueChange = { value ->
                        viewModel.email.value = value
                    },
                    placeholder = {
                        Text(
                            "Enter email",
                            color = colors.surfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.onBackground,
                        focusedBorderColor = colors.onPrimary,
                        unfocusedBorderColor = colors.background,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background
                    ),
                    maxLines = 1,
                    shape = RoundedCornerShape(50.dp)
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    value = viewModel.password.value,
                    onValueChange = { value ->
                        viewModel.password.value = value
                    },
                    placeholder = {
                        Text(
                            "Enter password",
                            color = colors.surfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (!showPassword.value) {
                            Icon(
                                painterResource(Res.drawable.baseline_visibility_24),
                                "show password",
                                modifier = Modifier.clickable {
                                    showPassword.value = true
                                })
                        } else {
                            Icon(
                                painterResource(Res.drawable.baseline_visibility_off_24),
                                "hide password",
                                modifier = Modifier.clickable {
                                    showPassword.value = false
                                }
                            )
                        }

                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    maxLines = 1,
                    visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.onBackground,
                        focusedBorderColor = colors.onPrimary,
                        unfocusedBorderColor = colors.background,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background
                    ),
                    shape = RoundedCornerShape(50.dp)
                )

                Button(
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryContainer,
                        contentColor = colors.onBackground
                    ),
                    onClick = {
                        if (viewModel.email.value != "" && viewModel.password.value != "") {
                            viewModel.setAction(
                                AuthAction.SignIn(
                                    viewModel.email.value,
                                    viewModel.password.value
                                )
                            )
                        } else {
                            invalidCredentials.value = true
                        }
                    }
                ) {
                    Text("Sign In")
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )

                Text(
                    "OR",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp
                    ),
                    color = colors.onBackground
                )

                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colors.primary
                    ),
                    onClick = {
                        navigator.push(SignUpScreen())
                    }
                ) {
                    Text(
                        "Sign Up", style = TextStyle.Default.copy(
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize
                        )
                    )
                }

            }
        }
    }


}