package cz.freego.spacexexplorer.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cz.freego.spacexexplorer.LaunchDetailActivity
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.databinding.FragmentLaunchesBinding
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.RemoteRepository
import cz.freego.spacexexplorer.remote.RemoteViewModel
import cz.freego.spacexexplorer.remote.RemoteViewModelFactory
import cz.freego.spacexexplorer.remote.data.response.Launch
import cz.freego.spacexexplorer.ui.adapter.LaunchAdapter
import cz.freego.spacexexplorer.ui.adapter.OnLaunchClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LaunchesFragment : Fragment(), OnLaunchClickListener {

    lateinit var remoteViewModel: RemoteViewModel
    lateinit var launchAdapter: LaunchAdapter
    private var _binding: FragmentLaunchesBinding? = null

    private val binding get() = _binding!!

    private var asc: Boolean = true
    private var upcoming: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //menu.clear()
        inflater.inflate(R.menu.menu_launches, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_asc) {
            asc = true
            setupView(upcoming, asc)
        }
        if (id == R.id.action_desc) {
            asc = false
            setupView(upcoming, asc)
        }
        if (id == R.id.action_upcoming) {
            upcoming = true
            setupView(upcoming, asc)
        }
        if (id == R.id.action_previous) {
            upcoming = false
            setupView(upcoming, asc)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaunchesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupList()
        setupView(upcoming, asc)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView(upcoming: Boolean, asc: Boolean) {
        lifecycleScope.launch {
            remoteViewModel.getLaunches(upcoming, asc).collect {
                launchAdapter.submitData(it)
            }
        }
    }

    private fun setupList() {
        launchAdapter = LaunchAdapter(this)
        val recyclerView = binding.recyclerviewLaunches
        recyclerView.apply {
            adapter = launchAdapter
        }
    }

    private fun setupViewModel() {
        val apiService = ApiService.getInstance(requireContext())
        val remoteRepository = RemoteRepository(apiService)
        remoteViewModel = ViewModelProvider(this,
            RemoteViewModelFactory(remoteRepository)
        ).get(RemoteViewModel::class.java)
        remoteViewModel.errorMessage.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onLaunchClicked(launch: Launch) {
        activity?.let{
            val intent = Intent (it, LaunchDetailActivity::class.java)
            val moshi = Utils.getMoshi()
            val crewJson = moshi.adapter(Launch::class.java).toJson(launch)
            intent.putExtra("launch", crewJson)
            it.startActivity(intent)
        }
    }

}