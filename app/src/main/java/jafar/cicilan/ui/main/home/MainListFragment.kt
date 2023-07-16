package jafar.cicilan.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import jafar.cicilan.databinding.MainHomeListBinding
import jafar.cicilan.ui.main.HomeViewModel
import jafar.cicilan.ui.main.home.current.CurrentAdapter
import jafar.cicilan.ui.main.home.done.DoneAdapter
import jafar.cicilan.utils.Constanta
import jafar.cicilan.utils.runWhenCreated
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainListFragment : Fragment() {
    private var _binding: MainHomeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private val currentAdapter =
        CurrentAdapter().apply { stateRestorationPolicy = PREVENT_WHEN_EMPTY }
    private val doneAdapter = DoneAdapter().apply { stateRestorationPolicy = PREVENT_WHEN_EMPTY }
    private var tabName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainHomeListBinding.inflate(
            LayoutInflater.from(parentFragment?.context), container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabName = arguments?.getString(Constanta.ARGS_TAB)
        with(binding) {
            runWhenCreated {
                if (tabName == Constanta.TAB_CURRENT) {
                    launch {
                        viewModel.getCurrentList.collectLatest { item ->
                            if (item.isEmpty()) {
                                progressBar.visibility = View.GONE
                                rvCicilanBerjalan.visibility = View.GONE
                                viewEmptyCurrent.visibility = View.VISIBLE
                            } else {
                                progressBar.visibility = View.GONE
                                viewEmptyCurrent.visibility = View.GONE
                                rvCicilanBerjalan.visibility = View.VISIBLE
                                currentAdapter.submitList(item)
                            }
                        }
                    }
                    rvCicilanBerjalan.adapter = currentAdapter
                } else {
                    launch {
                        viewModel.getDoneList.collectLatest { item ->
                            if (item.isEmpty()) {
                                progressBar.visibility = View.GONE
                                rvCicilanBerjalan.visibility = View.GONE
                                viewEmptyDone.visibility = View.VISIBLE
                            } else {
                                progressBar.visibility = View.GONE
                                viewEmptyDone.visibility = View.GONE
                                rvCicilanLunas.visibility = View.VISIBLE
                                doneAdapter.submitList(item)
                            }
                        }
                    }
                    rvCicilanLunas.adapter = doneAdapter
                }
            }
        }
    }
}
