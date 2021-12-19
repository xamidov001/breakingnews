package uz.pdp.breakingnews.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import uz.pdp.breakingnews.databinding.ItemCategoryBinding
import uz.pdp.breakingnews.madels.adap.RVClass

class RecAdapterCategory(var list: List<RVClass>, var listener: OnCardClicked): RecyclerView.Adapter<RecAdapterCategory.VH>() {

    interface OnCardClicked {
        fun onclick(rvClass: Int, checked: Boolean)
    }

    inner class VH(var binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val item = list[position]

            txt.text = item.text

            card.setOnClickListener {
                item.isChecked = !item.isChecked
                setColor(item.isChecked, card, txt)
                listener.onclick(position, item.isChecked)
            }

            setColor(item.isChecked, card, txt)

        }
    }

    fun setColor(isChecked: Boolean,card: CardView, txt: TextView) {
        if (isChecked) {
            card.setCardBackgroundColor(Color.parseColor("#475AD7"))
            txt.setTextColor(Color.WHITE)
        } else {
            card.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
            txt.setTextColor(Color.parseColor("#666C8E"))
        }
    }

    override fun getItemCount(): Int = list.size
}