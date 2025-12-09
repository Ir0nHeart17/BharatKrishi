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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bharatkrishi.app.AIChatViewModel
import com.bharatkrishi.app.ChatMessage
import com.bharatkrishi.app.utils.LocalizationManager
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
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (LocalizationManager.isHindi) "hi-IN" else "en-US")
                putExtra(RecognizerIntent.EXTRA_PROMPT, LocalizationManager.get("Speak now..."))
            }
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, LocalizationManager.get("Speech recognition not supported"), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, LocalizationManager.get("Permission denied"), Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = "AI Assistant",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(LocalizationManager.get("KrishiMitra AI"), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text(LocalizationManager.get("Online"), fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
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
            
            // Image Preview
            var selectedImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
            
            if (selectedImageBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    androidx.compose.foundation.Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                    )
                    IconButton(
                        onClick = { selectedImageBitmap = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Image")
                    }
                }
            }

            // Image Picker
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            // Decode bitmap
                            val bitmap = if (android.os.Build.VERSION.SDK_INT < 28) {
                                @Suppress("DEPRECATION")
                                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            } else {
                                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                                android.graphics.ImageDecoder.decodeBitmap(source)
                            }
                            
                            // Resize to max 1024px dimension to avoid OOM and slow uploads
                            val maxDimension = 1024
                            val rBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
                                val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
                                android.graphics.Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                            } else {
                                bitmap
                            }

                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                selectedImageBitmap = rBitmap
                            }
                        } catch (e: Exception) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            // Input Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                        Icon(Icons.Default.Mic, contentDescription = "Speak", tint = MaterialTheme.colorScheme.primary)
                    }
                    
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Image, contentDescription = "Pick Image", tint = MaterialTheme.colorScheme.primary)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text(LocalizationManager.get(if (selectedImageBitmap != null) "Add a caption (optional)..." else "Ask me anything..."), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank() || selectedImageBitmap != null) {
                                viewModel.sendMessage(messageText, selectedImageBitmap)
                                messageText = ""
                                selectedImageBitmap = null
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = if (messageText.isNotBlank() || selectedImageBitmap != null) MaterialTheme.colorScheme.primary else Color.Gray
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                    }
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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.image != null) {
                    androidx.compose.foundation.Image(
                        bitmap = message.image.asImageBitmap(),
                        contentDescription = "Sent Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(bottom = 8.dp)
                    )
                }
                
                if (message.text.isNotBlank()) {
                    Text(
                        message.text,
                        color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
                if (!message.isFromUser) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}