package dk.soerensen.chorechamp.ui.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import dk.soerensen.chorechamp.data.local.database.ChoreChampDatabase
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import dk.soerensen.chorechamp.ui.navigation.NavRoutes
import dk.soerensen.chorechamp.viewmodel.ParentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val db = ChoreChampDatabase.getDatabase(context)
    val repository = ChoreRepository(
        db.userProfileDao(), db.taskDao(), db.rewardDao(), db.childStatsDao()
    )
    val viewModel: ParentViewModel = viewModel(
        factory = ParentViewModel.Factory(repository, username)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏠 ChoreChamp - Parent") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.addTask(username)) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = today,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    ApprovalSummaryCard(
                        pendingCount = uiState.pendingCount,
                        onOpenApprovals = { navController.navigate(NavRoutes.approvals(username)) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate(NavRoutes.rewards(username)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🏆 Rewards")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate(NavRoutes.addTask(username)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("➕ Add Chore")
                        }
                    }
                }

                item {
                    Text(
                        text = "Today's Chores",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.todayTasks.isEmpty()) {
                    item {
                        Text(
                            text = "No chores planned for today. Use the + button to add some!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                items(uiState.todayTasks) { task ->
                    ParentTaskCard(
                        task = task,
                        childName = uiState.childProfiles[task.selectedByChildId]?.username
                    )
                }
            }
        }
    }
}

@Composable
private fun ApprovalSummaryCard(pendingCount: Int, onOpenApprovals: () -> Unit) {
    Card(
        onClick = onOpenApprovals,
        colors = CardDefaults.cardColors(
            containerColor = if (pendingCount > 0)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pending Approvals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (pendingCount > 0) "Tap to review" else "All caught up!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Badge(
                containerColor = if (pendingCount > 0)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = "$pendingCount",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ParentTaskCard(task: TaskEntity, childName: String?) {
    val statusColor = when (task.status) {
        "PENDING_APPROVAL" -> MaterialTheme.colorScheme.error
        "APPROVED" -> MaterialTheme.colorScheme.secondary
        "SELECTED" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (childName != null) {
                    Text(
                        text = "👦 $childName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${task.points} pts",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = task.status.replace("_", " "),
                style = MaterialTheme.typography.labelLarge,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
