package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentAsteroidListBinding

class AsteroidListFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)


        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    private val asteroidAdapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { asteroid ->
        viewModel.onAsteroidClicked(asteroid)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentAsteroidListBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = asteroidAdapter

        setHasOptionsMenu(true)

        viewModel.goToDetailAsteroid.observe(viewLifecycleOwner, Observer { asteroid ->
            if (asteroid != null){
                this.findNavController().navigate(AsteroidListFragmentDirections.actionShowDetail(asteroid))
                viewModel.onAsteroidNavigated()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroid  ->
            asteroid.apply { asteroidAdapter.submitList(this) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
