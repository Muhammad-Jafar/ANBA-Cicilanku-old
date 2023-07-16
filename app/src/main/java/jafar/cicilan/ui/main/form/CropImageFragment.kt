package jafar.cicilan.ui.main.form

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.databinding.MainImageCropBinding
import jafar.cicilan.utils.Constanta.CROPPED_BITMAP
import jafar.cicilan.utils.Constanta.CROP_IMAGE_RESULT
import jafar.cicilan.utils.formatPNG
import jafar.cicilan.utils.showToast

class CropImageFragment : BaseFragment<MainImageCropBinding>(MainImageCropBinding::inflate) {
    private val args: CropImageFragmentArgs by navArgs()

    override fun renderView(savedInstanceState: Bundle?) {
        with(binding) {
            toolbar.apply {
                inflateMenu(R.menu.crop_option_menu)
                setNavigationOnClickListener {
                    showToast(getString(R.string.cancel_direction))
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener {
                    with(cropImageView) {
                        if (it.itemId == R.id.rotateButton) rotateImage(90)
                    }
                    true
                }
            }
            cropButton.setOnClickListener {
                with(cropImageView) {
                    croppedImageAsync(saveCompressFormat = formatPNG(), 100, 340, 240)
                }
            }
            cropImageView.apply {
                setImageUriAsync(args.uri)
                setOnCropImageCompleteListener { _, result ->
                    if (!result.isSuccessful) {
                        showToast(result.error?.localizedMessage)
                        return@setOnCropImageCompleteListener
                    }
                    setFragmentResult(CROP_IMAGE_RESULT, Bundle().apply {
                        putParcelable(CROPPED_BITMAP, result.uriContent)
                    })
                    findNavController().popBackStack()
                }
            }
        }
    }
}
