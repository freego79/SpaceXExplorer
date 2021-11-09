package cz.freego.spacexexplorer.watchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.wear.widget.WearableLinearLayoutManager
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.RemoteRepository
import cz.freego.spacexexplorer.remote.RemoteViewModel
import cz.freego.spacexexplorer.remote.RemoteViewModelFactory
import cz.freego.spacexexplorer.remote.data.response.Crew
import kotlinx.android.synthetic.main.activity_watch.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WatchActivity : AppCompatActivity(), OnItemClickListener  {

    lateinit var remoteViewModel: RemoteViewModel
    lateinit var crewAdapter: CrewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)
        recycler_launcher_view.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager =
                WearableLinearLayoutManager(this@WatchActivity,
                    CustomScrollingLayoutCallback())
            requestFocus()
        }
        setupViewModel()
        setupList()
        setupView("")
    }

    private fun setupView(text: String) {
        lifecycleScope.launch {
            remoteViewModel.getCrews(text).collect {
                crewAdapter.submitData(it)
            }
        }
    }

    private fun setupList() {
        crewAdapter = CrewAdapter(this)
        recycler_launcher_view.apply {
            adapter = crewAdapter
        }
    }

    private fun setupViewModel() {
        val apiService = ApiService.getInstance(this)
        val remoteRepository = RemoteRepository(apiService)
        remoteViewModel = ViewModelProvider(this,
            RemoteViewModelFactory(remoteRepository)
        ).get(RemoteViewModel::class.java)
        remoteViewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onItemClicked(crew: Crew) {
            val intent = Intent(this, CrewDetailActivity::class.java)
            val moshi = Utils.getMoshi()
            val crewJson = moshi.adapter(Crew::class.java).toJson(crew)
            intent.putExtra("crew", crewJson)
            startActivity(intent)
    }

}