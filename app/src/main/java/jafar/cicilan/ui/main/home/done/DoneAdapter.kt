package jafar.cicilan.ui.main.home.done

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jafar.cicilan.R
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.databinding.ItemDoneBinding
import jafar.cicilan.ui.main.home.HomeFragmentDirections
import jafar.cicilan.utils.Constanta
import jafar.cicilan.utils.format
import jafar.cicilan.utils.rupiahFormat

class DoneAdapter : ListAdapter<ItemEntity, DoneAdapter.ViewHolder>(Constanta.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemDoneBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemDoneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ItemEntity) {
            with(binding) {
                if (items.gambar_barang != null) avatar.setImageURI(items.gambar_barang.toUri())
                else avatar.apply {
                    setImageResource(R.drawable.icon_broken_image_black)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }

                nameProduct.text = items.nama_barang
                nameUser.text = items.nama_penyicil
                cicilanPerBulan.text = "+ ".plus(rupiahFormat(items.total_laba))
                lunasPada.text = items.lunas_pada?.format("d MMMM yyyy")
            }
            itemView.setOnClickListener {
                if (items.id_cicilan != null) {
                    val direction = HomeFragmentDirections.actionMainToDoneDetail(items.id_cicilan)
                    it.findNavController().navigate(direction)
                } else return@setOnClickListener
            }
        }
    }
}
