package com.example.painter.screens.saved_images_list

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.painter.R
import com.example.painter.adapters.SavedImagesListAdapter
import com.example.painter.constants.Constants
import com.example.painter.constants.ProgressMode
import com.example.painter.databinding.ScreenSavedImagesListBinding
import com.example.painter.helpers.HelperManager
import com.example.painter.helpers.PermissionManager
import com.example.painter.shared_view_models.MainViewModel
import com.google.android.material.snackbar.Snackbar


class SavedImagesListFragment : Fragment(), SavedImagesListAdapter.SavedImagesListAdapterInterface {

    private var _binding: ScreenSavedImagesListBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by activityViewModels()
    private var exportImagePosition = -1

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
        setViewModelObservers()
    }

    private fun setViewModelObservers() {
        mainViewModel.listOfDrawings.observe(viewLifecycleOwner, { savedDrawings ->

            savedDrawings?.let {
                (binding?.rvSavedImagesList?.adapter as? SavedImagesListAdapter?)?.refreshList(it.asReversed())
            }

        })

        mainViewModel.exportImageMode.observe(viewLifecycleOwner, { mode ->

            when (mode) {
                ProgressMode.IN_PROGRESS -> {
                    exportImagePosition = -1
                    showLoading()
                }

                ProgressMode.FINISHED -> {
                    //val uriString = mode.value
                    hideLoading()
                    mainViewModel.resetExportImageMode()
                    showImageExportedSnackBar()
                }

                else -> {

                }
            }

        })
    }

    // setovanje adaptera i podataka za prikaz liste
    private fun setSavedImagesList() {

        binding?.rvSavedImagesList?.let {

            if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                it.layoutManager = layoutManager
            } else {
                val layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                it.layoutManager = layoutManager
            }

            it.adapter = SavedImagesListAdapter(requireContext(), mutableListOf(), this)

            it.setHasFixedSize(true)
            it.setItemViewCacheSize(20)

        }

    }

    override fun onExportImageClick(position: Int) {
        // ako je novi api odmah eksportuj sliku
        if (HelperManager.isNewApi())
            mainViewModel.exportImage(requireContext(), position)
        else { // ako je stari api mora da se zatrazi permsija prvo
            exportImagePosition = position
            requestPermission()
        }

    }

    private fun requestPermission() {
        storagePermission.launch(Constants.Permission.WriteExternalStorage.PERMISSION)
    }


    private val storagePermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                with(binding?.root) {
                    when {
                        granted -> {
                            //Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show()
                            mainViewModel.exportImage(requireContext(), exportImagePosition)
                        }
                        shouldShowRequestPermissionRationale(Constants.Permission.WriteExternalStorage.PERMISSION) -> {
                            //this option is available starting in API 23
                            //Toast.makeText(requireContext(), "Permission denied, show more info!", Toast.LENGTH_SHORT).show()

                            PermissionManager.showPermissionExplanationDialog(this@SavedImagesListFragment, Constants.Permission.WriteExternalStorage.PERMISSION, Constants.Permission.WriteExternalStorage.REQUEST_CODE, getString(R.string.permission_write_external_storage)) { requestPermission() }
                        }
                        else -> {
                            //Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()

                            PermissionManager.showPermissionDeniedDialog(this@SavedImagesListFragment, Constants.Permission.WriteExternalStorage.PERMISSION, Constants.Permission.WriteExternalStorage.REQUEST_CODE, getString(R.string.permission_write_external_storage_denied), startForResult)
                        }
                    }
                }
            }

    private var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
        }
    }

    private fun showLoading() {
        binding?.clLoadingLayout?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding?.clLoadingLayout?.visibility = View.GONE
    }

    private fun showImageExportedSnackBar() {
        binding?.rvSavedImagesList?.let {
            val snackBar = Snackbar.make(it, getString(R.string.image_saved), Snackbar.LENGTH_SHORT)
            snackBar.setAction(getString(R.string.snackBar_open_btn)) {
                openFileLocation()
            }
            snackBar.show()
        }

    }

    private fun openFileLocation() {
        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //val uri: Uri = Uri.parse(uriString)
       // intent.setDataAndType(uri, "image/png")
        //startActivity(Intent.createChooser(intent, "Open folder"))

        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }


}