package jafar.cicilan.ui.main.home.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jafar.cicilan.data.model.ItemLogEntity
import jafar.cicilan.databinding.ItemDetailLogBinding
import jafar.cicilan.utils.Constanta
import jafar.cicilan.utils.format
import jafar.cicilan.utils.rupiahFormat

class DetailLogAdapter :
    ListAdapter<ItemLogEntity, DetailLogAdapter.ViewHolder>(Constanta.DIFF_CALLBACK_LOG) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemDetailLogBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemDetailLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemLogEntity) {
            with(binding) {
                tanggalTransaksi.text = item.tgl_transaksi.format("d MMMM yyyy")
                nominalTransaksi.text = rupiahFormat(item.nominal_transaksi)
                noteTransaksi.text = if (item.catatan.equals("")) "Tidak ada catatan" else item.catatan
            }
        }
    }
}
