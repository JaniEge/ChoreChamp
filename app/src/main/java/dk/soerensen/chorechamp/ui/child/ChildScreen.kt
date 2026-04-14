package dk.soerensen.chorechamp.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.soerensen.chorechamp.data.local.database.ChoreChampDatabase
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import dk.soerensen.chorechamp.ui.navigation.NavRoutes
import dk.soerensen.chorechamp.ui.theme.BackgroundDark
import dk.soerensen.chorechamp.ui.theme.BackgroundMedium
import dk.soerensen.chorechamp.ui.theme.DragonGold
import dk.soerensen.chorechamp.ui.theme.SurfaceCard
import dk.soerensen.chorechamp.viewmodel.ChildViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ChildScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val db = ChoreChampDatabase.getDatabase(context)
    val repository = ChoreRepository(
        db.userProfileDao(), db.taskDao(), db.rewardDao(), db.childStatsDao()
    )
    val viewModel: ChildViewModel = viewModel(
        factory = ChildViewModel.Factory(repository, username)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundMedium)
                )
            )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TopSection(
                    username = username,
                    profileImageUri = uiState.profile?.profileImageUri,
                    totalPoints = uiState.stats?.totalPoints ?: 0,
                    dragonLabel = uiState.dragonLabel,
                    onProfileClick = { navController.navigate(NavRoutes.profile(username)) }
                )

                Text(
                    text = today,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (uiState.todayTasks.isEmpty()) {
                        item {
                            Text(
                                text = "No tasks for today. Tap 'Choose Tasks' to add some!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    items(uiState.todayTasks) { task ->
                        TaskCard(
                            task = task,
                            onComplete = { viewModel.requestCompletion(task.id) },
                            onDelete = { viewModel.deselectTask(task.id) }
                        )
                    }
                }

                BottomActions(
                    onMyTasksClick = { },
                    onChooseTasksClick = {
                        navController.navigate(NavRoutes.childChooseTasks(username))
                    },
                    onRewardsClick = {
                        navController.navigate(NavRoutes.rewards(username))
                    }
                )
            }
        }
    }
}

@Composable
private fun TopSection(
    username: String,
    profileImageUri: String?,
    totalPoints: Int,
    dragonLabel: String,
    onProfileClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                onClick = onProfileClick,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile picture of $username",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$totalPoints",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DragonGold
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dragonLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val statusLabel = when (task.status) {
        "PENDING_APPROVAL" -> "⏳ Pending approval"
        "APPROVED" -> "✅ Approved"
        else -> "📋 Selected"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "${task.points} pts  •  $statusLabel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            if (task.status == "SELECTED") {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color.DarkGray)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("✅ Complete") },
                            onClick = {
                                menuExpanded = false
                                onComplete()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🗑 Remove from list") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomActions(
    onMyTasksClick: () -> Unit,
    onChooseTasksClick: () -> Unit,
    onRewardsClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(onClick = onMyTasksClick) {
                Text("My Tasks")
            }
            Button(onClick = onChooseTasksClick) {
                Text("Choose Tasks")
            }
            OutlinedButton(onClick = onRewardsClick) {
                Text("Rewards")
            }
        }
    }
}
