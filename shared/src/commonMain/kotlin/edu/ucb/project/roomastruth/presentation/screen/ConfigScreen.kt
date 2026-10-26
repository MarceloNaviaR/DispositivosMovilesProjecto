package edu.ucb.project.roomastruth.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucb.project.roomastruth.presentation.composable.AddConfigForm
import edu.ucb.project.roomastruth.presentation.composable.ConfigItemRow
import edu.ucb.project.roomastruth.presentation.state.ConfigViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfigScreen(viewModel: ConfigViewModel = koinViewModel()) {

    // Reactivo: la UI se recompone cada vez que Room emite una nueva lista.
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Configuración local",
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(
                    onClick = { showDeleteAllDialog = true },
                    enabled = state.items.isNotEmpty()
                ) { Text("Borrar todo") }
            }

            AddConfigForm(
                keyInput = state.keyInput,
                valueInput = state.valueInput,
                error = state.error,
                onKeyChange = viewModel::onKeyChange,
                onValueChange = viewModel::onValueChange,
                onAdd = viewModel::onAddClick
            )

            Spacer(Modifier.height(4.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay configuraciones guardadas")
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        ConfigItemRow(item = item, onDelete = { viewModel.onDelete(item.id) })
                    }
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Borrar todo") },
            text = { Text("Se eliminarán todas las configuraciones. ¿Continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteAll()
                    showDeleteAllDialog = false
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
