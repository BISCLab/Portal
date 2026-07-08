package com.bisc.portal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bisc.portal.ui.about.AboutScreen
import com.bisc.portal.ui.home.HomeScreen
import com.bisc.portal.ui.lock.LockScreen
import com.bisc.portal.ui.settings.SettingsScreen
import com.bisc.portal.ui.settings.SettingsViewModel
import com.bisc.portal.ui.stats.StatsScreen
import com.bisc.portal.ui.theme.PortalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val theme by settingsVm.theme.collectAsState()
            val lockEnabled by settingsVm.lockEnabled.collectAsState()
            // rememberSaveable: survives rotation; resets on process restart → lock shown on each cold start
            var lockVerified by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()

            PortalTheme(themePreference = theme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (lockEnabled && !lockVerified) {
                        LockScreen(vm = settingsVm, onUnlock = { lockVerified = true })
                    } else {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                HomeScreen(
                                    onNavigateToSettings = { navController.navigate("settings") },
                                    onNavigateToAbout = { navController.navigate("about") }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    onBack = { navController.popBackStack() },
                                    onNavigateToStats = { navController.navigate("stats") },
                                    onLockEnabled = { lockVerified = true }
                                )
                            }
                            composable("about") {
                                AboutScreen(onBack = { navController.popBackStack() })
                            }
                            composable("stats") {
                                StatsScreen(onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
