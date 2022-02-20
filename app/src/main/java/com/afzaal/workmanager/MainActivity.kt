package com.afzaal.workmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.afzaal.workmanager.databinding.ActivityMainBinding
import com.afzaal.workmanager.utils.KEY_IMAGE_URI

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: BlurViewModel by viewModels { BlurViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }

        binding.seeFileButton.setOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        binding.cancelButton.setOnClickListener { viewModel.cancelWork() }
        viewModel.outputWorkInfos.observe(this, workInfosObserver())
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // If there is an output file show "See File" button
                if (!outputImageUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputImageUri as String)
                    binding.seeFileButton.visibility = View.VISIBLE
                }
            }
            else {
                showWorkInProgress()
            }
        }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }

    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
            viewModel.cancelWork()
        }
    }
}