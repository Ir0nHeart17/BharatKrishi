package com.bharatkrishi.app.screens

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bharatkrishi.app.AIChatViewModel
import com.bharatkrishi.app.ChatMessage
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(navController: NavController, viewModel: AIChatViewModel = viewModel()) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Text To Speech
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale("hi", "IN") // Default to Hindi/Indian English
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Speech To Text
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!resultText.isNullOrEmpty()) {
                messageText = resultText[0]
                viewModel.sendMessage(messageText)
                messageText = ""
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = "AI Assistant",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("KrishiMitra AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Online", fontSize = 12.sp, color = Color(0xFF4CAF50))
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message, onSpeak = {
                    tts?.speak(message.text, TextToSpeech.QUEUE_FLUSH, null, null)
                })
            }
        }

        // Input Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                    Icon(Icons.Default.Mic, contentDescription = "Speak", tint = Color(0xFF2E7D32))
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask me anything...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        cursorColor = Color(0xFF2E7D32)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0xFF2E7D32)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, onSpeak: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) Color(0xFF2E7D32) else Color.White
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    message.text,
                    color = if (message.isFromUser) Color.White else Color.Black,
                    fontSize = 14.sp
                )
                if (!message.isFromUser) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}