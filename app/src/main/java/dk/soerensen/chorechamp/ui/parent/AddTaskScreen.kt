package dk.soerensen.chorechamp.ui.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dk.soerensen.chorechamp.data.local.database.ChoreChampDatabase
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import dk.soerensen.chorechamp.viewmodel.AddTaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val db = ChoreChampDatabase.getDatabase(context)
    val repository = ChoreRepository(
        db.userProfileDao(), db.taskDao(), db.rewardDao(), db.childStatsDao()
    )
    val viewModel: AddTaskViewModel = viewModel(
        factory = AddTaskViewModel.Factory(repository, username)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.onSavedHandled()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Chore") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Chore title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.points,
                onValueChange = viewModel::onPointsChange,
                label = { Text("Points") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Schedule for:",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (0..6).forEach { offset ->
                    val date = LocalDate.now().plusDays(offset.toLong())
                    val label = when (offset) {
                        0 -> "Today"
                        1 -> "Tmrw"
                        else -> date.format(DateTimeFormatter.ofPattern("EEE"))
                    }
                    FilterChip(
                        selected = uiState.selectedDayOffset == offset,
                        onClick = { viewModel.onDayOffsetChange(offset) },
                        label = { Text(label) }
                    )
                }
            }

            if (uiState.children.isNotEmpty()) {
                Text(
                    text = "Assign to child (optional):",
                    style = MaterialTheme.typography.labelLarge
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = uiState.selectedChildId == null,
                        onClick = { viewModel.onChildSelected(null) },
                        label = { Text("Any child") }
                    )
                    uiState.children.forEach { child ->
                        FilterChip(
                            selected = uiState.selectedChildId == child.id,
                            onClick = { viewModel.onChildSelected(child.id) },
                            label = { Text(child.username) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::saveTask,
                enabled = uiState.title.isNotBlank() && uiState.points.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Chore")
            }
        }
    }
}
