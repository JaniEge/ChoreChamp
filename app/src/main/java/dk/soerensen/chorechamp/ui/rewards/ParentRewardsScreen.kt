package dk.soerensen.chorechamp.ui.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dk.soerensen.chorechamp.data.local.database.ChoreChampDatabase
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import dk.soerensen.chorechamp.viewmodel.ParentRewardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentRewardsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = ChoreChampDatabase.getDatabase(context)
    val repository = ChoreRepository(
        db.userProfileDao(), db.taskDao(), db.rewardDao(), db.childStatsDao()
    )
    val viewModel: ParentRewardsViewModel = viewModel(
        factory = ParentRewardsViewModel.Factory(repository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var newRewardTitle by remember { mutableStateOf("") }
    var newRewardCost by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Reward") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newRewardTitle,
                        onValueChange = { newRewardTitle = it },
                        label = { Text("Reward name") },
                        placeholder = { Text("e.g. Screen time, Cinema trip") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRewardCost,
                        onValueChange = { newRewardCost = it },
                        label = { Text("Point cost") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cost = newRewardCost.toIntOrNull() ?: 0
                        if (newRewardTitle.isNotBlank() && cost > 0) {
                            viewModel.addReward(newRewardTitle, cost)
                            showAddDialog = false
                            newRewardTitle = ""
                            newRewardCost = ""
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddDialog = false
                    newRewardTitle = ""
                    newRewardCost = ""
                }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏆 Manage Rewards") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reward")
            }
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
        } else if (uiState.rewards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No rewards yet.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap + to add rewards for children to redeem.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
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
                items(uiState.rewards, key = { it.id }) { reward ->
                    ParentRewardCard(
                        reward = reward,
                        onDelete = { viewModel.deleteReward(reward) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParentRewardCard(reward: RewardEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete reward",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
