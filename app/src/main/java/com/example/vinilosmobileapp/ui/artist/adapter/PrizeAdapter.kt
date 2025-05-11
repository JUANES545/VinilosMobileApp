package com.example.vinilosmobileapp.ui.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemPrizeBinding
import com.example.vinilosmobileapp.model.PerformerPrize
import com.example.vinilosmobileapp.model.Prize

class PrizeAdapter(
    private var performerPrizes: List<PerformerPrize>,
    private var prizes: List<Prize>
) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    inner class PrizeViewHolder(val binding: ItemPrizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(performerPrize: PerformerPrize) {
            // Find the corresponding Prize by checking if the PerformerPrize exists in the Prize's performerPrizes list
            val prize = prizes.find { prize ->
                prize.performerPrizes.any { it.id == performerPrize.id }
            }
            binding.tvPrizeName.text = prize?.name ?: "Premio desconocido"
            binding.tvOrganization.text = prize?.organization ?: "Organización desconocida"
            binding.tvDateAwarded.text = performerPrize.premiationDate.take(10) // Format date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val binding = ItemPrizeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        holder.bind(performerPrizes[position])
    }

    override fun getItemCount() = performerPrizes.size

    fun updatePrizes(newPerformerPrizes: List<PerformerPrize>, newPrizes: List<Prize>) {
        performerPrizes = newPerformerPrizes
        prizes = newPrizes
        notifyDataSetChanged()
    }
}
