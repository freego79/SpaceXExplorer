package cz.freego.spacexexplorer.ui.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cz.freego.spacexexplorer.CrewDetailActivity
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.databinding.FragmentCrewsBinding
import cz.freego.spacexexplorer.remote.RemoteRepository
import cz.freego.spacexexplorer.remote.RemoteViewModelFactory
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.data.response.Crew
import cz.freego.spacexexplorer.ui.adapter.CrewAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import cz.freego.spacexexplorer.ContextExtensions.hideKeyboard
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.remote.RemoteViewModel
import cz.freego.spacexexplorer.ui.adapter.OnCrewClickListener

class CrewsFragment : Fragment(), OnCrewClickListener {

    lateinit var remoteViewModel: RemoteViewModel
    lateinit var crewAdapter: CrewAdapter
    lateinit var searchView: SearchView
    private var _binding: FragmentCrewsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_crews, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                setupView("")
                hideKeyboard()
                searchView.onActionViewCollapsed()
                return true
            }
        })
        val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = getString(R.string.action_search)
        val searchPlateView: View =
            searchView.findViewById(androidx.appcompat.R.id.search_plate)
        searchPlateView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { setupView(it) }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupList()
        setupView("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val recyclerView = binding.recyclerviewCrews
        recyclerView.apply {
            adapter = crewAdapter
        }
    }

    private fun setupViewModel() {
        val apiService = ApiService.getInstance(requireContext())
        val remoteRepository = RemoteRepository(apiService)
        remoteViewModel = ViewModelProvider(this,
            RemoteViewModelFactory(remoteRepository)).get(RemoteViewModel::class.java)
        remoteViewModel.errorMessage.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCrewClicked(crew: Crew) {
        activity?.let{
            val intent = Intent (it, CrewDetailActivity::class.java)
            val moshi = Utils.getMoshi()
            val crewJson = moshi.adapter(Crew::class.java).toJson(crew)
            intent.putExtra("crew", crewJson)
            it.startActivity(intent)
        }
    }

}