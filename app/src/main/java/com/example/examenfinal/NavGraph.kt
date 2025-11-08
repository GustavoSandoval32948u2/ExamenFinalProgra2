package com.example.examenfinal


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import model.Routes

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        composable(Routes.StudentHome.route) {
            StudentHomeScreen(navController)
        }

        composable(Routes.AdminPanel.route) {
            AdminPanelScreen(navController)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
