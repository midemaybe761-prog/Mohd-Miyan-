package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.BottomNavItem
import com.example.ui.components.StudioBottomBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CreateVideoScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ScriptWriterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThumbnailScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.StudioDarkBg
import com.example.ui.viewmodel.MainViewModel

object StudioRoutes {
    const val DASHBOARD = "dashboard"
    const val CREATE_VIDEO = "create_video"
    const val SCRIPT_WRITER = "script_writer"
    const val VIDEO_EDITOR = "video_editor/{projectId}"
    const val VIDEO_PLAYER = "video_player/{projectId}"
    const val THUMBNAIL_GENERATOR = "thumbnail_generator/{projectId}"
    const val EXPORT_SCREEN = "export_screen/{projectId}"
    const val PROJECTS_LIST = "projects_list"
    const val SETTINGS = "settings"
    const val ADMIN_PANEL = "admin_panel"

    fun videoEditorRoute(projectId: String) = "video_editor/$projectId"
    fun videoPlayerRoute(projectId: String) = "video_player/$projectId"
    fun thumbnailRoute(projectId: String) = "thumbnail_generator/$projectId"
    fun exportRoute(projectId: String) = "export_screen/$projectId"
}

@Composable
fun StudioNavHost(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = StudioDarkBg,
        bottomBar = {
            StudioBottomBar(
                currentRoute = currentRoute,
                onNavigate = { item ->
                    when (item) {
                        BottomNavItem.HOME -> {
                            navController.navigate(StudioRoutes.DASHBOARD) {
                                popUpTo(StudioRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        BottomNavItem.GENERATE -> {
                            navController.navigate(StudioRoutes.CREATE_VIDEO) {
                                popUpTo(StudioRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        BottomNavItem.PROJECTS -> {
                            navController.navigate(StudioRoutes.PROJECTS_LIST) {
                                popUpTo(StudioRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        BottomNavItem.THUMBNAIL -> {
                            val activeProjId = viewModel.currentProject.value?.id ?: "default"
                            navController.navigate(StudioRoutes.thumbnailRoute(activeProjId)) {
                                popUpTo(StudioRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        BottomNavItem.SETTINGS -> {
                            navController.navigate(StudioRoutes.SETTINGS) {
                                popUpTo(StudioRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            NavHost(
                navController = navController,
                startDestination = StudioRoutes.DASHBOARD
            ) {
                composable(StudioRoutes.DASHBOARD) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCreate = {
                            navController.navigate(StudioRoutes.CREATE_VIDEO)
                        },
                        onNavigateToScript = {
                            navController.navigate(StudioRoutes.SCRIPT_WRITER)
                        },
                        onNavigateToEditor = { projectId ->
                            navController.navigate(StudioRoutes.videoEditorRoute(projectId))
                        },
                        onNavigateToPlayer = { projectId ->
                            navController.navigate(StudioRoutes.videoPlayerRoute(projectId))
                        },
                        onNavigateToThumbnails = { projectId ->
                            navController.navigate(StudioRoutes.thumbnailRoute(projectId ?: "default"))
                        },
                        onNavigateToProjects = {
                            navController.navigate(StudioRoutes.PROJECTS_LIST)
                        },
                        onNavigateToExport = { projectId ->
                            navController.navigate(StudioRoutes.exportRoute(projectId))
                        },
                        onNavigateToSettings = {
                            navController.navigate(StudioRoutes.SETTINGS)
                        },
                        onNavigateToAdmin = {
                            navController.navigate(StudioRoutes.ADMIN_PANEL)
                        }
                    )
                }

                composable(StudioRoutes.CREATE_VIDEO) {
                    CreateVideoScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onVideoGenerated = { projectId ->
                            navController.navigate(StudioRoutes.videoPlayerRoute(projectId)) {
                                popUpTo(StudioRoutes.DASHBOARD)
                            }
                        }
                    )
                }

                composable(
                    route = StudioRoutes.VIDEO_PLAYER,
                    arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                    VideoPlayerScreen(
                        viewModel = viewModel,
                        projectId = projectId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEditor = { id ->
                            navController.navigate(StudioRoutes.videoEditorRoute(id))
                        },
                        onNavigateToSettings = {
                            navController.navigate(StudioRoutes.SETTINGS)
                        }
                    )
                }

                composable(StudioRoutes.SCRIPT_WRITER) {
                    ScriptWriterScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onTransferToStudio = { topic, script ->
                            val projId = viewModel.createNewProject(
                                title = topic,
                                prompt = topic
                            )
                            navController.navigate(StudioRoutes.videoEditorRoute(projId)) {
                                popUpTo(StudioRoutes.DASHBOARD)
                            }
                        },
                        onVideoGenerated = { projectId ->
                            navController.navigate(StudioRoutes.videoPlayerRoute(projectId)) {
                                popUpTo(StudioRoutes.DASHBOARD)
                            }
                        }
                    )
                }

                composable(
                    route = StudioRoutes.VIDEO_EDITOR,
                    arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                    VideoEditorScreen(
                        viewModel = viewModel,
                        projectId = projectId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToExport = { id ->
                            navController.navigate(StudioRoutes.exportRoute(id))
                        }
                    )
                }

                composable(
                    route = StudioRoutes.THUMBNAIL_GENERATOR,
                    arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId")
                    ThumbnailScreen(
                        viewModel = viewModel,
                        projectId = projectId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = StudioRoutes.EXPORT_SCREEN,
                    arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                    ExportScreen(
                        viewModel = viewModel,
                        projectId = projectId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(StudioRoutes.PROJECTS_LIST) {
                    ProjectsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEditor = { id ->
                            navController.navigate(StudioRoutes.videoEditorRoute(id))
                        },
                        onNavigateToPlayer = { id ->
                            navController.navigate(StudioRoutes.videoPlayerRoute(id))
                        },
                        onNavigateToExport = { id ->
                            navController.navigate(StudioRoutes.exportRoute(id))
                        }
                    )
                }

                composable(StudioRoutes.SETTINGS) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAdmin = {
                            navController.navigate(StudioRoutes.ADMIN_PANEL)
                        }
                    )
                }

                composable(StudioRoutes.ADMIN_PANEL) {
                    AdminScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
