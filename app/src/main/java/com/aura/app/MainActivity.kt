package com.aura.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.app.navigation.AuraNavGraph
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = hiltViewModel()
            val userProfile by userViewModel.userProfile.collectAsState()
            val ageGroup = userProfile?.ageGroup ?: "adults"

            AuraTheme(ageGroup = ageGroup) {
                AuraNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    userViewModel = userViewModel,
                )
            }
        }
    }
}
