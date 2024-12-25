
package app.olauncher.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import android.content.Context
import android.util.AttributeSet
import androidx.compose.ui.res.stringResource
import app.olauncher.R

class HomeAppView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val composeView = ComposeView(context).also { addView(it) }
    var text: String = stringResource(id = R.string.app)
    var onClick: () -> Unit = {}

    init {
        composeView.setContent {
            HomeAppComposable(text = text, onClick = onClick)
        }

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HomeAppView,
            0, 0
        ).apply {

            try {
                text = getString(R.styleable.HomeAppView_text) ?: stringResource(id = R.string.app)
            } finally {
                recycle()
            }
        }

    }

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.olauncher.R

@Composable
fun HomeAppComposable(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}



//    init {
//        context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.HomeAppView,
//            0, 0
//        ).apply {
//
//            try {
//                // Customize view based on attributes here if needed
//            } finally {
//                recycle()
//            }
//        }
//    }
}
