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
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.databinding.ItemUpcomingLaunchBinding
import cz.freego.spacexexplorer.remote.data.response.Launch

class UpcomingLaunchesAdapter(val onLaunchItemClickListener: OnLaunchItemClickListener) :
    ListAdapter<Launch, UpcomingLaunchViewHolder>(object : DiffUtil.ItemCallback<Launch>() {

        override fun areItemsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingLaunchViewHolder {
        val binding = ItemUpcomingLaunchBinding.inflate(LayoutInflater.from(parent.context))
        return UpcomingLaunchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingLaunchViewHolder, position: Int) {
        val launch = getItem(position)
        launch?.let { l ->
            holder.root.setOnClickListener {
                onLaunchItemClickListener.onLaunchItemClicked(l)
            }
            holder.textView.text = l.name
            if (l.links?.patch?.small != null) {
                Glide.with(holder.itemView.context).load(l.links?.patch?.small).circleCrop()
                    .into(holder.imageView)
            } else {
                holder.imageView.setImageResource(R.drawable.rocket_clipart)
            }
        }
   }
}

class UpcomingLaunchViewHolder(binding: ItemUpcomingLaunchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val root: View = binding.root
    val imageView: ImageView = binding.imageViewLaunch
    val textView: TextView = binding.launchName
}

interface OnLaunchItemClickListener{
    fun onLaunchItemClicked(launch: Launch)
}
