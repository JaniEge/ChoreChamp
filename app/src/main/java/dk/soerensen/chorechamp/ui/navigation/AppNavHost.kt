package dk.soerensen.chorechamp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dk.soerensen.chorechamp.ui.approvals.ApprovalsScreen
import dk.soerensen.chorechamp.ui.child.ChildScreen
import dk.soerensen.chorechamp.ui.child.ChooseTasksScreen
import dk.soerensen.chorechamp.ui.parent.AddTaskScreen
import dk.soerensen.chorechamp.ui.parent.ParentScreen
import dk.soerensen.chorechamp.ui.profile.ProfileScreen
import dk.soerensen.chorechamp.ui.rewards.RewardsScreen
import dk.soerensen.chorechamp.ui.role.RoleScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.ROLE,
        modifier = modifier
    ) {
        composable(NavRoutes.ROLE) {
            RoleScreen(navController = navController)
        }

        composable(
            route = NavRoutes.CHILD,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ChildScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.CHILD_CHOOSE_TASKS,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ChooseTasksScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.PARENT,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ParentScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.APPROVALS,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ApprovalsScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.ADD_TASK,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            AddTaskScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.REWARDS,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            RewardsScreen(navController = navController, username = username)
        }

        composable(
            route = NavRoutes.PROFILE,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ProfileScreen(navController = navController, username = username)
        }
    }
}
