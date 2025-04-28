package com.example.vinilosmobileapp.ui.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosmobileapp.databinding.ItemCommentBinding
import com.example.vinilosmobileapp.model.Comment

class CommentInputAdapter(private var comments: List<Comment>) :
    RecyclerView.Adapter<CommentInputAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentDescription.text = comment.description
            binding.commentAuthor.text = "- ${comment.collector?.name ?: "Anónimo"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }

    fun getComments(): List<Comment> = comments

    fun addComment(comment: Comment) {
        comments = comments + comment
        notifyItemInserted(comments.size - 1)
    }


}
