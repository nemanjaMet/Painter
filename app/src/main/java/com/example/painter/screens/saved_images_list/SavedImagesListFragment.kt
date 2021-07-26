package com.example.painter.screens.saved_images_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.painter.adapters.SavedImagesListAdapter
import com.example.painter.databinding.ScreenSavedImagesListBinding
import com.example.painter.shared_view_models.MainViewModel

class SavedImagesListFragment : Fragment() {

    private var _binding: ScreenSavedImagesListBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenSavedImagesListBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSavedImagesList()
    }

    // setovanje adaptera i podataka za prikaz liste
    private fun setSavedImagesList() {

        binding?.rvSavedImagesList?.let {
            val layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            it.layoutManager = layoutManager

            it.adapter = SavedImagesListAdapter(requireContext(), mainViewModel.getSavedDrawings())
        }

    }

}