package cz.freego.spacexexplorer.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.CrewDetailActivity
import cz.freego.spacexexplorer.LaunchDetailActivity
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.databinding.FragmentHomeBinding
import cz.freego.spacexexplorer.local.LocalRepository
import cz.freego.spacexexplorer.local.LocalViewModel
import cz.freego.spacexexplorer.local.LocalViewModelFactory
import cz.freego.spacexexplorer.local.db.AppDatabase
import cz.freego.spacexexplorer.remote.RemoteRepository
import cz.freego.spacexexplorer.remote.RemoteViewModelFactory
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.RemoteViewModel
import cz.freego.spacexexplorer.remote.data.response.Crew
import cz.freego.spacexexplorer.remote.data.response.Launch
import cz.freego.spacexexplorer.ui.adapter.FavoriteCrewAdapter
import cz.freego.spacexexplorer.ui.adapter.OnFavoriteItemClickListener
import cz.freego.spacexexplorer.ui.adapter.OnLaunchItemClickListener
import cz.freego.spacexexplorer.ui.adapter.UpcomingLaunchesAdapter

class HomeFragment : Fragment(), OnFavoriteItemClickListener, OnLaunchItemClickListener {

    lateinit var remoteViewModel: RemoteViewModel
    lateinit var localViewModel: LocalViewModel
    private var _binding: FragmentHomeBinding? = null

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var interval: Long = 1000L
    private lateinit var nextLaunch: Launch

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onDestroy() {
        if (this::handler.isInitialized and this::runnable.isInitialized) handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerViewFavCrew = binding.recyclerviewFavoriteCrews
        val adapterFavCrew = FavoriteCrewAdapter(this)
        recyclerViewFavCrew.adapter = adapterFavCrew

        val recyclerviewUpcomingLaunches = binding.recyclerviewUpcomingLaunches
        val adapterUpcomingLaunches = UpcomingLaunchesAdapter(this)
        recyclerviewUpcomingLaunches.adapter = adapterUpcomingLaunches

        val apiService = ApiService.getInstance(requireContext())
        val remoteRepository = RemoteRepository(apiService)
        remoteViewModel = ViewModelProvider(this,
            RemoteViewModelFactory(remoteRepository)).get(RemoteViewModel::class.java)
        remoteViewModel.upcomingLaunches.observe(viewLifecycleOwner, {
            adapterUpcomingLaunches.submitList(it)
            if (it.isNotEmpty()) setupNextLaunch(it.get(0))
        })
        remoteViewModel.latestLaunch.observe(viewLifecycleOwner, {
            setupLastLaunch(it)
        })

        val db = AppDatabase(requireContext())
        val localRepository = LocalRepository(db)
        localViewModel = ViewModelProvider(this,
            LocalViewModelFactory(localRepository)).get(LocalViewModel::class.java)
        localViewModel.favoriteCrewList.observe(viewLifecycleOwner, {
            adapterFavCrew.submitList(it)
        })

        remoteViewModel.getUpcomingLaunches()
        remoteViewModel.getLatestLaunch()
        localViewModel.getAllFavoriteCrew()

        return root
    }

    override fun onResume() {
        super.onResume()
        localViewModel.getAllFavoriteCrew()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFavoriteItemClicked(crew: Crew) {
        activity?.let{
            val intent = Intent (it, CrewDetailActivity::class.java)
            val moshi = Utils.getMoshi()
            val crewJson = moshi.adapter(Crew::class.java).toJson(crew)
            intent.putExtra("crew", crewJson)
            it.startActivity(intent)
        }
    }

    override fun onLaunchItemClicked(launch: Launch) {
        activity?.let{
            val intent = Intent (it, LaunchDetailActivity::class.java)
            val moshi = Utils.getMoshi()
            val launchJson = moshi.adapter(Launch::class.java).toJson(launch)
            intent.putExtra("launch", launchJson)
            it.startActivity(intent)
        }
    }

    private fun setupNextLaunch(nextLaunch: Launch) {
        binding.upcomingLaucnTemplate.apply {
            templateRoot.visibility = View.VISIBLE
            Glide.with(requireContext()).load(nextLaunch.links?.patch?.small).circleCrop().into(launchIcon)
            name.text = nextLaunch.name
            flightNumber.text = "Flight Number: ${nextLaunch.flightNumber}"
            templateRoot.setOnClickListener({ onLaunchItemClicked(nextLaunch) })
        }
        countdownNextLaunch(nextLaunch)
    }

    private fun setupLastLaunch(lastLaunch: Launch) {
        binding.latestLaunchTemplate.apply {
            templateRoot.visibility = View.VISIBLE
            Glide.with(requireContext()).load(lastLaunch.links?.patch?.small).circleCrop().into(launchIcon)
            name.text = lastLaunch.name
            flightNumber.text = "Flight Number: ${lastLaunch.flightNumber}"
            successOrTimeCount.text = "Status: " + if (lastLaunch.success == null) "Unknown"
                                                    else if (lastLaunch.success == true) "Success"
                                                    else "Failed"
            templateRoot.setOnClickListener({ onLaunchItemClicked(lastLaunch) })
        }
    }

    private fun countdownNextLaunch(nextLaunch: Launch) {
        this.nextLaunch = nextLaunch
        runnable = Runnable { // do some task on delay
            doCountdownTask(handler)
        }
        handler.post(runnable)
    }

    private fun doCountdownTask(handler: Handler) {
        if (_binding == null) return;
        val currentTimeSecs = System.currentTimeMillis() / 1000
        if (nextLaunch.dateUnix <= currentTimeSecs) {
            handler.removeCallbacks(runnable)
            binding.upcomingLaucnTemplate.successOrTimeCount.text = "Status: Launched..."
        } else {
            updateNextLaunchCountdown(currentTimeSecs)
            handler.postDelayed(runnable,interval)
        }
    }

    private fun updateNextLaunchCountdown(currentUnixTimeSec: Long) {
        if (_binding == null) return;
        val delta = nextLaunch.dateUnix - currentUnixTimeSec
        val _days = delta / 86400
        val _hours = (delta - _days * 86400) / 3600
        val _mins = (delta - _days * 86400 - _hours * 3600) / 60
        val _secs = delta - _days * 86400 - _hours * 3600 - _mins * 60
        binding.upcomingLaucnTemplate.successOrTimeCount.text = "T -${_days}d ${_hours}h ${_mins}m ${_secs}s"
    }

}