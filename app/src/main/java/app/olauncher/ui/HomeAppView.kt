
package app.olauncher.ui

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import android.content.Context
import android.util.AttributeSet
import androidx.compose.ui.res.stringResource
import app.olauncher.R

//class HomeAppView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : FrameLayout(context, attrs, defStyleAttr) {
//
//    private val composeView = ComposeView(context).also { addView(it) }
//    var onClick: () -> Unit = {}
//
//    init {
//        composeView.setContent {
//            var text: String = stringResource(id = R.string.app)
//            HomeAppComposable(text = text, onClick = onClick)
//        }
//
//        context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.HomeAppView,
//            0, 0
//        ).apply {
//
//            try {
//                text = getString(R.styleable.HomeAppView_text) ?: stringResource(id = R.string.app)
//            } finally {
//                recycle()
//            }
//        }
//
//    }




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
//}
