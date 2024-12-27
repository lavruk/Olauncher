package app.olauncher.ui

import android.app.Application
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.olauncher.helper.getAppsList
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow

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
fun AppListScreen2(viewModel: AppListViewModel) {
    val appList by viewModel.appList
    val filteredAppList by viewModel.filteredAppList
    val selectedLetter by viewModel.selectedLetter
    val alphabetList by viewModel.alphabetList
    val touchPoint by viewModel.touchPoint
    val isAlphabetListTouched by viewModel.isAlphabetListTouched

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {

        // App List (LazyColumn)
        AppList(filteredAppList)

        // Alphabet List (Side Bar)
        AlphabetListSideBar(
            modifier = Modifier.align(Alignment.CenterEnd),
            alphabetList,
            selectedLetter,
            touchPoint,
            isAlphabetListTouched,
            screenHeight,
            onLetterTouched = { letter, y ->
                viewModel.onLetterTouched(letter, y)
            },
            onAlphabetListTouchEnded = { viewModel.onAlphabetListTouchEnded() }
        )

        // Overlay for selected letter (optional, based on your design)
        if (selectedLetter != null) {
            SelectedLetterOverlay(selectedLetter!!)
        }
    }
}

@Composable
fun AppList(appList: List<AppInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp)
    ) {
        items(appList) { app ->
            AppItem2(app)
        }
    }
}

@Composable
fun AppItem2(app: AppInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
    screenHeight: Dp,
    onLetterTouched: (Char, Offset) -> Unit,
    onAlphabetListTouchEnded: () -> Unit
) {
    val density = LocalDensity.current
    val textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, color = Color.White)
    val itemHeight = screenHeight / alphabetList.size
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val touchPointX = remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(end = 8.dp) // Add padding to the right side
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, _ ->
                        val touchY = change.position.y
                        touchPointX.value = change.position.x
                        val index = (touchY / with(density) { itemHeight.toPx() })
                            .coerceAtLeast(0f)
                            .coerceAtMost(alphabetList.size - 1f)
                            .toInt()
                        val letter = alphabetList[index]
                        onLetterTouched(letter, change.position)
                    },
                    onDragStart = {
                        val touchY = it.y
                        touchPointX.value = it.x
                        val index = (touchY / with(density) { itemHeight.toPx() })
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
                val letterY = with(density) { (index * itemHeight.toPx() + itemHeight.toPx() / 2f) }
                val distanceToTouch = abs(touchPoint.y - letterY)

                // Max shift distance and easing
                val maxOffsetX = with(density) { 80.dp.toPx() } // Reduced for less extreme shifting
                val normalizedDistance =
                    (distanceToTouch / (screenHeight.value * 0.75f)).coerceAtMost(1f) // Normalized to half screen height
                val easingFactor = easeInOutCubic(normalizedDistance)
                val currentOffsetX = (touchPoint.x - maxOffsetX)  * (1f - easingFactor)

                println("@@@ touch x: ${touchPoint.x}, currentOffsetX: $currentOffsetX, easingFactor: $easingFactor")


                // Determine shift direction based on touch position relative to letter
                if (touchPoint.y < letterY) {
                    currentOffsetX // Shift left
                } else {
                    currentOffsetX // Shift left
                }
            } else {
                0f // No offset when not touched
            }

            Text(
                text = letter.toString(),
                modifier = Modifier
                    .height(itemHeight)
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
fun SelectedLetterOverlay(letter: Char) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
    ) {
        Text(
            text = letter.toString(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}