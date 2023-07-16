package jafar.cicilan.ui.main.home.current

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jafar.cicilan.R
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.databinding.ItemCurrentBinding
import jafar.cicilan.ui.main.home.HomeFragmentDirections
import jafar.cicilan.utils.Constanta
import jafar.cicilan.utils.rupiahFormat

class CurrentAdapter : ListAdapter<ItemEntity, CurrentAdapter.ViewHolder>(Constanta.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemCurrentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemCurrentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ItemEntity) {
            with(binding) {
                if (items.nominal_lunas != null) {
                    with(itemProgress) {
                        max = items.nominal_bayar
                        setProgressCompat(items.nominal_lunas, true)
                    }
                    sisaCicilanContent.text =
                        rupiahFormat(items.nominal_bayar - items.nominal_lunas)
                } else {
                    with(itemProgress) {
                        max = items.nominal_bayar
                        setProgressCompat(0, true)
                    }
                    sisaCicilanContent.text = rupiahFormat(items.nominal_bayar - 0)
                }

                if (items.gambar_barang != null) avatar.setImageURI(items.gambar_barang.toUri())
                else avatar.apply {
                    setImageResource(R.drawable.icon_broken_image_black)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                nameProduct.text = items.nama_barang
                nameUser.text = items.nama_penyicil
                cicilanPerBulan.text = rupiahFormat(items.nominal_per_bulan)
            }
            itemView.setOnClickListener {
                if (items.id_cicilan != null) {
                    val direction =
                        HomeFragmentDirections.actionMainToCurrentDetail(items.id_cicilan)
                    it.findNavController().navigate(direction)
                } else return@setOnClickListener
            }
        }
    }
}
