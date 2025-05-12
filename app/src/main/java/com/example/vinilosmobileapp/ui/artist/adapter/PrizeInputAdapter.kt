package com.example.vinilosmobileapp.ui.artist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemPrizeBinding
import com.example.vinilosmobileapp.model.PerformerPrize
import com.example.vinilosmobileapp.model.Prize

class PrizeInputAdapter(
    private var performerPrizes: MutableList<PerformerPrize>,
    private var prizes: List<Prize>
) : RecyclerView.Adapter<PrizeInputAdapter.VH>() {

    inner class VH(val binding: ItemPrizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(performerPrize: PerformerPrize) {
            // Find the corresponding Prize by matching IDs
            val prize = prizes.find { it.id == performerPrize.id }
            binding.tvOrganization.text = prize?.organization ?: "Organización desconocida"
            binding.tvPrizeName.text = prize?.name ?: "Nombre desconocido"
            binding.tvDateAwarded.text = performerPrize.premiationDate.take(10) // Format date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPrizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(performerPrizes[position])

    override fun getItemCount() = performerPrizes.size

    fun addPrize(performerPrize: PerformerPrize) {
        performerPrizes.add(performerPrize)
        notifyItemInserted(performerPrizes.lastIndex)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePrizes(newPrizes: List<Prize>) {
        prizes = newPrizes
        notifyDataSetChanged()
    }

    fun getSelectedPrizes(): List<PerformerPrize> {
        return performerPrizes
    }
}
