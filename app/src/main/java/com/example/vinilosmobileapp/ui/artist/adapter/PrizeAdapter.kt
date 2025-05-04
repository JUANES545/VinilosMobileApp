package com.example.vinilosmobileapp.ui.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemPrizeBinding
import com.example.vinilosmobileapp.model.PerformerPrize

class PrizeAdapter(
    private var prizes: List<PerformerPrize>
) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    inner class PrizeViewHolder(val binding: ItemPrizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prize: PerformerPrize) {
            binding.tvPrizeName.text = prize.prizeName
            binding.tvOrganization.text = prize.organization
            binding.tvDateAwarded.text = prize.awardedDate.take(10)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val b = ItemPrizeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrizeViewHolder(b)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) =
        holder.bind(prizes[position])

    override fun getItemCount() = prizes.size

    fun updatePrizes(new: List<PerformerPrize>) {
        prizes = new
        notifyDataSetChanged()
    }
}
