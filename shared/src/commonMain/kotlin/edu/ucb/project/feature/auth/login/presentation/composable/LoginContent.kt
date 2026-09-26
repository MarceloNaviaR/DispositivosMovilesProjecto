package edu.ucb.project.feature.auth.login.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LoginContent(

    username: String,

    password: String,

    onUsernameChange: (String) -> Unit,

    onPasswordChange: (String) -> Unit,

    onLoginClick: () -> Unit

) {


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {


        Text(
            text = "LOG IN DE LA PAGINA"
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        TextField(

            value = username,

            onValueChange = onUsernameChange,

            label = {
                Text("Nombre de usuario")
            }

        )


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        TextField(

            value = password,

            onValueChange = onPasswordChange,

            label = {
                Text("Contraseña")
            }

        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        Button(

            onClick = onLoginClick

        ) {

            Text(
                "ENTRAR"
            )

        }

    }

}