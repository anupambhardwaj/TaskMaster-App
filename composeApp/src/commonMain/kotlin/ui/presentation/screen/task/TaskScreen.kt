package ui.presentation.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ui.BLANK_STRING
import ui.domain.Task
import ui.domain.TaskAction
import ui.theme.DarkColors


const val DEFAULT_TITLE = "Enter the Title"
const val DEFAULT_DESCRIPTION = "Add some description"

data class TaskScreen(val task: Task? = null) : Screen {

    val colors = DarkColors

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TaskScreenViewModel>()
        var currentTitle by remember {
            mutableStateOf(task?.title ?: BLANK_STRING)
        }
        var currentDescription by remember {
            mutableStateOf(task?.description ?: BLANK_STRING)
        }



        Scaffold(
            modifier = Modifier
                .background(color = colors.background),
            topBar = {
                TopAppBar(
                    title = {
                        CenterAlignedTopAppBar(title = { Text(text = "Add Task") }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = colors.background,
                            titleContentColor = colors.onBackground
                        ))
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotEmpty() && currentDescription.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            if (task != null) {
                                viewModel.setAction(
                                    action = TaskAction.Update(
                                        Task().apply {
                                            _id = task._id
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            } else {
                                viewModel.setAction(
                                    action = TaskAction.Add(
                                        Task().apply {
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            }
                            navigator.pop()
                        },
                        shape = RoundedCornerShape(size = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checkmark Icon"
                        )
                    }
                }
            }
        ) { padding ->


            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {

                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .weight(0.2f),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    ),
                    placeholder = {
                                  Text(DEFAULT_TITLE, style =  TextStyle(
                                      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                      fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                  ))
                    },
                    singleLine = true,
                    value = currentTitle,
                    onValueChange = { currentTitle = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        disabledContainerColor = colors.background,
                        focusedIndicatorColor = colors.background,
                        unfocusedIndicatorColor = colors.background,
                        disabledIndicatorColor = colors.background,
                        errorContainerColor = colors.background,
                    )

                )

                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.9f),
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    placeholder = {
                                  Text(DEFAULT_DESCRIPTION, style =  TextStyle(
                                      fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                      color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                                  ))
                    },
                    value = currentDescription,
                    onValueChange = { description -> currentDescription = description },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        disabledContainerColor = colors.background,
                        focusedIndicatorColor = colors.background,
                        unfocusedIndicatorColor = colors.background,
                        disabledIndicatorColor = colors.background,
                        errorContainerColor = colors.background,
                    )
                )
            }
        }
    }
}