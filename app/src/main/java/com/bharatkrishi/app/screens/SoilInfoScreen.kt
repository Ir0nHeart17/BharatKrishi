package com.bharatkrishi.app.screens

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.util.Date
import java.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer
import com.bharatkrishi.app.utils.LocalizationManager
import com.bharatkrishi.app.data.FirebaseManager
import com.bharatkrishi.app.utils.GPSLocationManager

// 8-class wheat disease labels
private val CLASS_LABELS = arrayOf(
    "Healthy",
    "Yellow rust",
    "Brown rust",
    "Loose Smut",
    "This is not a wheat crop",
    "Septoria",
    "Stripe rust",
    "Mildew"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilInfoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Wheat Analysis"), 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
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
        ) {
            CropScannerContent()
        }
    }
}

@Composable
fun CropScannerContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var ortEnvironment by remember { mutableStateOf<OrtEnvironment?>(null) }
    var ortSession by remember { mutableStateOf<OrtSession?>(null) }
    var analysisResult by remember { mutableStateOf<PredictionResult?>(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var modelError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // CAMERA permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            errorMessage = "Camera permission denied."
        }
    }

    // CAMERA launcher (High Quality)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            try {
                // Decode Full Res Bitmap
                val rawBitmap = if (android.os.Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(context.contentResolver, currentPhotoUri!!)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, currentPhotoUri!!)
                }

                // Fix rotation and mutable properties
                val bitmap = ensureBitmapMutable(rawBitmap)
                capturedImage = bitmap

                if (ortSession != null && ortEnvironment != null) {
                    val result = runModelOnBitmap(bitmap, ortSession!!, ortEnvironment!!)
                    result.onSuccess {
                        analysisResult = it
                        errorMessage = null
                    }.onFailure {
                        errorMessage = "Analysis failed: ${it.message}"
                        analysisResult = null
                    }
                } else {
                    errorMessage = "Model not loaded yet."
                }
            } catch (e: Exception) {
                errorMessage = "Error processing image: ${e.message}"
            }
        } else {
             // errorMessage = "Camera capture cancelled or failed." // Optional: don't show error on cancel
        }
    }
    // GALLERY launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val rawBitmap = if (android.os.Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                // FIX: convert HARDWARE bitmap to ARGB_8888
                val bitmap = ensureBitmapMutable(rawBitmap)
                capturedImage = bitmap

                if (ortSession != null && ortEnvironment != null) {
                    val result = runModelOnBitmap(bitmap, ortSession!!, ortEnvironment!!)
                    result.onSuccess {
                        analysisResult = it
                        errorMessage = null
                    }.onFailure {
                        errorMessage = "Analysis failed: ${it.message}"
                        analysisResult = null
                    }
                } else {
                    errorMessage = "Model not loaded."
                }

            } catch (e: Exception) {
                errorMessage = "Error loading image: ${e.message}"
            }
        }
    }


    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val env = OrtEnvironment.getEnvironment()
                ortEnvironment = env

                val modelFile = loadOnnxModel(context, "mobilevit_wheat_8class.onnx")
                ortSession = env.createSession(modelFile.absolutePath)

                Log.d("ONNX", "Model loaded successfully")
            } catch (e: Exception) {
                Log.e("ONNX", "Error loading ONNX model", e)
                modelError = "Failed to load model: ${e.message}"
            }
        }
    }

    // SURVEILLANCE INTEGRATION
    val firebaseManager = remember { FirebaseManager() }
    val gpsManager = remember { GPSLocationManager(context) }
    val currentUserId = "test_user_001" // Replace with actual Auth ID

    LaunchedEffect(analysisResult) {
        analysisResult?.let { result ->
            if (result.confidence > 0.70f && result.label != "Healthy" && result.label != "This is not a wheat crop") {
                val loc = gpsManager.getCurrentLocation()
                val lat = loc?.latitude ?: 0.0
                val lng = loc?.longitude ?: 0.0

                firebaseManager.saveDetection(
                    userId = currentUserId,
                    diseaseName = result.label,
                    confidence = result.confidence,
                    imageUrl = "",
                    latitude = lat,
                    longitude = lng,
                    locationName = "Auto-Detected"
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scanner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        LocalizationManager.get("Scan Your Crop"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        LocalizationManager.get("Take a photo of your crop leaves to detect diseases, pests, and nutrient deficiencies"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Check camera permission at runtime
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                // Create temporary file for high-quality image
                                try {
                                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                                    val imageFileName = "JPEG_" + timeStamp + "_"
                                    val storageDir = java.io.File(context.cacheDir, "my_images")
                                    if (!storageDir.exists()) storageDir.mkdirs()
                                    val imageFile = java.io.File.createTempFile(
                                        imageFileName, /* prefix */
                                        ".jpg", /* suffix */
                                        storageDir /* directory */
                                    )
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        context.packageName + ".fileprovider",
                                        imageFile
                                    )
                                    currentPhotoUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    errorMessage = "Could not create image file: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LocalizationManager.get("Open Camera & Analyze"))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LocalizationManager.get("Select from Gallery"))
                    }


    // Model loading status
                    Spacer(modifier = Modifier.height(8.dp))
                    if (ortSession == null && modelError == null) {
                        Text(
                            LocalizationManager.get("Loading AI model..."),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    modelError?.let {
                        Text(
                            it,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    errorMessage?.let {
                        Text(
                            it,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Captured Image Display
        capturedImage?.let { bitmap ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            LocalizationManager.get("Analyzed Image"),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Crop Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratio)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }
            }
        }

        // Result card
        analysisResult?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Prediction",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                LocalizationManager.get("AI Diagnosis"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Surveillance Badge
                         if (result.confidence > 0.70f && result.label != "Healthy" && result.label != "This is not a wheat crop") {
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    LocalizationManager.get("Reported to Disease Surveillance Network"), 
                                    fontSize = 10.sp, 
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Top Prediction
                        Text(
                            text = LocalizationManager.get(result.label),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${LocalizationManager.get("Confidence")}: ${(result.confidence * 100).toInt()}%",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (result.label == "This is not a wheat crop") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = LocalizationManager.get("Please take a different photo."),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Low Confidence Warning
                        if (result.confidence < 0.30f) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Light Orange
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "Low Confidence",
                                        tint = Color(0xFFFF9800)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            LocalizationManager.get("Low Confidence"),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            LocalizationManager.get("Please take the photo again. Ensure the leaf is in focus and well-lit."),
                                            fontSize = 12.sp,
                                            color = Color(0xFFEF6C00)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Confidence Distribution
                        Text(
                            LocalizationManager.get("Confidence Distribution"),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        result.distribution.take(5).forEach { (label, score) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    LocalizationManager.get(label),
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp
                                )
                                LinearProgressIndicator(
                                    progress = score,
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${(score * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        // How it works
        item {
            Text(
                LocalizationManager.get("How it works"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            val steps = listOf(
                ScanStep("1", LocalizationManager.get("Take Photo"), LocalizationManager.get("Capture clear image of affected crop leaves"), Icons.Default.Camera),
                ScanStep("2", LocalizationManager.get("AI Analysis"), LocalizationManager.get("Our AI analyzes the image for diseases and pests"), Icons.Default.AutoAwesome),
                ScanStep("3", LocalizationManager.get("Get Results"), LocalizationManager.get("Receive diagnosis with treatment recommendations"), Icons.Default.Assignment)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEach { step ->
                    StepCard(step)
                }
            }
        }

        // Recent Scans (static demo data)
        item {
            Text(
                LocalizationManager.get("Recent Scans"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            // WHEAT ONLY - Removing Tomato and Cotton as requested
            val recentScans = listOf(
                ScanResult(LocalizationManager.get("Wheat"), LocalizationManager.get("Early Blight Detected"), LocalizationManager.get("Updated 5 days ago"), Color(0xFFFF9800)),
                ScanResult(LocalizationManager.get("Wheat"), LocalizationManager.get("Healthy"), LocalizationManager.get("Updated 5 days ago"), Color(0xFF4CAF50))
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentScans) { scan ->
                    RecentScanCard(scan)
                }
            }
        }
    }
}


fun loadOnnxModel(context: Context, modelName: String): File {
    val onnxFile = File(context.filesDir, modelName)
    val dataFile = File(context.filesDir, "$modelName.data")

    Log.d("ONNX", "Checking ONNX: ${onnxFile.absolutePath} exists=${onnxFile.exists()}")
    Log.d("ONNX", "Checking DATA: ${dataFile.absolutePath} exists=${dataFile.exists()}")

    // Copy ONNX
    if (!onnxFile.exists()) {
        Log.d("ONNX", "Copying .onnx file from assets")
        context.assets.open(modelName).use { input ->
            FileOutputStream(onnxFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    // Copy .data file
    if (!dataFile.exists()) {
        Log.d("ONNX", "Copying .onnx.data file from assets")
        try {
            context.assets.open("$modelName.data").use { input ->
                FileOutputStream(dataFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.e("ONNX", "ERROR COPYING .data FILE → FILE NOT FOUND IN ASSETS")
        }
    }

    Log.d("ONNX", "Final check: onnx.exists=${onnxFile.exists()}, data.exists=${dataFile.exists()}")

    return onnxFile
}
fun ensureBitmapMutable(src: Bitmap): Bitmap {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
        src.config == Bitmap.Config.HARDWARE
    ) {
        // Convert HARDWARE → ARGB_8888
        src.copy(Bitmap.Config.ARGB_8888, true)
    } else {
        src
    }
}

fun runModelOnBitmap(
    bitmap: Bitmap,
    session: OrtSession,
    env: OrtEnvironment
): Result<PredictionResult> {
    return try {
        // 1. Resize to 256x256
        val safeBitmap = ensureBitmapMutable(bitmap)
        val resized = Bitmap.createScaledBitmap(safeBitmap, 256, 256, true)

        // 2. Convert bitmap -> FloatArray (CHW format)
        val input = FloatArray(1 * 3 * 256 * 256)
        val width = 256
        val height = 256

        var idx = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = resized.getPixel(x, y)

                val r = ((pixel shr 16) and 0xFF) / 255f
                val g = ((pixel shr 8) and 0xFF) / 255f
                val b = (pixel and 0xFF) / 255f

                // CHW layout: all R first, then G, then B
                input[idx] = r
                input[idx + width * height] = g
                input[idx + 2 * width * height] = b

                idx++
            }
        }

        val shape = longArrayOf(1, 3, 256, 256)
        val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(input), shape)

        val inputName = session.inputNames.first()
        val output = session.run(mapOf(inputName to tensor))[0].value

        val rawScores: FloatArray = when (output) {
            is FloatArray -> output
            is Array<*> -> output[0] as FloatArray
            else -> throw Exception("Unknown output format: ${output!!::class.java}")
        }

        // Apply Softmax to get probabilities (0.0 - 1.0)
        val scores = softMax(rawScores)

        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: 0
        val label = CLASS_LABELS.getOrNull(maxIndex) ?: "This is not a wheat crop"
        val conf = scores[maxIndex]

        // Create distribution list
        val distribution = scores.mapIndexed { index, score ->
            (CLASS_LABELS.getOrNull(index) ?: "This is not a wheat crop") to score
        }.sortedByDescending { it.second }

        Result.success(PredictionResult(label, conf, distribution))
    } catch (e: Exception) {
        Log.e("ONNX", "Bitmap inference failed", e)
        Result.failure(e)
    }
}



@Composable
fun StepCard(step: ScanStep) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    step.number,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    step.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    step.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                step.icon,
                contentDescription = step.title,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RecentScanCard(scan: ScanResult) {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                scan.cropName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                scan.result,
                fontSize = 12.sp,
                color = scan.statusColor,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                scan.time,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



// Data classes
data class ScanStep(
    val number: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class ScanResult(
    val cropName: String,
    val result: String,
    val time: String,
    val statusColor: Color
)

data class PredictionResult(
    val label: String,
    val confidence: Float,
    val distribution: List<Pair<String, Float>>
)

fun softMax(scores: FloatArray): FloatArray {
    val max = scores.maxOrNull() ?: 0f
    val expScores = scores.map { kotlin.math.exp(it - max) }
    val sumExp = expScores.sum() - 0f // Safety
    if(sumExp == 0f) return scores 
    return expScores.map { (it / sumExp).toFloat() }.toFloatArray()
}

