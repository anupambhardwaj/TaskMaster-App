@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)

package ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kevinnzou.swipebox.SwipeBox
import com.kevinnzou.swipebox.SwipeDirection
import com.kevinnzou.swipebox.widget.SwipeIcon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import taskmaster.composeapp.generated.resources.Res
import taskmaster.composeapp.generated.resources.delete_24px
import taskmaster.composeapp.generated.resources.keep
import taskmaster.composeapp.generated.resources.keep_filled
import taskmaster.composeapp.generated.resources.keep_off_24px
import ui.domain.Task
import ui.theme.DarkColors


@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
@Composable
fun TaskView(
    modifier: Modifier = Modifier,
    task: Task,
    showActive: Boolean = true,
    onSelect: (Task) -> Unit,
    onComplete: (Task, Boolean) -> Unit,
    onPinned: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit
) {

    val colors = DarkColors/*if (!isSystemInDarkTheme()) {
        LightColors
    } else {
        DarkColors
    }*/

    val scope = rememberCoroutineScope()
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        SwipeBox(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .then(modifier),
            swipeDirection = SwipeDirection.Both,
            startContentWidth = 60.dp,
            startContent = { swipeableState, startSwipeProgress ->
                val painterIcon = if (task.pinned) painterResource(Res.drawable.keep_off_24px) else painterResource(Res.drawable.keep)
                SwipeIcon(
                    painter = painterIcon,
                    contentDescription = null,
                    tint = Color.White,
                    background = colors.surfaceVariant
                ) {
                    scope.launch {
                        onPinned(task, !task.pinned)
                        swipeableState.animateTo(0)
                    }
                }
            },
            endContentWidth = 60.dp,
            endContent = { swipeableState, endSwipeProgress ->
                SwipeIcon(
                    painter = painterResource(Res.drawable.delete_24px),
                    contentDescription = null,
                    tint = Color.White,
                    background = colors.surfaceVariant
                ) {
                    scope.launch {
                        onDelete(task)
                        swipeableState.animateTo(0)
                    }
                }
            }
        ) { _, _, _ ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .padding(8.dp)
                    .clickable {
                        if (showActive) onSelect(task)
                        else onDelete(task)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(4.dp).weight(0.8f)) {
                    Checkbox(
                        modifier = Modifier.weight(0.2f),
                        checked = task.completed,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = colors.surface,
                            checkedColor = colors.primaryContainer,
                            uncheckedColor = colors.primary
                        ),
                        onCheckedChange = { onComplete(task, !task.completed) },
                    )
//                Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight( 0.8f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            modifier = Modifier,
                            text = task.title,
                            color = colors.onBackground,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            textDecoration = if (showActive) TextDecoration.None
                            else TextDecoration.LineThrough
                        )
                        if (showActive) {
                            Text(
                                modifier = Modifier,
                                text = task.description,
                                color = colors.onBackground,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                    }
                }

                if (task.pinned) {
                    Icon(
                        painter = painterResource(
                            Res.drawable.keep_filled
                        ),
                        contentDescription = "Favorite Icon",
                        tint = colors.primary,
                        modifier = Modifier.weight(0.2f)
                            .size(18.dp)
                    )
                }
            }
        }
    }

}