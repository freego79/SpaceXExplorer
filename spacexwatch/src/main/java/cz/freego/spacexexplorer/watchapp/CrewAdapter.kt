package cz.freego.spacexexplorer.watchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.databinding.ItemCrewBinding
import cz.freego.spacexexplorer.remote.data.response.Crew

class CrewAdapter(val itemClickListener: OnItemClickListener) :
    PagingDataAdapter<Crew, CrewViewHolder>(object : DiffUtil.ItemCallback<Crew>() {

        override fun areItemsTheSame(oldItem: Crew, newItem: Crew): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Crew, newItem: Crew): Boolean =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val binding = ItemCrewBinding.inflate(LayoutInflater.from(parent.context))
        return CrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        val crew = getItem(position)
        if (crew != null) {
            holder.root.setOnClickListener {
                itemClickListener.onItemClicked(crew)
            }
            holder.textView.text = crew.name
            Glide.with(holder.itemView.context).load(crew.image).circleCrop().into(holder.imageView)
        }
    }
}

class CrewViewHolder(binding: ItemCrewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val root: View = binding.root
    val imageView: ImageView = binding.imageViewItem
    val textView: TextView = binding.textViewItem
}

interface OnItemClickListener{
    fun onItemClicked(crew: Crew)
}
