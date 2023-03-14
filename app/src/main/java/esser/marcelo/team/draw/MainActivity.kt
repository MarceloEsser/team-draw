@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
)

package esser.marcelo.team.draw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import esser.marcelo.team.draw.ui.theme.TeamDrawTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamDrawTheme {

                Child()

            }
        }
    }
}

@Composable
fun Child(
    viewModel: MainViewModel = viewModel(),
) {
    val fatDude by remember { mutableStateOf(false) }
    var soccerName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            }
        ) {
            Surface(
                shape = MaterialTheme.shapes.large
            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 16.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                ) {
                    itemsIndexed(items = viewModel.teams) { index, team ->
                        if (index > 0) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            )
                        }
                        Text(text = "Time $index", modifier = Modifier.padding(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .height(110.dp)
                        ) {
                            items(team.players, key = { it.hashCode() }) { player ->
                                Text(text = player.name)
                            }
                        }
                    }
                }

            }
        }
    }

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
                    openDialog.value = openDialog.value.not()
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
            val progress = remember {
                viewModel.progress
            }
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
                        viewModel.addPlayer(soccerName)
                        soccerName = ""
                    })
                )

                Slider(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 10.dp)
                        .width(225.dp),
                    value = progress.value,
                    onValueChange = {
                        viewModel.progress.value = it
                    },
                    steps = 3,
                    valueRange = 1f..5f,
                )

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
                                        if (player.isPlaying) {
                                            Icon(
                                                Icons.Filled.CheckCircle,
                                                contentDescription = "Delete player"
                                            )
                                        }

                                    },
                                    trailing = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.alpha(0.4f).height(16.dp)
                                        ) {
                                            Text(
                                                player.rating.toString(),
                                                modifier = Modifier.padding(end = 4.dp),
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                                            )
                                            Icon(
                                                Icons.Filled.Star,
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

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = true,
)
@Composable
fun DefaultPreview() {
    TeamDrawTheme {
        Child()
    }
}