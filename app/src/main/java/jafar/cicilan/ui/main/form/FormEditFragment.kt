package jafar.cicilan.ui.main.form

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.parcelable
import com.google.android.material.bottomsheet.BottomSheetDialog
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.Modal
import jafar.cicilan.databinding.DialogPickImageBinding
import jafar.cicilan.databinding.MainFormEditBinding
import jafar.cicilan.ui.main.FormViewModel
import jafar.cicilan.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*

class FormEditFragment : BaseFragment<MainFormEditBinding>(MainFormEditBinding::inflate) {
    private lateinit var requestFromGallery: ActivityResultLauncher<String>
    private lateinit var requestFromCamera: ActivityResultLauncher<String>
    private val viewModel: FormViewModel by viewModel()
    private val args: FormEditFragmentArgs by navArgs()

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
                    tvAddImage.visibility = View.GONE
                    saveImage.setImageURI(it)
                }
            }
            if (args.item?.id_cicilan != null) loadData(args.item!!)
            saveImage.setOnClickListener { showImageChooser() }
            saveButton.setOnClickListener { doUpdate() }
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
        priceInput.afterInputNumberChanged {
            priceInputLayout.error = when {
                it < 500000 -> getString(R.string.form_min_harga)
                it > 10000000 -> getString(R.string.form_max_harga)
                else -> null
            }
        }
        firstPayInput.afterInputNumberChanged {
            if (priceInput.text.getNumber() > 1) {
                firstInputLayout.error = when {
                    it < 300000 -> getString(R.string.form_dp)
                    it >= priceInput.text.getNumber() -> getString(R.string.form_dp_lessThan_harga)
                    else -> null
                }
            } else firstInputLayout.error = getString(R.string.filled_price)
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

    private fun loadData(item: ItemEntity) = with(binding) {
        if (item.gambar_barang != null) {
            saveImage.setImageURI(item.gambar_barang.toUri())
            tvAddImage.visibility = View.GONE
        } else {
            saveImage.apply {
                setImageResource(R.drawable.icon_broken_image_black)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            tvAddImage.text = getString(R.string.no_image_found)
        }
        nameInput.apply {
            setText(item.nama_penyicil)
            requestFocus()
            showSoftKeyboard()
            afterInputStringChanged {
                saveButton.isEnabled = nameInput.text.toString().isNotEmpty()
            }
        }
        thingInput.setText(item.nama_barang)
        categoryInput.setText(item.kategori, false)
        priceInput.addAutoConverterToMoneyFormat(priceInputLayout)
        priceInput.setText(item.harga_barang.toString())
        firstPayInput.addAutoConverterToMoneyFormat(firstInputLayout)
        firstPayInput.setText(item.uang_muka.toString())
        periodPayInput.setText(item.periode.toString())
        tenggatPayInput.setText(item.tenggat_bayar.toString())
    }

    private fun doUpdate() = with(binding) {
        val penyicil = nameInput.text.toString()
        val barang = thingInput.text.toString()
        val kategoriInput = categoryInput.text.toString()
        val harga = priceInput.text.getNumber()
        val dp = firstPayInput.text.getNumber()
        val periode = periodPayInput.text.getNumber()
        val tenggat = tenggatPayInput.text.getNumber()

        val imageUri = if (args.item?.gambar_barang != null) {
            if (viewModel.imageUri == null) args.item?.gambar_barang
            else viewModel.imageUri?.let { convertCacheImageToExternalFileImage(it) }
        } else args.item?.gambar_barang

        when {
            penyicil.isBlank() -> nameInputLayout.error = getString(R.string.fill_data)
            barang.isBlank() -> thingInputLayout.error = getString(R.string.fill_data)
            kategoriInput.isBlank() -> categoryInputLayout.error = getString(R.string.fill_data)
            harga < 1 -> priceInputLayout.error = getString(R.string.fill_data)
            dp < 1 -> firstInputLayout.error = getString(R.string.fill_data)
            periode < 1 -> periodInputLayout.error = getString(R.string.fill_data)
            tenggat < 1 -> tenggatInputLayout.error = getString(R.string.fill_data)
            else -> {
                val data = Modal(
                    args.item?.id_cicilan, imageUri.toString(), penyicil, barang, kategoriInput,
                    harga, dp, periode, tenggat
                )
                runWhenResumed {
                    viewModel.update(data)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun openImageCropFragment(uri: Uri?) {
        uri ?: return
        findNavController().navigate(FormEditFragmentDirections.actionFormEditToImageCrop(uri))
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
        val fileName = "${Calendar.getInstance().timeInMillis}+${Random().nextInt(100)}.webp"
        val parentPath = requireContext().getExternalFilesDirs(Environment.DIRECTORY_PICTURES)
            .first().absolutePath
        val destinationFile = File(parentPath, fileName)
        FileOutputStream(destinationFile).use { out ->
            requireContext().contentResolver.openInputStream(cacheImageUri).use { it?.copyTo(out) }
        }
        return destinationFile.toUri()
    }
}
