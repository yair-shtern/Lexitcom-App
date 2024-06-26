package lexitcom.lexitcomapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lexitcom.lexitcomapp.R
import lexitcom.lexitcomapp.viewmodel.UserViewModel

@Composable
fun Profile(
    userViewModel: UserViewModel,
) {
    if (userViewModel.currentUser.value != null) {
        var isEditing by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf(userViewModel.currentUser.value!!.name) }
        var email by remember { mutableStateOf(userViewModel.currentUser.value!!.email) }
        var password by remember { mutableStateOf(userViewModel.currentUser.value!!.password) }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Your Profile", style = MaterialTheme.typography.headlineMedium)

            ProfileField(label = "Name", value = name, isEditing = isEditing) { newValue ->
                if (isEditing) name = newValue
            }

            ProfileField(
                label = "Email",
                value = email,
                isEditing = isEditing,
                isEmail = true
            ) { newValue ->
                if (isEditing) email = newValue
            }

            ProfileField(
                label = "Password", value = password, isPassword = true, isEditing = isEditing
            ) { newValue ->
                if (isEditing) password = newValue
            }

            if (isEditing) {
                Button(
                    onClick = {
                        userViewModel.updateUserData(
                            name = name,
                            email = email,
                            password = password
                        )
                        isEditing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Update Profile", color = Color.White)
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Edit Profile", color = Color.White)
                }
            }
        }

    } else {
        Text(
            text = "No user Found",
            modifier = Modifier.fillMaxSize(),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}


@Composable
fun ProfileField(
    label: String,
    value: String,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(value = value,
            readOnly = !isEditing,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { newValue -> onValueChange(newValue) },
            textStyle = MaterialTheme.typography.bodyMedium,
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else if (isEmail) KeyboardOptions(
                keyboardType = KeyboardType.Email
            ) else KeyboardOptions.Default,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surface,
                cursorColor = Color.Black,
                disabledLabelColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(
                    width = if (isEditing) 1.dp else 0.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                ),
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                }
            })
    }
}