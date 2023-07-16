package jafar.cicilan.utils

import androidx.recyclerview.widget.DiffUtil
import jafar.cicilan.data.model.ItemEntity
import jafar.cicilan.data.model.ItemLogEntity

object Constanta {

    /* Home list */
    const val ARGS_TAB = "Tab argument"
    const val TAB_CURRENT = "Tab current"
    const val TAB_DONE = "Tab done"

    /* Image Crop Fragment */
    const val CROP_IMAGE_RESULT = "crop_image_result"
    const val CROPPED_BITMAP = "bitmap"

    /* Diff Callback RecycleView for ItemEntity*/
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemEntity>() {
        override fun areItemsTheSame(oldItem: ItemEntity, newItem: ItemEntity):
                Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: ItemEntity, newItem: ItemEntity):
                Boolean = oldItem.id_cicilan == newItem.id_cicilan
    }

    /* Diff Callback RecycleView for ItemLogEntity*/
    val DIFF_CALLBACK_LOG = object : DiffUtil.ItemCallback<ItemLogEntity>() {
        override fun areItemsTheSame(oldItem: ItemLogEntity, newItem: ItemLogEntity):
                Boolean = oldItem.id_cicilan == newItem.id_cicilan

        override fun areContentsTheSame(oldItem: ItemLogEntity, newItem: ItemLogEntity):
                Boolean = oldItem == newItem
    }

    /* Store theme and language value */
    enum class StoreData { Theme, Language }

    const val defaultTheme = -1 // Follow system default theme
    const val defaultLanguage = "in"
}
