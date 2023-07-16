package jafar.cicilan.ui.main.form

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.parcelable
import com.google.android.material.bottomsheet.BottomSheetDialog
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.data.model.Modal
import jafar.cicilan.databinding.DialogPickImageBinding
import jafar.cicilan.databinding.MainFormAddBinding
import jafar.cicilan.ui.main.FormViewModel
import jafar.cicilan.utils.Constanta
import jafar.cicilan.utils.TakePicture
import jafar.cicilan.utils.addAutoConverterToMoneyFormat
import jafar.cicilan.utils.afterInputNumberChanged
import jafar.cicilan.utils.afterInputStringChanged
import jafar.cicilan.utils.dotPixel
import jafar.cicilan.utils.getNumber
import jafar.cicilan.utils.runWhenResumed
import jafar.cicilan.utils.showSoftKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Random

class FormAddFragment : BaseFragment<MainFormAddBinding>(MainFormAddBinding::inflate) {
    private lateinit var requestFromGallery: ActivityResultLauncher<String>
    private lateinit var requestFromCamera: ActivityResultLauncher<String>
    private val viewModel: FormViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFromCamera = registerForActivityResult(TakePicture()) { openImageCropFragment(it) }
        requestFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            openImageCropFragment(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("imageUri", viewModel.imageUri)
    }

    override fun renderView(savedInstanceState: Bundle?) {
        savedInstanceState?.parcelable<Uri>("imageUri")?.let {
            binding.saveImage.setImageURI(it)
            viewModel.imageUri = it
        }
        with(binding) {
            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                isTitleCentered = true
            }
            setFragmentResultListener(Constanta.CROP_IMAGE_RESULT) { _, bundle ->
                bundle.parcelable<Uri>(Constanta.CROPPED_BITMAP).let {
                    viewModel.imageUri = it
                    saveImage.setImageURI(it)
                }
            }
            saveImage.setOnClickListener { showImageChooser() }
            saveButton.setOnClickListener { doSave() }
        }
        validate()
    }

    private fun validate() = with(binding) {
        nameInput.apply {
            requestFocus()
            showSoftKeyboard()
            afterInputStringChanged {
                saveButton.isEnabled = nameInput.text.toString().isNotEmpty()
                nameInputLayout.error = when {
                    it!!.isBlank() -> getString(R.string.fill_data)
                    else -> null
                }
            }
        }
        thingInput.afterInputStringChanged {
            thingInputLayout.error = when {
                it!!.isBlank() -> getString(R.string.fill_data)
                else -> null
            }
        }
        categoryInput.afterInputStringChanged {
            categoryInputLayout.error = when {
                it!!.isBlank() -> getString(R.string.fill_data)
                else -> null
            }
        }
        priceInput.apply {
            addAutoConverterToMoneyFormat(priceInputLayout)
            afterInputNumberChanged {
                priceInputLayout.error = when {
                    it < 500000 -> getString(R.string.form_min_harga)
                    it > 10000000 -> getString(R.string.form_max_harga)
                    else -> null
                }
            }
        }
        firstPayInput.apply {
            addAutoConverterToMoneyFormat(firstInputLayout)
            afterInputNumberChanged {
                if (priceInput.text.getNumber() > 1) {
                    firstInputLayout.error = when {
                        it < 300000 -> getString(R.string.form_dp)
                        it >= priceInput.text.getNumber() -> getString(R.string.form_dp_lessThan_harga)
                        else -> null
                    }
                } else firstInputLayout.error = getString(R.string.filled_price)
            }
        }
        periodPayInput.afterInputNumberChanged {
            periodInputLayout.error = when {
                it > 12 -> getString(R.string.form_date_periode)
                else -> null
            }
        }
        tenggatPayInput.afterInputNumberChanged {
            tenggatInputLayout.error = when {
                it > 30 -> getString(R.string.form_date_tenggat)
                else -> null
            }
        }
    }

    private fun doSave() = with(binding) {
        val kategoriInput = categoryInput.text.toString()
        val penyicil = nameInput.text.toString()
        val barang = thingInput.text.toString()
        val harga = priceInput.text.getNumber()
        val dp = firstPayInput.text.getNumber()
        val periode = periodPayInput.text.getNumber()
        val tenggat = tenggatPayInput.text.getNumber()

        when {
            penyicil.isBlank() -> nameInputLayout.error = getString(R.string.fill_data)
            barang.isBlank() -> thingInputLayout.error = getString(R.string.fill_data)
            kategoriInput.isBlank() -> categoryInputLayout.error = getString(R.string.fill_data)
            harga < 1 -> priceInputLayout.error = getString(R.string.fill_data)
            dp < 1 -> firstInputLayout.error = getString(R.string.fill_data)
            periode < 1 -> periodInputLayout.error = getString(R.string.fill_data)
            tenggat < 1 -> tenggatInputLayout.error = getString(R.string.fill_data)
            else -> {
                runWhenResumed {
                    val imageUri =
                        viewModel.imageUri?.let { convertCacheImageToExternalFileImage(it) }
                    val data = Modal(
                        null, imageUri?.toString(), penyicil, barang, kategoriInput,
                        harga, dp, periode, tenggat
                    )
                    viewModel.add(data)
                    withContext(Dispatchers.Main) { findNavController().navigateUp() }
                }
            }
        }
    }

    private fun openImageCropFragment(uri: Uri?) {
        uri ?: return
        findNavController().navigate(FormAddFragmentDirections.actionFormToImageCrop(uri))
    }

    private fun showImageChooser() {
        val form = DialogPickImageBinding.inflate(layoutInflater)
        val dialogChooser = BottomSheetDialog(requireContext()).apply {
            setContentView(form.root)
            behavior.maxHeight = 480.dotPixel()
        }
        with(form) {
            root.doOnPreDraw { dialogChooser.behavior.peekHeight = it.height }
            cameraButton.setOnClickListener {
                dialogChooser.dismiss()
                requestFromCamera.launch("image-captured.jpg")
            }
            galeriButton.setOnClickListener {
                dialogChooser.dismiss()
                requestFromGallery.launch("image/*")
            }
        }
        dialogChooser.show()
    }

    private fun convertCacheImageToExternalFileImage(cacheImageUri: Uri): Uri {
        val fileName = "${Calendar.getInstance().timeInMillis}+${Random().nextInt(100)}.png"
        val parentPath = requireContext().getExternalFilesDirs(Environment.DIRECTORY_PICTURES)
            .first().absolutePath
        val destinationFile = File(parentPath, fileName)
        FileOutputStream(destinationFile).use { out ->
            requireContext().contentResolver.openInputStream(cacheImageUri).use { it?.copyTo(out) }
        }
        return destinationFile.toUri()
    }
}
