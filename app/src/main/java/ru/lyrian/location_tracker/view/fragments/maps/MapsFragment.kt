package ru.lyrian.location_tracker.view.fragments.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.FragmentMapsBinding
import ru.lyrian.location_tracker.view.fragments.base.BaseFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.fragments.MapsViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

class MapsFragment : BaseFragment<FragmentMapsBinding>() {
    private val mapsViewModel: MapsViewModel by createSharedViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentMapsBinding.inflate(inflater, container, false)
        setNavigationParameters()

        return (this.binding as FragmentMapsBinding).root
    }

    override fun onStart() {
        super.onStart()

        this.mapsViewModel.signOutNavigationLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                val action = MapsFragmentDirections.actionMapsFragmentToSignInFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this.binding = null
    }

    private fun setNavigationParameters() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcvNavigationNestedHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        this.binding?.bnvMapsSwitcher?.setupWithNavController(navController)
        this.binding?.bnvMapsSwitcher?.setOnItemReselectedListener { /* Do nothing if already selected item clicked */ }
    }
}