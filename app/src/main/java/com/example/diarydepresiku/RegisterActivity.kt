package com.example.diarydepresiku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.diarydepresiku.ui.theme.SoftYellow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import com.example.diarydepresiku.ui.theme.DiarydepresikuTheme


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DiarydepresikuTheme {
                RegisterScreen { email, password, name ->
                    lifecycleScope.launch {
                        val app = application as MyApplication
                        val resp = app.diaryApi.register(AuthRequest(email, password, name))
                        if (resp.isSuccessful) {
                            app.reminderPreferences.setUserName(name)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegister: (String, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.label_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onRegister(email, password, name) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_register))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = SoftYellow
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.privacy_message),
                color = SoftYellow,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
