package com.daffaakbari.test

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Login.route) {
        composable(NavigationItem.Home.route) { HomeScreen(navController) }
        composable(NavigationItem.Spaces.route) { SpacesScreen(navController) }
        composable(NavigationItem.Following.route) { FollowingScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen() }
        composable(NavigationItem.Detail.route) { DetailSpace() }
        composable(NavigationItem.Login.route) { Login(navController) }
        composable(NavigationItem.Register.route) { Register(navController) }
        composable(NavigationItem.CreateSpace.route) { CreateSpace(navController) }
    }
}
