package jafar.cicilan.ui.main.home.detail

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.google.android.material.transition.MaterialFadeThrough
import jafar.cicilan.R
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.databinding.MainDetailPreviewBinding
import jafar.cicilan.ui.main.DetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreviewImageFragment :
    BaseFragment<MainDetailPreviewBinding>(MainDetailPreviewBinding::inflate) {
    private val viewModel: DetailViewModel by viewModel()
    private val args: PreviewImageFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        val inflater = TransitionInflater.from(requireContext())
        sharedElementEnterTransition = inflater.inflateTransition(R.transition.change_bounds)
        sharedElementReturnTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun renderView(savedInstanceState: Bundle?) {
        viewModel.cicilanId = args.cicilanId

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            lifecycleScope.launch {
                viewModel.getDetail.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).collectLatest { item ->
                        if (item != null) {
                            if (item.gambar_barang != null) {
                                textPreview.visibility = View.GONE
                                preview.visibility = View.VISIBLE
                                preview.setImageURI(item.gambar_barang.toUri())
                            } else {
                                preview.visibility = View.GONE
                                textPreview.visibility = View.VISIBLE
                            }
                        }
                    }
            }
        }
    }
}
