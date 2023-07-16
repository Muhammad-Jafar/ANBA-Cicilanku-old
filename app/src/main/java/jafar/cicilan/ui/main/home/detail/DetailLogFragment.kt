package jafar.cicilan.ui.main.home.detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jafar.cicilan.base.BaseFragment
import jafar.cicilan.databinding.MainDetailLogBinding
import jafar.cicilan.ui.main.DetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailLogFragment : BaseFragment<MainDetailLogBinding>(MainDetailLogBinding::inflate) {
    private val viewModel: DetailViewModel by viewModel()
    private val args: DetailLogFragmentArgs by navArgs()
    private val logAdapter = DetailLogAdapter()

    override fun renderView(savedInstanceState: Bundle?) {
        viewModel.cicilanId = args.cicilanId

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            rvDetailLog.adapter = logAdapter
            lifecycleScope.launch {
                viewModel.getLog.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collectLatest { item ->
                        if (item.isNotEmpty()) {
                            viewEmpty.visibility = View.GONE
                            logAdapter.submitList(item)
                        } else viewEmpty.visibility = View.VISIBLE
                    }
            }
        }
    }
}
