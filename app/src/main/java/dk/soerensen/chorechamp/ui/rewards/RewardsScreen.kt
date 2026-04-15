package dk.soerensen.chorechamp.ui.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dk.soerensen.chorechamp.R
import dk.soerensen.chorechamp.data.local.database.ChoreChampDatabase
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import dk.soerensen.chorechamp.ui.theme.ScreenBackground
import dk.soerensen.chorechamp.viewmodel.ChildRewardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val db = ChoreChampDatabase.getDatabase(context)
    val repository = ChoreRepository(
        db.userProfileDao(), db.taskDao(), db.rewardDao(), db.childStatsDao()
    )
    val viewModel: ChildRewardsViewModel = viewModel(
        factory = ChildRewardsViewModel.Factory(repository, username)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.redeemMessage) {
        val message = uiState.redeemMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearRedeemMessage()
    }

    ScreenBackground(backgroundRes = R.drawable.bg_rewards) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("🏆 Rewards") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Total points summary
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⭐ Your total points: ${uiState.totalPoints}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                // Completed tasks section
                item {
                    Text(
                        text = "✅ Completed Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.approvedTasks.isEmpty()) {
                    item {
                        Text(
                            text = "No completed tasks yet. Complete some chores to earn points!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                } else {
                    items(uiState.approvedTasks) { task ->
                        CompletedTaskItem(task = task)
                    }
                }

                // Available rewards section
                item {
                    Text(
                        text = "🎁 Available Rewards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.rewards.isEmpty()) {
                    item {
                        Text(
                            text = "No rewards available yet. Ask a parent to add some!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                } else {
                    items(uiState.rewards) { reward ->
                        RewardCard(
                            reward = reward,
                            totalPoints = uiState.totalPoints,
                            onRedeem = { viewModel.redeemReward(reward) }
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun CompletedTaskItem(task: TaskEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+${task.points} pts",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun RewardCard(reward: RewardEntity, totalPoints: Int, onRedeem: () -> Unit) {
    val canAfford = totalPoints >= reward.cost

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${reward.cost} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = onRedeem,
                enabled = canAfford
            ) {
                Text("Redeem")
            }
        }
    }
}
