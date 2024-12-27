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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.olauncher.R
import app.olauncher.helper.getAppsList
import kotlinx.coroutines.launch
import java.time.format.TextStyle

class AppListViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _appList = mutableStateOf<List<AppInfo>>(emptyList())
    val appList: State<List<AppInfo>> = _appList

    private val _filteredAppList = mutableStateOf<List<AppInfo>>(emptyList())
    val filteredAppList: State<List<AppInfo>> = _filteredAppList

    private val _selectedLetter = mutableStateOf<Char?>(null)
    val selectedLetter: State<Char?> = _selectedLetter

    private val _alphabetList = mutableStateOf<List<Char>>(('A'..'Z').toList() + '#')
    val alphabetList: State<List<Char>> = _alphabetList

    private val _touchPointY = mutableStateOf<Float>(0f)
    val touchPointY: State<Float> = _touchPointY

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

    fun onLetterTouched(letter: Char, y: Float) {
        _selectedLetter.value = letter
        _touchPointY.value = y
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
    val touchPointY by viewModel.touchPointY
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
            touchPointY,
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
    touchPointY: Float,
    isAlphabetListTouched: Boolean,
    screenHeight: Dp,
    onLetterTouched: (Char, Float) -> Unit,
    onAlphabetListTouchEnded: () -> Unit
) {
    val density = LocalDensity.current
    val textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.White) // Adjust style as needed

    // Calculate item height dynamically based on available screen height
    val itemHeight = screenHeight / alphabetList.size

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(end = 8.dp) // Add padding to the right side
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, _ ->
                        val touchY = change.position.y
                        val index =
                            (touchY / with(density) { itemHeight.toPx() }).coerceAtLeast(0f)
                                .coerceAtMost(alphabetList.size - 1f).toInt()
                        val letter = alphabetList[index]
                        onLetterTouched(letter, touchY)
                    },
                    onDragStart = {
                        val touchY = it.y
                        val index =
                            (touchY / with(density) { itemHeight.toPx() }).coerceAtLeast(0f)
                                .coerceAtMost(alphabetList.size - 1f).toInt()
                        val letter = alphabetList[index]
                        onLetterTouched(letter, touchY)
                    },
                    onDragEnd = {
                        onAlphabetListTouchEnded()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabetList.forEachIndexed { index, letter ->
            val isSelected = letter == selectedLetter
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

            // Shifting logic based on touch position
            val offsetY = if (isAlphabetListTouched) {
                val targetY = touchPointY - with(density) { (itemHeight / 2).toPx() }
                val letterY = with(density) { (index * itemHeight.toPx()) }
                lerp(
                    letterY,
                    targetY.coerceAtLeast(0f).coerceAtMost(with(density) { (screenHeight - itemHeight).toPx() }),
                    0.3f // Adjust the shifting factor (0.0f to 1.0f)
                ) - letterY
            } else {
                0f
            }

            Text(
                text = letter.toString(),
                modifier = Modifier
                    .height(itemHeight)
                    .offset(y = with(density) { offsetY.toDp() })
                    .graphicsLayer {
                        alpha = if (isAlphabetListTouched) {
                            if (isSelected) 1f else 0.4f
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