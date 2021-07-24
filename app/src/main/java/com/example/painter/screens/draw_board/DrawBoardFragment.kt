package com.example.painter.screens.draw_board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.painter.databinding.ScreenDrawBoardBinding

class DrawBoardFragment : Fragment() {

    private var _binding: ScreenDrawBoardBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenDrawBoardBinding.inflate(inflater, container, false)
        return _binding?.root
    }




}