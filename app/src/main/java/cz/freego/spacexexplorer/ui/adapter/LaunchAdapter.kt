package cz.freego.spacexexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.ContextExtensions.getDateTime
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.databinding.ItemLaunchBinding
import cz.freego.spacexexplorer.remote.data.response.Launch

class LaunchAdapter(val launchClickListener: OnLaunchClickListener) :
    PagingDataAdapter<Launch, LaunchViewHolder>(object : DiffUtil.ItemCallback<Launch>() {

        override fun areItemsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {
        val binding = ItemLaunchBinding.inflate(LayoutInflater.from(parent.context))
        return LaunchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        val launch = getItem(position)
        launch?.let {l ->
            holder.root.setOnClickListener {
                launchClickListener.onLaunchClicked(l)
            }
            holder.textView.text = "${l.name}\n${getDateTime(l.date_unix)}"
            if (l.links?.patch?.small != null) {
                Glide.with(holder.itemView.context).load(l.links?.patch?.small).circleCrop()
                    .into(holder.imageView)
            } else {
                holder.imageView.setImageResource(R.drawable.rocket_clipart)
            }
        }
   }
}

class LaunchViewHolder(binding: ItemLaunchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val root: View = binding.root
    val imageView: ImageView = binding.imageViewItem
    val textView: TextView = binding.textViewItem
}

interface OnLaunchClickListener{
    fun onLaunchClicked(launch: Launch)
}
