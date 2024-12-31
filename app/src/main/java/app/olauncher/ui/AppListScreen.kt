package app.olauncher.ui

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.olauncher.helper.getAppsList
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class AppListViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _appList = mutableStateOf<List<AppInfo>>(emptyList())
    val appList: State<List<AppInfo>> = _appList

    private val _filteredAppList = mutableStateOf<List<AppInfo>>(emptyList())
    val filteredAppList: State<List<AppInfo>> = _filteredAppList

    private val _selectedLetter = mutableStateOf<Char?>(null)
    val selectedLetter: State<Char?> = _selectedLetter

    private val _alphabetList = mutableStateOf<List<Char>>(('A'..'Z').toList() + '#')
    val alphabetList: State<List<Char>> = _alphabetList

    private val _touchPoint = mutableStateOf<Offset>(Offset(0f, 0f))
    val touchPoint: State<Offset> = _touchPoint

    private val _isAlphabetListTouched = mutableStateOf<Boolean>(false)
    val isAlphabetListTouched: State<Boolean> = _isAlphabetListTouched

    init {
        // Load your app list here (e.g., from PackageManager)
        loadAppList()
    }

    private fun loadAppList() = viewModelScope.launch {
        // Placeholder - Replace with your app loading logic
        _appList.value = getAppsList(application).map { AppInfo(it.appLabel, it.appPackage, it.appIcon) }
        _filteredAppList.value = _appList.value
        _alphabetList.value = _appList.value.map { it.name.first().uppercaseChar() }.distinct().sorted()
    }

    fun onLetterTouched(letter: Char, touchPoint: Offset) {
        _selectedLetter.value = letter
        _touchPoint.value = touchPoint
        _isAlphabetListTouched.value = true
        filterAppList(letter)
    }

    fun onAlphabetListTouchEnded() {
        _isAlphabetListTouched.value = false
        _selectedLetter.value = null
        _filteredAppList.value = _appList.value
    }

    private fun filterAppList(letter: Char) {
        _filteredAppList.value = if (letter == '#') {
            _appList.value.filter { it.name.first().isDigit() }
        } else {
            _appList.value.filter { it.name.startsWith(letter, ignoreCase = true) }
        }
    }
}

@Composable
fun AppListScreen(viewModel: AppListViewModel) {
    val appList by viewModel.appList
    val filteredAppList by viewModel.filteredAppList
    val selectedLetter by viewModel.selectedLetter
    val alphabetList by viewModel.alphabetList
    val touchPoint by viewModel.touchPoint
    val isAlphabetListTouched by viewModel.isAlphabetListTouched


    val density = LocalDensity.current
    val screenHeight = with(density) {LocalConfiguration.current.screenHeightDp.dp.toPx()}
    val alphabetTextSize = 20.sp
    val alphabetTextPadding = 6.dp
    val alphabetItemHeight = with(density){ 2 * alphabetTextPadding.toPx() + alphabetTextSize.toPx() }.roundToInt()
    val alphabetContainerOffsetTop = screenHeight * 0.45f

    Box(modifier = Modifier.fillMaxSize()) {

        // App List (LazyColumn)
        AppList(filteredAppList)

        // Alphabet List (Side Bar)
        AlphabetListSideBar(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = with(density){alphabetContainerOffsetTop.toDp()}),
            alphabetList,
            selectedLetter,
            touchPoint,
            isAlphabetListTouched,
            alphabetTextSize,
            alphabetItemHeight,
            onLetterTouched = { letter, y ->
                viewModel.onLetterTouched(letter, y)
            },
            onAlphabetListTouchEnded = { viewModel.onAlphabetListTouchEnded() }
        )

        // Overlay for selected letter (optional, based on your design)
        if (selectedLetter != null) {
            SelectedLetterOverlay(
                Modifier.align(Alignment.CenterEnd),
                selectedLetter!!,
                touchPoint, alphabetContainerOffsetTop)
        }
    }
}

@Composable
fun AppList(appList: List<AppInfo>) {
    val groupedApps = appList.groupBy { it.name.first().uppercaseChar() }
    val density = LocalDensity.current
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 32.dp),
        state = listState
    ) {
        groupedApps.forEach { (letter, apps) ->
            item {
                AppListTitle(letter)
            }
            items(apps, key = { it.uniqueId }) { app ->
                // Calculate alpha based on item position
                val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == app.uniqueId }
                val alpha = if (itemInfo != null) {
                    val topOffset = itemInfo.offset
                    val threshold = with(density) { 100.dp.toPx() } // Adjust threshold as needed
                    // Corrected alpha calculation:
                    (topOffset.coerceAtLeast(0) / threshold).coerceAtMost(1f)
                } else {
                    1f // Default to fully opaque if itemInfo is not found
                }

                AppItem(app, alpha)
            }
        }
    }
}

@Composable
fun AppListTitle(letter: Char) {
    Text(
        text = letter.toString(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun AppItem(app: AppInfo, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                this.alpha = alpha
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bitmap = app.icon.toBitmap()
        Image(
            bitmap = bitmap,
            contentDescription = "App Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = app.name, color = Color.White)
    }
}

@Composable
fun AlphabetListSideBar(
    modifier: Modifier = Modifier,
    alphabetList: List<Char>,
    selectedLetter: Char?,
    touchPoint: Offset,
    isAlphabetListTouched: Boolean,
    textSize: TextUnit,
    itemHeight: Int,
    onLetterTouched: (Char, Offset) -> Unit,
    onAlphabetListTouchEnded: () -> Unit
) {
    val density = LocalDensity.current
    val textStyle = androidx.compose.ui.text.TextStyle(fontSize = textSize, color = Color.White)

    Column(
        modifier = modifier
            .wrapContentHeight()
            .padding(end = 8.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, _ ->
                        val touchY = change.position.y
                        val index = (touchY /  itemHeight)
                            .coerceAtLeast(0f)
                            .coerceAtMost(alphabetList.size - 1f)
                            .toInt()
                        val letter = alphabetList[index]
                        onLetterTouched(letter, change.position)
                    },
                    onDragStart = {
                        val touchY = it.y
                        val index = (touchY / itemHeight)
                            .coerceAtLeast(0f)
                            .coerceAtMost(alphabetList.size - 1f)
                            .toInt()
                        val letter = alphabetList[index]
                        onLetterTouched(letter, it)
                    },
                    onDragEnd = {
                        onAlphabetListTouchEnded()
                    }
                )
            },
        horizontalAlignment = Alignment.End // Align to the end (right)
    ) {
        alphabetList.forEachIndexed { index, letter ->
            val isSelected = letter == selectedLetter
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

            // Calculate the horizontal offset for centering
            val offsetX = if (isAlphabetListTouched) {
                val letterY =  (index * itemHeight + itemHeight / 2f)
                val distanceToTouch = abs(touchPoint.y - letterY)

                // Max shift distance and easing
                val maxOffsetX = with(density) { 80.dp.toPx() } // Reduced for less extreme shifting
                val listHeight = alphabetList.size * itemHeight
                val normalizedDistance =
                    (distanceToTouch / (listHeight * 0.5f)).coerceAtMost(1f)
                val easingFactor = easeInOutCubic(normalizedDistance)
                val currentOffsetX = (touchPoint.x - maxOffsetX)  * (1f - easingFactor)
                currentOffsetX
            } else {
                0f // No offset when not touched
            }

            Text(
                text = letter.toString(),
                modifier = Modifier
                    .height(with(density) { itemHeight.toDp()})
                    .offset(x = with(density) { offsetX.toDp() })
                    .graphicsLayer {
                        alpha = if (isAlphabetListTouched) {
                            if (isSelected) 1f else 0.6f
                        } else {
                            1f
                        }
                    },
                style = textStyle,
                fontWeight = fontWeight,
            )
        }
    }
}

// Easing function for smoother animation (optional)
private fun easeInOutCubic(x: Float): Float {
    return if (x < 0.5) {
        4 * x * x * x
    } else {
        1 - ((-2 * x + 2).pow(3) / 2)
    }
}

@Composable
fun SelectedLetterOverlay(modifier: Modifier, letter: Char, touchPoint: Offset, topOffset: Float) {
        val density = LocalDensity.current
        val overlaySize = 48.dp
        val xOffset  = with(density) { 100.dp.toPx() }
        val overlaySizePx = with(density) { overlaySize.toPx() }

        // Detect touch gestures on the entire screen
        Box(
            modifier = modifier
                .fillMaxHeight()
        ) {
            // Calculate offset based on touchPoint
            val offsetX = touchPoint.x - xOffset- overlaySizePx / 2
            val offsetY = topOffset + touchPoint.y - overlaySizePx / 2

            // Only show the overlay if a touch is detected
            if (touchPoint != Offset.Zero) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .clip(CircleShape)
                        .size(overlaySize)
                        .background(Color.Blue)
                ) {
                    Text(
                        text = letter.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
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
    val icon: Drawable,
    val uniqueId: String = UUID.randomUUID().toString()
)