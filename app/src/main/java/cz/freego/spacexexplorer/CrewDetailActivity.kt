package cz.freego.spacexexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.databinding.ActivityCrewDetailBinding
import cz.freego.spacexexplorer.local.LocalRepository
import cz.freego.spacexexplorer.local.LocalViewModel
import cz.freego.spacexexplorer.local.LocalViewModelFactory
import cz.freego.spacexexplorer.local.db.AppDatabase
import cz.freego.spacexexplorer.remote.data.response.Crew

class CrewDetailActivity : AppCompatActivity() {

    lateinit var localViewModel: LocalViewModel
    private lateinit var binding: ActivityCrewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crewJson = intent.getStringExtra("crew")
        val moshi = Utils.getMoshi()
        val crew = moshi.adapter(Crew::class.java).fromJson(crewJson)

        binding = ActivityCrewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        crew?.let {
            binding.toolbarLayout.title = it.name
            val contentText = "Agency: ${it.agency}\n\nStatus: ${it.status}\n\n${it.wikipedia}"
            binding.includedContent.contentText.text = contentText
            Glide.with(this).load(it.image).into(binding.headerImage)
        }

        val db = AppDatabase(this)
        val localRepository = LocalRepository(db)
        localViewModel = ViewModelProvider(this,
            LocalViewModelFactory(localRepository)
        ).get(LocalViewModel::class.java)

        localViewModel.favoriteCrewExists.observe(this, {
            binding.fab.setImageResource(if (it) R.drawable.ic_star_24dp else R.drawable.ic_star_border_24dp)
            binding.fab.tag = it
        })
        crew?.let { localViewModel.favoriteCrewExists(it.id) }

        binding.fab.setOnClickListener { view ->
            if (view.tag as Boolean) {
                localViewModel.deleteFavoriteCrew(crew!!)
                binding.fab.setImageResource(R.drawable.ic_star_border_24dp)
                binding.fab.tag = false
            } else {
                localViewModel.addFavoriteCrew(crew!!)
                binding.fab.setImageResource(R.drawable.ic_star_24dp)
                binding.fab.tag = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}