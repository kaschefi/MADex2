package com.example.quiz_app_starter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quiz_app_starter.MainMenuScreen
import com.example.quiz_app_starter.model.getDummyQuestions
import com.example.quiz_app_starter.presentation.FinishScreen
import com.example.quiz_app_starter.presentation.QuestionScreen
import com.example.quiz_app_starter.presentation.QuestionScreenViewModel


sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Question : Screen("question")
    object Finish : Screen("finish/{points}") {
        fun createRoute(points: Int) = "finish/$points"
    }
}
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        // 1. Main Menu
        composable(route = Screen.MainMenu.route) {
            MainMenuScreen(
                modifier = Modifier,
                onPlayClick = {
                    navController.navigate(Screen.Question.route)
                }
            )
        }


        composable(route = Screen.Question.route) {
            val questionViewModel: QuestionScreenViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return QuestionScreenViewModel(getDummyQuestions()) as T
                    }
                }
            )

            QuestionScreen(
                viewModel = questionViewModel,
                onQuizFinished = { score ->
                    navController.navigate(Screen.Finish.createRoute(score)) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                    }
                },
                onExit = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }

        // Finish Screen
        composable(
            route = Screen.Finish.route,
            arguments = listOf(navArgument("points") { type = NavType.IntType })
        ) { backStackEntry ->
            val points = backStackEntry.arguments?.getInt("points") ?: 0

            FinishScreen(
                score = points,
                onRestart = {
                    navController.navigate(Screen.Question.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                    }
                },
                onHome = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
