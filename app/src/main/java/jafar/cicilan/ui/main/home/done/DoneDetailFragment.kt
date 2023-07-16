package jafar.cicilan.ui.main.home.done

import android.os.Bundle
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
import jafar.cicilan.databinding.DialogDeleteDataBinding
import jafar.cicilan.databinding.MainDetailDoneBinding
import jafar.cicilan.ui.main.DetailViewModel
import jafar.cicilan.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoneDetailFragment : BaseFragment<MainDetailDoneBinding>(MainDetailDoneBinding::inflate) {
    private val viewModel: DetailViewModel by viewModel()
    private val args: DoneDetailFragmentArgs by navArgs()

    override fun renderView(savedInstanceState: Bundle?) {
        viewModel.cicilanId = args.cicilanId
        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            lifecycleScope.launch {
                viewModel.getDetail.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collectLatest { if (it != null) setDetailData(it) }
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
            doneAtContent.text = data.lunas_pada?.format("d MMMM yyyy")

            categoryContent.text = data.kategori
            userNameContent.text = data.nama_penyicil

            labaContent.text = rupiahFormat(data.laba_per_bulan)
            totalLabaContent.setTextColor(getColor(totalLabaContent, R.attr.colorLeaf))
            totalLabaContent.text = "+ ".plus(rupiahFormat(data.total_laba))

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
                    DoneDetailFragmentDirections.actionDetailLunasToPreviewImage(args.cicilanId)
                findNavController().navigate(destinationAvatar, extras)
            }
            logTransactionButton.setOnClickListener {
                val destinationLog =
                    DoneDetailFragmentDirections.actionDetailLunasToLog(args.cicilanId)
                findNavController().navigate(destinationLog)
            }
            deleteButton.setOnClickListener { showDeleteDialog(args.cicilanId) }
        }
    }

    private fun showDeleteDialog(id: Int) {
        val dialog = DialogDeleteDataBinding.inflate(layoutInflater)
        val dialogForm = BottomSheetDialog(requireContext()).apply {
            setContentView(dialog.root)
            setCancelable(true)
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
}
