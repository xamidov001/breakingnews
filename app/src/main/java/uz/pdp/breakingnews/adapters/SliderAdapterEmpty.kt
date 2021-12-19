package uz.pdp.breakingnews.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import uz.pdp.breakingnews.R
import uz.pdp.breakingnews.databinding.SliderItemEmptyBinding

class SliderAdapterEmpty(var context: Context, var list: List<String>) : RecyclerView.Adapter<SliderAdapterEmpty.VH>() {


    inner class VH(var binding: SliderItemEmptyBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(SliderItemEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val article = list[position]

            Glide.with(context).load(article).addListener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    anim.visibility = View.GONE
                    return false
                }
            }).into(bigImage)
        }
    }

    override fun getItemCount(): Int = list.size


}