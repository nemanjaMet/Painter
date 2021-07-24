package com.example.painter.screens.saved_images_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.painter.databinding.ScreenSavedImagesListBinding

class SavedImagesListFragment : Fragment() {

    private var _binding: ScreenSavedImagesListBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenSavedImagesListBinding.inflate(inflater, container, false)
        return _binding?.root
    }

}