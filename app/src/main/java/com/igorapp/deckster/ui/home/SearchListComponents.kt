package com.igorapp.deckster.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.igorapp.deckster.feature.home.DecksterSearchUiState
import com.igorapp.deckster.feature.home.DecksterUiEvent
import com.igorapp.deckster.ui.theme.PurpleGrey40
import com.igorapp.deckster.ui.theme.WhiteIcon
import com.igorapp.deckster.ui.theme.topGradientColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchToolbar(
    onEvent: (onEvent: DecksterUiEvent) -> Unit,
    state: DecksterSearchUiState,
    navController: NavController,
) {
    val queryString = if (state is DecksterSearchUiState.Content) {
        state.term.orEmpty()
    } else {
        ""
    }

    var showSearch by remember { mutableStateOf(false) }
    var query: String by rememberSaveable { mutableStateOf(queryString) }
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current


    val focusRequester = FocusRequester()

    OutlinedTextField(
        value = query,
        placeholder = {
            Text(
                color = WhiteIcon,
                text = "Search by typing the name of a game.",
            )
        },
        onValueChange = { onQueryChanged ->
            query = onQueryChanged
            onEvent(DecksterUiEvent.OnSearch(query))
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            textColor = Color.White,
            placeholderColor = Color.Transparent,
            cursorColor = PurpleGrey40,
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = topGradientColor
            )
            .onFocusChanged { focusState ->
                showClearButton = (focusState.isFocused)
            },
        maxLines = 1,
        textStyle = MaterialTheme.typography.subtitle1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        trailingIcon = {
            IconButton(onClick = {
                if (query.isEmpty()) {
                    keyboardController?.hide()
                    showSearch = false
                    navController.popBackStack()
                } else {
                    query = ""
                }
            }) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Rounded.Clear,
                    tint = WhiteIcon,
                    contentDescription = "Clear Icon"
                )
            }
        },
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}