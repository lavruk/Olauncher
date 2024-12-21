package app.olauncher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import app.olauncher.R
import androidx.recyclerview.widget.RecyclerView
import app.olauncher.databinding.AdapterLetterIndexBinding

class LetterIndexAdapter(private val onLetterScroll: (Char) -> Unit) : RecyclerView.Adapter<LetterIndexAdapter.ViewHolder>() {

    private val letters = ('A'..'Z').toList()

    class ViewHolder(val binding: AdapterLetterIndexBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterLetterIndexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val letter = letters[position]
        holder.binding.letterTextView.text = letter.toString()
        val scaleDownAnim = android.view.animation.AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_down)
        val scaleUpAnim = android.view.animation.AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_up)
        holder.binding.letterTextView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> v.startAnimation(scaleDownAnim)
                android.view.MotionEvent.ACTION_UP -> {
                    v.startAnimation(scaleUpAnim)
                    onLetterScroll(letter)
                }
            }
            true
        }
    }

    override fun getItemCount(): Int = letters.size
}
