package cz.freego.spacexexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.databinding.ActivityLaunchDetailBinding
import cz.freego.spacexexplorer.remote.data.response.Launch

class LaunchDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchJson = intent.getStringExtra("launch")
        val moshi = Utils.getMoshi()
        val launch = moshi.adapter(Launch::class.java).fromJson(launchJson)

        binding = ActivityLaunchDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        launch?.let {
            binding.toolbarLayout.title = it.name
            val contentText = "Flight Number: ${it.flight_number}" +
                    "\n\nUpcoming: ${if (it.upcoming) "Yes" else "No"}" +
                    "\n\nStatus: ${if (it.success == null) "Unknown" else if (it.success == true) "Success" else "Failed"}" +
                    "\n\nWikipedia:\n${if (it.links?.wikipedia.isNullOrEmpty()) "-" else it.links?.wikipedia}" +
                    "\n\nWebcast:\n${if (it.links?.webcast.isNullOrEmpty()) "-" else it.links?.webcast}"
            binding.includedContent.contentText.text = contentText
            if (it.links?.patch?.large != null) {
                Glide.with(this).load(it.links?.patch?.large).into(binding.headerImage)
            } else {
                binding.headerImage.setImageResource(R.drawable.rocket_clipart)
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}