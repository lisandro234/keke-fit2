package com.pec.kekefit.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

private val KekeGreen = Color(0xFF38BDF8)
private val KekeDark = Color(0xFF082F49)
private val KekeBubble = Color(0xFF0F4C81)
private val KekeUser = Color(0xFF38BDF8)
private val KekeSoft = Color(0xFFE0F2FE)

@Composable
fun ChatScreen(
    onVolver: () -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KekeDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KekeBubble)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Text(
                text = "🥗",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "KekeBot ${uiState.modelo}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Text(
                    text = "Asistente inteligente de Keke-Fit",
                    color = KekeSoft,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = { viewModel.cambiarModelo() },
                colors = ButtonDefaults.buttonColors(containerColor = KekeGreen),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (uiState.modelo == "2.0") "2.0" else "2.1", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    WelcomeMessage()
                }
            }

            items(uiState.messages) { message ->
                MessageBubble(message = message)
            }

            if (uiState.isLoading) {
                item {
                    LoadingBubble()
                }
            }
        }

        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFB71C1C)
                )
            ) {
                Text(
                    text = error,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }

            LaunchedEffect(error) {
                delay(4000)
                viewModel.clearError()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KekeBubble)
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Preguntale a KekeBot...",
                        color = Color(0xFFBFDBFE),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = KekeGreen,
                    unfocusedBorderColor = Color(0xFF93C5FD),
                    cursorColor = KekeGreen,
                    focusedContainerColor = Color(0x14000000),
                    unfocusedContainerColor = Color(0x14000000)
                ),
                shape = RoundedCornerShape(24.dp),
                minLines = 1,
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = { viewModel.clearChat() },
                    containerColor = KekeBubble,
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar chat",
                        tint = Color.White
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    containerColor = KekeGreen,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Text(
                text = "🥗",
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 6.dp, top = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .background(
                    color = if (isUser) KekeUser else KekeBubble,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun LoadingBubble() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🥗",
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 6.dp)
        )

        Box(
            modifier = Modifier
                .background(KekeBubble, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = KekeGreen,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("KekeBot está pensando...", color = KekeSoft, fontSize = 13.sp)
        }
    }
}

@Composable
private fun WelcomeMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🥗",
            fontSize = 50.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¡Hola! Soy KekeBot",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tu asistente inteligente de Keke-Fit.\nPreguntame sobre comidas, calorías o hábitos.",
            color = KekeSoft,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
