package app.olauncher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.olauncher.databinding.AdapterLetterIndexBinding

class LetterIndexAdapter(private val onLetterClick: (Char) -> Unit) : RecyclerView.Adapter<LetterIndexAdapter.ViewHolder>() {

    private val letters = ('A'..'Z').toList()

    class ViewHolder(val binding: AdapterLetterIndexBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterLetterIndexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val letter = letters[position]
        holder.binding.letterTextView.text = letter.toString()
        holder.binding.letterTextView.setOnClickListener {
            onLetterClick(letter)
        }
    }

    override fun getItemCount(): Int = letters.size
}
