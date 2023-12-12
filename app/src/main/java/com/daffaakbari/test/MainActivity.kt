package com.daffaakbari.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffaakbari.test.session.PreferenceDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val iconActiveColor = Color.Black
val iconInactiveColor = Color(0xFFA0A0A0)

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var preferenceDatastore = PreferenceDatastore(this)
            val navController = rememberNavController()
            AppTheme {
                // Cek Session
                CoroutineScope(Dispatchers.IO).launch {
                    preferenceDatastore.getSession().collect{
                        withContext(Dispatchers.Main) {
                            Log.d("session", it.toString())
                        }
                    }
                }
                MainScreen(navController)
            }
        }
    }
}

@Composable
fun LogRegScreen(navController: NavHostController) {
    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithSearch(header: String) {
    SmallTopAppBar(
        title = {
            Text(
                text = header,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = { /* Handle search action */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBackBarWithSearch() {
    SmallTopAppBar(
        title = {
            Text(
                text = "",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle search action */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Spaces,
        NavigationItem.Following,
        NavigationItem.Profile
    )
    val currentRoute = navController.currentDestination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) iconActiveColor else iconInactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) iconActiveColor else iconInactiveColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (navController.currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = iconActiveColor,
                    unselectedIconColor = iconInactiveColor,
                    selectedTextColor = iconActiveColor,
                    unselectedTextColor = iconInactiveColor
                )
            )
        }
    }
}

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object Spaces : NavigationItem("spaces", Icons.Filled.Email, "Spaces")
    object Following : NavigationItem("following", Icons.Filled.List, "Following")
    object Profile : NavigationItem("profile", Icons.Filled.Person, "Profile")
    object Detail : NavigationItem("detail", Icons.Filled.Info, "Detail")
    object CreateSpace : NavigationItem("createSpace", Icons.Filled.Build, "CreateSpace")
    object Login : NavigationItem("login", Icons.Filled.Check, "Login")
    object Register : NavigationItem("register", Icons.Filled.Create, "Register")
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    AppTheme {
//        MainScreen()
//    }
//}
