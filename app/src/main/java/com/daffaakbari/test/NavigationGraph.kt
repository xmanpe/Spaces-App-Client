package com.daffaakbari.test

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daffaakbari.test.session.PreferenceDatastore

@Composable
fun NavigationGraph(
    navController: NavHostController,
    preferenceDatastore: PreferenceDatastore,
    firstDestination: String
) {
    NavHost(navController, startDestination = firstDestination) {
        composable(NavigationItem.Home.route) { HomeScreen(navController, preferenceDatastore) }
        composable(NavigationItem.Spaces.route) { SpacesScreen(navController, preferenceDatastore) }
        composable(NavigationItem.Following.route) { FollowingScreen(navController, preferenceDatastore) }
        composable(NavigationItem.Profile.route) { ProfileScreen(preferenceDatastore) }
        composable("detail/{username}") { navBackStackEntry ->
            val username = navBackStackEntry.arguments?.getString("username")
            if (username != null) {
                DetailSpace(navController, preferenceDatastore, username)
            }
        }
        composable(NavigationItem.Login.route) { Login(navController, preferenceDatastore) }
        composable(NavigationItem.Register.route) { Register(navController) }
        composable(NavigationItem.CreateSpace.route) { CreateSpace(navController, preferenceDatastore) }
        composable("detailpost/{document}") { navBackStackEntry ->
            val document = navBackStackEntry.arguments?.getString("document")
            if (document != null) {
                DetailPost(navController, preferenceDatastore, document)
            }
        }
    }
}
