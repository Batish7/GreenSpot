package com.batish.android.greenspot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.batish.android.greenspot.databinding.FragmentPlantsDetailBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.Date

class PlantsDetailFragment : Fragment() {

    private var _binding: FragmentPlantsDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: PlantsDetailFragmentArgs by navArgs()
    private val plantDetailViewModel: PlantDetailViewModel by viewModels {
        PlantDetailViewModelFactory(args.plantId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantsDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            plantsTitle.doOnTextChanged { text, _, _, _ ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(title = text.toString())
                }
            }
            plantsPlace.doOnTextChanged { text, _, _, _ ->
                plantDetailViewModel.updatePlant { oldPlant ->
                    oldPlant.copy(place = text.toString())
                }}

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    plantDetailViewModel.plant.collect { plant ->
                        plant?.let { updateUi(it) }
                    }
                }
            }
            setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY_DATE
            ) { _, bundle ->
                val newDate =
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                plantDetailViewModel.updatePlant { it.copy(date = newDate) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun updateUi(plant: Plant) {
        binding.apply {
            if (plantsTitle.text.toString() != plant.title) {
                plantsTitle.setText(plant.title)
            }
            plantsDate.text = plant.date.toString()
            plantsDate.setOnClickListener {
                findNavController().navigate(
                    PlantsDetailFragmentDirections.selectDate(plant.date)
                )
            }
            plantsPlace.text = plant.place
        }
    }
}