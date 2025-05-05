package com.example.vinilosmobileapp.ui.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemPrizeBinding

class PrizeInputAdapter(private var items: MutableList<Pair<String, String?>>) :
    RecyclerView.Adapter<PrizeInputAdapter.VH>() {

    inner class VH(val binding: ItemPrizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prize: Pair<String, String?>) {
            binding.tvPrizeName.text = prize.first
            // binding.tvOrganization.text = prize.second ?: ""
            binding.tvDateAwarded.text = prize.second ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPrizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun addPrize(name: String, date: String?) {
        items.add(name to date)
        notifyItemInserted(items.lastIndex)
    }
}
