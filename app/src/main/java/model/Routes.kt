package model

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object AdminPanel : Routes("admin_panel")
    object StudentHome : Routes("student_home")
    object Profile : Routes("profile")
}