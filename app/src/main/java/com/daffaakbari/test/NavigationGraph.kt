package com.daffaakbari.test

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) { HomeScreen(navController) }
        composable(NavigationItem.Spaces.route) { SpacesScreen() }
        composable(NavigationItem.Following.route) { FollowingScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen() }
        composable(NavigationItem.Detail.route) { DetailPost() }
    }
}
