package lexitcom.lexitcomapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lexitcom.lexitcomapp.viewmodel.UserViewModel

@Composable
fun Settings(userViewModel: UserViewModel) {
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    var isLevelDialogOpen by remember { mutableStateOf(false) }
    var isNotificationsChecked by remember { mutableStateOf(false) }
    var isDarkModeChecked by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedLevel by remember { mutableStateOf("Beginner") }
    val languageMap = mapOf(
        "English" to Pair("English","en"),
        "Español" to Pair("Spanish","es"),
        "Français" to Pair("French","fr"),
        "Deutsch" to Pair("German","de"),
        "עברית" to Pair("Hebrew","he")
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "General Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        DropdownSettingItem(
            label = "Language",
            description = "Select your preferred language",
            isDialogOpen  = isLanguageDialogOpen,
            selectedOption = selectedLanguage,
            options = languageMap.keys.toList(),
            onOptionSelected = { option ->
                selectedLanguage = option
                isLanguageDialogOpen = false
                // Handle language change here
            },
            onClick = { isLanguageDialogOpen = true }
        )

        DropdownSettingItem(
            label = "Level",
            description = "Select your level",
            isDialogOpen = isLevelDialogOpen,
            selectedOption = selectedLevel,
            options = listOf("Beginner", "Intermediate", "Advanced"),
            onOptionSelected = { option ->
                selectedLevel = option
                isLevelDialogOpen = false
                // Handle level change here
            },
            onClick = { isLevelDialogOpen = true }
        )

        SettingItem(
            label = "Dark Mode",
            description = "Enable dark mode for the app",
            isChecked = isDarkModeChecked,
            onCheckedChange = {
                isDarkModeChecked =
                    !isDarkModeChecked
                /* Handle dark mode change */
            }
        )

        SettingItem(
            label = "Notifications",
            description = "Receive notifications for updates and events",
            isChecked = isNotificationsChecked,
            onCheckedChange = {
                isNotificationsChecked =
                    !isNotificationsChecked
                /* Handle notifications change */
            }
        )
    }
}

@Composable
fun DropdownSettingItem(
    label: String,
    description: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onClick: () -> Unit,
    isDialogOpen: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = description, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onClick() })
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = isDialogOpen,
            onDismissRequest = { onOptionSelected(selectedOption) },
            modifier = Modifier.wrapContentSize()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                    }
                ) {
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
//@Composable
//fun ThemeSwitcherTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
//) {
//    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}