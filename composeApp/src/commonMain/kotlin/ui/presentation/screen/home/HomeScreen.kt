package ui.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ui.domain.RequestState
import ui.domain.Task
import ui.domain.TaskAction
import ui.presentation.components.ErrorScreen
import ui.presentation.components.LoadingScreen
import ui.presentation.components.TaskView
import ui.presentation.screen.task.TaskScreen
import ui.theme.DarkColors
import ui.theme.md_theme_dark_onBackground


class HomeScreen : Screen {

    val colors = DarkColors

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeScreenViewModel>()
        val activeTasks by viewModel.activeTasks
        val completedTasks by viewModel.completedTasks


        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val isActiveTaskOpen = remember { mutableStateOf(true) }

        ModalNavigationDrawer(
            modifier = Modifier,
            drawerState = drawerState,
            gesturesEnabled = true,
            scrimColor = colors.surface,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .background(
                            color = colors.surface
                        )
                        .fillMaxWidth(0.8f),
                    drawerContainerColor = colors.surface,
                    drawerTonalElevation = 12.dp
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth().height(24.dp))
                    NavigationDrawerItem(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        label = { Text(text = "Active Task") },
                        selected = isActiveTaskOpen.value,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = colors.background
                        ),
                        onClick = {
                            isActiveTaskOpen.value = true
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.fillMaxWidth().height(12.dp))
                    NavigationDrawerItem(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        label = { Text(text = "Completed Task") },
                        selected = !isActiveTaskOpen.value,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = colors.background.copy(alpha = 0.9f)
                        ),
                        onClick = {
                            isActiveTaskOpen.value = false
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            },
        ) {
            Scaffold(
                modifier = Modifier.background(
                    color = colors.background
                ),
                topBar = {
                    CenterAlignedTopAppBar(title = { Text(text = "Home") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = colors.background,
                            titleContentColor = colors.onBackground
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Edit Icon"
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    viewModel.setAction(TaskAction.Logout)
                                    navigator.pop()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navigator.push(TaskScreen()) },
                        shape = RoundedCornerShape(size = 12.dp),
                        containerColor = colors.primaryContainer,
                        contentColor = colors.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Edit Icon"
                        )
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                ) {
                    if (isActiveTaskOpen.value) {
                        DisplayTasks(
                            modifier = Modifier.weight(1f),
                            tasks = activeTasks,
                            onSelect = { selectedTask ->
                                navigator.push(TaskScreen(selectedTask))
                            },
                            onFavorite = { task, isFavorite ->
                                viewModel.setAction(
                                    action = TaskAction.SetFavorite(task, isFavorite)
                                )
                            },
                            onComplete = { task, completed ->
                                viewModel.setAction(
                                    action = TaskAction.SetCompleted(task, completed)
                                )
                            },
                            onDelete = { task ->
                                viewModel.setAction(TaskAction.Delete(task))
                            }
                        )
                    } else {
                        /*Divider(modifier = Modifier.height(2.dp).fillMaxWidth(), color = Color.White)
                        Spacer(modifier = Modifier.height(24.dp))*/
                        DisplayTasks(
                            modifier = Modifier.weight(1f),
                            tasks = completedTasks,
                            showActive = false,
                            onComplete = { task, completed ->
                                viewModel.setAction(
                                    action = TaskAction.SetCompleted(task, completed)
                                )
                            },
                            onDelete = { task ->
                                viewModel.setAction(
                                    action = TaskAction.Delete(task)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayTasks(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<Task>>,
    showActive: Boolean = true,
    onSelect: ((Task) -> Unit)? = null,
    onFavorite: ((Task, Boolean) -> Unit)? = null,
    onComplete: (Task, Boolean) -> Unit,
    onDelete: ((Task) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: Task? by remember { mutableStateOf(null) }

    if (showDialog) {
        AlertDialog(
            title = {
                Text(text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${taskToDelete!!.title}' task?",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            confirmButton = {
                Button(onClick = {
                    onDelete?.invoke(taskToDelete!!)
                    showDialog = false
                    taskToDelete = null
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = {
                taskToDelete = null
                showDialog = false
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = if (showActive) "Active Tasks" else "Completed Tasks",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Medium,
            color = md_theme_dark_onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        tasks.DisplayResult(
            onLoading = { LoadingScreen() },
            onError = { ErrorScreen(message = it) },
            onSuccess = {
                if (it.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(
                            items = it,
                            key = { task -> task._id/*.toHexString()*/ }
                        ) { task ->
                            TaskView(
                                showActive = showActive,
                                task = task,
                                onSelect = { onSelect?.invoke(task) },
                                onComplete = { selectedTask, completed ->
                                    onComplete(selectedTask, completed)
                                },
                                onPinned = { selectedTask, favorite ->
                                    onFavorite?.invoke(selectedTask, favorite)
                                },
                                onDelete = { selectedTask ->
                                    taskToDelete = selectedTask
                                    showDialog = true
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
                        }
                    }
                } else {
                    Column() {
                        ErrorScreen(message = "There is no List")
                        Text("Press + to add the list")
                    }
                }
            }
        )
    }
}