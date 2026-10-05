package edu.ucb.project.feature.exchange.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import edu.ucb.project.feature.exchange.presentation.viewmodel.ExchangeViewModel

@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel = koinViewModel()
) {

    val message =
        viewModel.message.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = message.value
        )
    }
}