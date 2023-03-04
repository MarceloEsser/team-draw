@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
)

package esser.marcelo.team.draw

import android.os.Bundle
import android.widget.RatingBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import esser.marcelo.team.draw.ui.theme.TeamDrawTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamDrawTheme {
                val drawerState =
                    androidx.compose.material.rememberDrawerState(androidx.compose.material.DrawerValue.Closed)
                TeamsModal(lifeCycleOwner = this, drawerState = drawerState)
                Child(drawerState = drawerState, lifeCycleOwner = this)
            }
        }
    }
}

@Composable
fun Child(
    viewModel: MainViewModel = viewModel(),
    drawerState: androidx.compose.material.DrawerState,
    lifeCycleOwner: ComponentActivity
) {
    var fatDude by remember { mutableStateOf(false) }
    var soccerName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Team Draw") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.drawTeams()
                    focusManager.clearFocus()
                    soccerName = ""
                    scope.launch {
                        drawerState.animateTo(DrawerValue.Open, TweenSpec(durationMillis = 0))
                    }
                },
                icon = {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Add player"
                    )
                },
                text = { Text("Sortear") })
        },
        bottomBar = {
            BottomAppBar {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 10.dp)
                        .width(225.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    value = soccerName,
                    onValueChange = { a ->
                        soccerName = a
                    },
                    label = {
                        Text(text = "Nome")
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.addPlayer(soccerName, fatDude)
                        soccerName = ""
                    })
                )
                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Gordão",
                        Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Checkbox(
                        checked = fatDude,
                        onCheckedChange = { checked -> fatDude = checked })
                }
            }
        },
    ) { scaffoldPadding ->
        Box(modifier = Modifier.padding(scaffoldPadding)) {
            val soccerPlayers = remember { viewModel.soccerPlayers }

            LazyColumn {
                items(items = soccerPlayers, key = { it.hashCode() }) { player ->
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        viewModel.deletePlayer(player)
                    }

                    SwipeToDismiss(
                        directions = setOf(
                            DismissDirection.EndToStart
                        ),
                        state = dismissState,
                        background = {
                            val mColor by animateColorAsState(
                                if (dismissState.targetValue != DismissValue.Default) MaterialTheme.colorScheme.error
                                else Color.Transparent
                            )

                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.65f else 1.1f
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(mColor)
                                    .padding(horizontal = Dp(20f)),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Icon",
                                    modifier = Modifier.scale(scale)
                                )
                            }
                        },
                        dismissContent = {
                            val transition = updateTransition(dismissState, label = "")
                            val elevation by transition.animateDp(label = "") {
                                if (it.targetValue == DismissValue.Default) 0.dp else 6.dp
                            }

                            Surface(
                                shadowElevation = elevation,
                                tonalElevation = elevation,
                                onClick = {
                                    viewModel.changeSoccerStatus(player.id)
                                },
                            ) {
                                if (player.id != soccerPlayers.first().id) {
                                    Divider(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    )
                                }

                                ListItem(
                                    text = { Text(text = player.name) },
                                    icon = {
                                        if(player.isPlaying) {
                                            Icon(
                                                Icons.Filled.CheckCircle,
                                                contentDescription = "Delete player"
                                            )
                                        }

                                    },
                                    trailing = {
                                        if (player.isFatDude) {
                                            Icon(
                                                Icons.Filled.Face,
                                                contentDescription = "Delete player"
                                            )
                                        }
                                    }
                                )

                            }
                        }
                    )


                }
            }
        }
    }
}

@Composable
fun TeamsModal(
    viewModel: MainViewModel = viewModel(),
    lifeCycleOwner: ComponentActivity,
    drawerState: androidx.compose.material.DrawerState
) {
    val drawerContentColor = animateColorAsState(
        targetValue = if (drawerState.isClosed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
    )
    val teams = remember { viewModel.teams }

    ModalDrawer(
        drawerContentColor = drawerContentColor.value,
        drawerState = drawerState,
        drawerContent = {
            Column {
                teams.forEach { team ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        team.forEach { player ->
                            Text(text = player.name)
                        }
                    }
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Times")
                Button(onClick = {
                    lifeCycleOwner.lifecycleScope.launch {
                        drawerState.open()
                    }

                }) {
                    Text(text = "Abrir")
                }
            }
        }
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = true,
)
@Composable
fun DefaultPreview() {
    TeamDrawTheme {
//        Child()
    }
}