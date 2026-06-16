package com.rork.sergolfandroid.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rork.sergolfandroid.ui.EventsViewModel
import com.rork.sergolfandroid.ui.screens.ClubScreen
import com.rork.sergolfandroid.ui.screens.EventDetailScreen
import com.rork.sergolfandroid.ui.screens.EventsScreen
import com.rork.sergolfandroid.ui.theme.AppColors

private const val ROUTE_EVENTS = "events"
private const val ROUTE_CLUB = "club"
private const val ROUTE_DETAIL = "detail"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val eventsViewModel: EventsViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute == ROUTE_EVENTS || currentRoute == ROUTE_CLUB

    Scaffold(
        containerColor = AppColors.BackgroundDark,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = AppColors.DarkGreen) {
                    NavBarItem(
                        selected = currentRoute == ROUTE_EVENTS,
                        label = "Events",
                        icon = { Icon(Icons.Filled.CalendarMonth, "Events") },
                        onClick = { navController.navigateTab(ROUTE_EVENTS) },
                    )
                    NavBarItem(
                        selected = currentRoute == ROUTE_CLUB,
                        label = "The Club",
                        icon = { Icon(Icons.Outlined.Info, "The Club") },
                        onClick = { navController.navigateTab(ROUTE_CLUB) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_EVENTS,
            modifier = Modifier.padding(padding),
        ) {
            composable(ROUTE_EVENTS) {
                EventsScreen(
                    viewModel = eventsViewModel,
                    onEventClick = { id -> navController.navigate("$ROUTE_DETAIL/$id") },
                )
            }
            composable(ROUTE_CLUB) {
                ClubScreen()
            }
            composable(
                route = "$ROUTE_DETAIL/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
            ) { entry ->
                val eventId = entry.arguments?.getString("eventId")
                EventDetailScreen(
                    event = eventsViewModel.eventById(eventId),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RowScope.NavBarItem(
    selected: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColors.Gold,
            selectedTextColor = AppColors.Gold,
            unselectedIconColor = AppColors.TextMuted,
            unselectedTextColor = AppColors.TextMuted,
            indicatorColor = AppColors.CardGreen,
        ),
    )
}
