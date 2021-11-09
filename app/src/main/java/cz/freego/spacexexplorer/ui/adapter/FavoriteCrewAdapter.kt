package cz.freego.spacexexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.databinding.ItemFavoriteCrewBinding
import cz.freego.spacexexplorer.remote.data.response.Crew

class FavoriteCrewAdapter(val favoriteItemClickListener: OnFavoriteItemClickListener) :
    ListAdapter<Crew, FavoriteCrewViewHolder>(object : DiffUtil.ItemCallback<Crew>() {

        override fun areItemsTheSame(oldItem: Crew, newItem: Crew): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Crew, newItem: Crew): Boolean =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCrewViewHolder {
        val binding = ItemFavoriteCrewBinding.inflate(LayoutInflater.from(parent.context))
        return FavoriteCrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteCrewViewHolder, position: Int) {
        val crew = getItem(position)
        crew?.let { c ->
            holder.root.setOnClickListener {
                favoriteItemClickListener.onFavoriteItemClicked(c)
            }
            holder.textView.text = c.name
            Glide.with(holder.itemView.context).load(c.image).circleCrop().into(holder.imageView)
        }
   }
}

class FavoriteCrewViewHolder(binding: ItemFavoriteCrewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val root: View = binding.root
    val imageView: ImageView = binding.imageViewPhoto
    val textView: TextView = binding.crewName
}

interface OnFavoriteItemClickListener{
    fun onFavoriteItemClicked(crew: Crew)
}
