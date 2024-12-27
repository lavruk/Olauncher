package app.olauncher.ui

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.launch

@Composable
fun AppListScreen1() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val installedApps = remember { getInstalledApps(packageManager) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    val filteredApps = installedApps.filter {
        selectedLetter == null || it.name.startsWith(selectedLetter!!, ignoreCase = true)
    }

    val alphabet = ('A'..'Z').toList()
    var alphabetListState = rememberLazyListState()
    var isTouchingAlphabet by remember { mutableStateOf(false) }
    var touchedLetterY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val highlightLetterScale by animateFloatAsState(
        targetValue = if (isTouchingAlphabet) 1.5f else 1f, label = "highlightLetterScale"
    )

    // Determine the closest letter to the touch point
    LaunchedEffect(isTouchingAlphabet, touchedLetterY) {
        if (isTouchingAlphabet) {
            val letterHeight = with(density) { 16.sp.toPx() }
            val index = ((touchedLetterY / letterHeight) - 1).coerceAtLeast(0f).toInt()
            if (index < alphabet.size) {
                selectedLetter = alphabet[index]
                // Scroll to the selected letter in the alphabet list
                launch {
                    alphabetListState.animateScrollToItem(index)
                }
            }
        } else {
            selectedLetter = null
        }
    }

        Box(modifier = Modifier.fillMaxSize()) {
            AppList(
                apps = filteredApps,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                isTouchingAlphabet = true
                                touchedLetterY = offset.y
                                tryAwaitRelease()
                                isTouchingAlphabet = false
                            },
                            onTap = {
                                isTouchingAlphabet = false
                            }
                        )
                    }
            ) {
                // Highlighted letter
                AnimatedVisibility(
                    visible = isTouchingAlphabet,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = selectedLetter?.toString() ?: "",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .scale(highlightLetterScale)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(),
                    state = alphabetListState,
                ) {
                    items(alphabet) { letter ->
                        val scale by animateFloatAsState(
                            targetValue = if (selectedLetter == letter) 1.5f else 1f,
                            label = "scale"
                        )
                        val alpha by animateFloatAsState(
                            targetValue = if (selectedLetter == letter || !isTouchingAlphabet) 1f else 0.5f,
                            label = "alpha"
                        )

                        Text(
                            text = letter.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedLetter == letter) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier
                                .scale(scale)
                                .padding(vertical = 0.dp)
                                .padding(end = 8.dp),
                        )
                    }
                }
            }
        }
}

@Composable
fun AppList(apps: List<AppInfo>, modifier: Modifier = Modifier) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true // Start the animation immediately
        }
    }
    val listState = rememberLazyListState()

    // Use derivedStateOf to calculate the first visible item index
    val firstVisibleItemIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    // Observe changes in the first visible item and log the app name
    LaunchedEffect(firstVisibleItemIndex) {
        if (apps.isNotEmpty()) {
            val firstVisibleApp = apps[firstVisibleItemIndex]
            Log.d("AppList", "First visible app: ${firstVisibleApp.name}")
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(initialAlpha = 0.3f),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(apps, key = { app -> app.packageName }) { app ->
                AppItem(app = app)
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo) {
    var rowHeight by remember { mutableStateOf(0) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                getBackgroundColorForItem(app.name.firstOrNull())
            )
            .clickable { /* Handle app click */ }
            .padding(8.dp)
            .onGloballyPositioned { coordinates ->
                rowHeight = coordinates.size.height
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            app.icon,
            contentDescription = "App Icon",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = app.name,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun getBackgroundColorForItem(firstLetter: Char?): Color {
    if (firstLetter == null) return Color.Gray // Default color

    // Define a base color
    val baseColor = Color(0xFF4A6572) // Example: a blueish color

    // Adjust the luminance based on the letter's position in the alphabet
    val index = firstLetter.uppercaseChar() - 'A'
    val luminanceShift = index / 26f // Normalize index to 0.0 - 1.0 range

    // Create a lighter or darker shade based on the letter's position
    val adjustedColor = ColorUtils.blendARGB(
        baseColor.toArgb(),
        if (luminanceShift < 0.5) Color.Black.toArgb() else Color.White.toArgb(),
        luminanceShift.coerceIn(0f, 1f) // Ensure the shift is within bounds
    )

    return Color(adjustedColor)
}

fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    return apps.mapNotNull { appInfo ->
        try {
            val icon = appInfo.loadIcon(packageManager)
            AppInfo(
                name = appInfo.loadLabel(packageManager).toString(),
                packageName = appInfo.packageName,
                icon = icon
            )
        } catch (e: Exception) {
            Log.e("AppInfo", "Error loading app info: ${appInfo.packageName}", e)
            null
        }
    }.filter {
        it.name.isNotBlank()
    }.sortedBy {
        it.name
    }
}

@Composable
fun Icon(drawable: Drawable, contentDescription: String?, modifier: Modifier = Modifier, tint: Color) {
    val bitmap = remember(drawable) { drawable.toBitmap() }
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

fun Drawable.toBitmap(): ImageBitmap {
    val bitmap = android.graphics.Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap.asImageBitmap()
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)