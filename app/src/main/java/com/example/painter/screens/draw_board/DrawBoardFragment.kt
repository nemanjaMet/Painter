package com.example.painter.screens.draw_board

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.painter.R
import com.example.painter.constants.Constants
import com.example.painter.databinding.ScreenDrawBoardBinding
import com.google.android.material.slider.Slider
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class DrawBoardFragment : Fragment() {

    private var _binding: ScreenDrawBoardBinding? = null
    private val binding get() = _binding
    private val viewModel: DrawBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenDrawBoardBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
        setViewModelObservers()
        initSlider()
    }

    private fun setViewModelObservers() {
        viewModel.penColorLiveData.observe(viewLifecycleOwner, {
            binding?.viewDrawBoard?.setPenColor(it)
        })

        viewModel.penSizePercentLiveData.observe(viewLifecycleOwner, {
            binding?.viewDrawBoard?.setPenSize(it)
        })
    }

    private fun setOnClickListeners() {
//        binding?.btnShowHideBoardMenu?.setOnClickListener {
//            onShowHideBoardMenuClick()
//        }

        binding?.btnSelectPen?.setOnClickListener {
            onSelectPenClick()
        }

        binding?.btnSelectBrush?.setOnClickListener {
            onSelectBrushClick()
        }

        binding?.btnSelectRubber?.setOnClickListener {
            onSelectRubberClick()
        }

        binding?.btnColorPicker?.setOnClickListener {
            onColorPickerClick()
        }

        binding?.btnPenSize?.setOnClickListener {

            if (it.tag == true)
                hidePenSizeSlider()
            else
                showPenSizeSlider()

        }
    }

    private fun onShowHideBoardMenuClick() {

    }

    private fun onSelectPenClick() {
        binding?.viewDrawBoard?.selectPen()
    }

    private fun onSelectBrushClick() {
        binding?.viewDrawBoard?.selectBrush()
    }

    private fun onSelectRubberClick() {
        binding?.viewDrawBoard?.selectRubber()
    }

    private fun onColorPickerClick() {
        openColorPicker()
    }

    private fun openColorPicker() {
        val currentColor = binding?.viewDrawBoard?.getPenColor() ?: Constants.DEFAULT_PEN_COLOR

        val colorPickerDialog = ColorPickerDialog.Builder(requireContext())
            .setTitle("Color Picker")
            //.setPreferenceName("MyColorPickerDialog")
            .setPositiveButton(getString(R.string.confirm_button),
                ColorEnvelopeListener { envelope, fromUser ->
                    envelope?.color?.let {
                        viewModel.setPenColor(it)
                    }
                }) // setLayoutColor(envelope)
            .setNegativeButton(
                getString(R.string.cancel_button)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(false) // the default value is true.
            .attachBrightnessSlideBar(true) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.

        colorPickerDialog.colorPickerView.setInitialColor(currentColor)

        colorPickerDialog.show()

    }

    // animacija za prikaz slider-a
    private fun showPenSizeSlider() {
        val slider = binding?.sliderPenSize

        slider?.let { s ->

            s.visibility = View.VISIBLE

            binding?.btnPenSize?.tag = true

            val translateAnimation = ObjectAnimator.ofFloat(s, "translationX", - s.width.toFloat(), 0f)
            translateAnimation.duration = resources.getInteger(R.integer.slider_animation_duration).toLong()

            translateAnimation.start()
        }
    }

    // animacija za sakrivanje slider-a
    private fun hidePenSizeSlider() {
        val slider = binding?.sliderPenSize

        slider?.let { s ->

            binding?.btnPenSize?.tag = false

            val translateAnimation = ObjectAnimator.ofFloat(s, "translationX", 0f, - s.width.toFloat())
            translateAnimation.duration = resources.getInteger(R.integer.slider_animation_duration).toLong()

            translateAnimation.start()
        }
    }

    // setujemo inicijalna podesavanja za slider
    private fun initSlider() {
        binding?.sliderPenSize?.value = Constants.DEFAULT_PEN_SIZE_PERCENT

        binding?.sliderPenSize?.doOnPreDraw {
            // setujemo translaciju zbog animacije
            it.translationX = - it.width.toFloat()
        }

        binding?.sliderPenSize?.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                viewModel.setPenSizePercent(value)
            }
        })
    }

//    private fun showColorPalette() {
//
//    }
//
//    private fun hideColorPalette() {
//
//    }

}