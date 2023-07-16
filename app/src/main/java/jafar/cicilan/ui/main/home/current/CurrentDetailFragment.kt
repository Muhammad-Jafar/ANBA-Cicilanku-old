package jafar.cicilan.ui.main.home.current

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.MaterialColors.getColor
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity
import jafar.cicilan.databinding.DialogConfirmLunasBinding
import jafar.cicilan.databinding.DialogDeleteDataBinding
import jafar.cicilan.databinding.DialogInputNominalBinding
import jafar.cicilan.databinding.MainDetailCurrentBinding
import jafar.cicilan.ui.main.DetailViewModel
import jafar.cicilan.utils.addAutoConverterToMoneyFormat
import jafar.cicilan.utils.afterInputNumberChanged
import jafar.cicilan.utils.currentDate
import jafar.cicilan.utils.dotPixel
import jafar.cicilan.utils.format
import jafar.cicilan.utils.getNumber
import jafar.cicilan.utils.rupiahFormat
import jafar.cicilan.utils.showSoftKeyboard
import jafar.cicilan.utils.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class CurrentDetailFragment :
    BaseFragment<MainDetailCurrentBinding>(MainDetailCurrentBinding::inflate) {
    private val viewModel: DetailViewModel by viewModel()
    private val args: CurrentDetailFragmentArgs by navArgs()

    override fun renderView(savedInstanceState: Bundle?) {
        viewModel.cicilanId = args.cicilanId
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        lifecycleScope.launch {
            viewModel.getDetail.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                if (it != null) setDetailData(it)
            }
        }
    }

    private fun setDetailData(data: ItemEntity) {
        with(binding) {
            toolbar.title = data.nama_barang
            if (data.gambar_barang != null) avatarPreview.apply {
                setImageURI(data.gambar_barang.toUri())
                scaleType = ImageView.ScaleType.CENTER_CROP
            } else avatarPreview.setImageResource(R.drawable.icon_broken_image_black)

            createdAtContent.text = data.dibuat_pada.format("d MMMM yyyy")
            categoryContent.text = data.kategori

            targetProgressBar.apply {
                max = data.nominal_bayar
                setProgressCompat(data.nominal_lunas!!, true)
            }
            val persen =
                ((data.nominal_lunas!!.toFloat() / data.nominal_bayar.toFloat()) * 100F).roundToInt()
            if (persen == 100) showLunasDialog()
            dataPersen.text = persen.toString()
            lunasContent.text = rupiahFormat(data.nominal_lunas)
            sisaContent.text = rupiahFormat(data.nominal_bayar - data.nominal_lunas)

            labaContent.text = rupiahFormat(data.laba_per_bulan)
            totalLabaContent.setTextColor(getColor(totalLabaContent, R.attr.colorLeaf))
            totalLabaContent.text = "+ ".plus(rupiahFormat(data.total_laba))

            userNameContent.text = data.nama_penyicil
            thingPriceContent.text = rupiahFormat(data.harga_barang)
            dpContent.text = rupiahFormat(data.uang_muka)
            nominalCicilanContent.text = rupiahFormat(data.nominal_bayar)
            nominalPerBulanContent.text = rupiahFormat(data.per_bulan)
            totalPerBulanContent.text = rupiahFormat(data.nominal_per_bulan)
            periodeContent.text = data.periode.toString()
            tenggatContent.text = data.tenggat_bayar.toString()

            avatarPreview.setOnClickListener {
                val extras = FragmentNavigatorExtras(avatarPreview to "image.ForPreview")
                val destinationAvatar =
                    CurrentDetailFragmentDirections.actionDetailBerjalanToPreviewImage(args.cicilanId)
                findNavController().navigate(destinationAvatar, extras)
            }
            logTransactionButton.setOnClickListener {
                val destinationLog =
                    CurrentDetailFragmentDirections.actionDetailBerjalanToLog(args.cicilanId)
                findNavController().navigate(destinationLog)
            }
            deleteButton.setOnClickListener { showDeleteDialog(args.cicilanId) }
            editButton.setOnClickListener {
                val destinationEdit =
                    CurrentDetailFragmentDirections.actionDetailCicilanBerjalanToFormEdit(data)
                findNavController().navigate(destinationEdit)
            }
            payDebtButton.setOnClickListener {
                doPayCicilan(data.nominal_lunas, data.nominal_bayar)
            }
        }
    }

    private fun showLunasDialog() {
        val dialogLunas = DialogConfirmLunasBinding.inflate(layoutInflater)
        val formLunas = BottomSheetDialog(requireContext()).apply {
            setContentView(dialogLunas.root)
            behavior.maxHeight = 560.dotPixel()
        }
        with(dialogLunas) {
            root.doOnPreDraw { formLunas.behavior.peekHeight = it.height }
            ConfirmButton.setOnClickListener {
                formLunas.dismiss()
                findNavController().navigateUp()
            }
        }
        formLunas.show()
    }

    private fun showDeleteDialog(id: Int) {
        val dialog = DialogDeleteDataBinding.inflate(layoutInflater)
        val dialogForm = BottomSheetDialog(requireContext()).apply {
            setContentView(dialog.root)
            behavior.maxHeight = 480.dotPixel()
        }
        with(dialog) {
            root.doOnPreDraw { dialogForm.behavior.peekHeight = it.height }
            ConfirmButton.setOnClickListener {
                viewModel.delete(id)
                dialogForm.dismiss()
                showToast(getString(R.string.deleted_successfully))
                findNavController().navigateUp()
            }
            cancelButton.setOnClickListener { dialogForm.dismiss() }
        }
        dialogForm.show()
    }

    private fun doPayCicilan(lunas: Int, utang: Int) {
        val inputForm = DialogInputNominalBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()).apply {
            setContentView(inputForm.root)
            behavior.maxHeight = 540.dotPixel()
        }

        with(inputForm) {
            root.doOnPreDraw { dialog.behavior.peekHeight = it.height }
            fun validateInput(): Boolean {
                if (nominalInput.text.getNumber() < (utang - lunas)) {
                    nominalInputLayout.error = when {
                        nominalInput.text.getNumber() < 1 -> getString(R.string.fill_data)
                        nominalInput.text.getNumber() < 100000 -> getString(R.string.input_min_limit)
                        nominalInput.text.getNumber() > (utang - lunas) -> getString(R.string.input_fill_over)
                        else -> return true
                    }
                } else nominalInputLayout.error = when {
                    nominalInput.text.getNumber() > (utang - lunas) -> getString(R.string.input_fill_over)
                    else -> return true
                }
                return true
            }
            with(nominalInput) {
                requestFocus()
                showSoftKeyboard()
                afterInputNumberChanged {
                    doInputButton.isEnabled = nominalInput.text.getNumber() > 1
                    validateInput()
                }
                addAutoConverterToMoneyFormat(nominalInputLayout)
                hint = rupiahFormat(utang - lunas)
            }

            with(noteInput) {
                hint = getString(R.string.note_here)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (nominalInput.text.getNumber() < (utang - lunas)) {
                            when {
                                nominalInput.text.getNumber() < 1 -> getString(R.string.fill_data)
                                nominalInput.text.getNumber() < 100000 -> showToast(getString(R.string.input_min_limit))
                                nominalInput.text.getNumber() > (utang - lunas) -> showToast(
                                    getString(R.string.input_fill_over)
                                )

                                else -> {
                                    val dataInput = ItemLogEntity(
                                        null,
                                        args.cicilanId,
                                        currentDate,
                                        nominalInput.text.getNumber(),
                                        noteInput.text.toString()
                                    )
                                    viewModel.updateNominal(dataInput)
                                    dialog.dismiss()
                                }
                            }
                        } else when {
                            nominalInput.text.getNumber() > (utang - lunas) -> getString(R.string.input_fill_over)
                            else -> {
                                val dataInput = ItemLogEntity(
                                    null,
                                    args.cicilanId,
                                    currentDate,
                                    nominalInput.text.getNumber(),
                                    noteInput.text.toString()
                                )
                                viewModel.updateNominal(dataInput)
                                dialog.dismiss()
                            }
                        }
                    }
                    false
                }
                doInputButton.setOnClickListener {
                    if (nominalInput.text.getNumber() < (utang - lunas)) when {
                        nominalInput.text.getNumber() < 1 -> getString(R.string.fill_data)
                        nominalInput.text.getNumber() < 100000 -> showToast(getString(R.string.input_min_limit))
                        nominalInput.text.getNumber() > (utang - lunas) -> showToast(getString(R.string.input_fill_over))
                        else -> {
                            val dataInput = ItemLogEntity(
                                null,
                                args.cicilanId,
                                currentDate,
                                nominalInput.text.getNumber(),
                                noteInput.text.toString()
                            )
                            viewModel.updateNominal(dataInput)
                            dialog.dismiss()
                        }
                    }
                    else when {
                        nominalInput.text.getNumber() > (utang - lunas) -> getString(R.string.input_fill_over)
                        else -> {
                            val dataInput = ItemLogEntity(
                                null,
                                args.cicilanId,
                                currentDate,
                                nominalInput.text.getNumber(),
                                noteInput.text.toString()
                            )
                            viewModel.updateNominal(dataInput)
                            dialog.dismiss()
                        }
                    }
                }
            }
            cancelButton.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

}
