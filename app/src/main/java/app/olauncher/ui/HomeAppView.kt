
package app.olauncher.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import app.olauncher.R
import app.olauncher.databinding.ViewHomeAppBinding

class HomeAppView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewHomeAppBinding = ViewHomeAppBinding.inflate(LayoutInflater.from(context), this, true)
    val textView: TextView = binding.homeAppText

    var onClickListener: OnClickListener? = null
        set(value) {
            field = value
            textView.setOnClickListener(value)
        }

    var onLongClickListener: OnLongClickListener? = null
        set(value) {
            field = value
            textView.setOnLongClickListener(value)
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HomeAppView,
            0, 0
        ).apply {

            try {
                // Customize view based on attributes here if needed
            } finally {
                recycle()
            }
        }
    }
}
