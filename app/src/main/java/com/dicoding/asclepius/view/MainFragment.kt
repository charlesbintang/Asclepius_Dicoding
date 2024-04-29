package com.dicoding.asclepius.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.FragmentMainBinding
import com.dicoding.asclepius.helper.DateHelper
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    private val binding
        get() = _binding
            ?: throw IllegalStateException("Do not access binding before onCreateView or after onDestroyView")

    private var currentImageUri: Uri? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val uCrop = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputImage = input[0]
            val outputImage = input[1]

            val uCropProcess = UCrop.of(inputImage, outputImage)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(224, 224)

            return uCropProcess.getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return intent?.let { UCrop.getOutput(it) }!!
        }

    }

    private val cropTheImage = registerForActivityResult(uCrop) { uri ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            galleryButton.setOnClickListener {
                startGallery()
            }
            analyzeButton.setOnClickListener {
                currentImageUri?.let {
                    progressIndicator.visibility = View.VISIBLE
                    analyzeImage(it)
                } ?: run {
                    showToast(getString(R.string.empty_image_warning))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentImageUri != null) {
            binding.previewImageView.setImageURI(Uri.EMPTY)
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val outputImage = File(requireContext().filesDir, "outputImage_${DateHelper.getCurrentImageDate()}.jpg").toUri()
            val listUri = listOf(uri, outputImage)
            cropTheImage.launch(listUri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?) {
                    binding.progressIndicator.visibility = View.GONE
                    results?.let { it ->
                        if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                            println(it)
                            val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                            val displayResult = sortedCategories.joinToString("\n") {
                                "${it.label} " + NumberFormat.getPercentInstance().format(it.score)
                                    .trim()
                            }
                            moveToResult(uri, displayResult)
                        }
                    }
                }
            }
        )
        activity?.let { imageClassifierHelper.classifyStaticImage(uri, it.contentResolver) }
    }

    private fun moveToResult(uri: Uri, data: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
        intent.putExtra(ResultActivity.EXTRA_DISPLAY_TEXT, data)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}