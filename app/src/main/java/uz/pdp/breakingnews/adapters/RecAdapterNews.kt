package uz.pdp.breakingnews.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import uz.pdp.breakingnews.dagger.data.entity.ArticleEntity

import uz.pdp.breakingnews.databinding.ItemCategoryBinding
import uz.pdp.breakingnews.databinding.ItemNewsBinding
import uz.pdp.breakingnews.madels.adap.RVClass
import uz.pdp.breakingnews.madels.news.Article

class RecAdapterNews(var context: Context, var listener: OnCardClicked): ListAdapter<ArticleEntity, RecAdapterNews.VH>(MyDiffUtil()) {

    interface OnCardClicked {
        fun onclick(articleEntity: ArticleEntity)
    }

    class MyDiffUtil: DiffUtil.ItemCallback<ArticleEntity>() {
        override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem.urlToImage == newItem.urlToImage
        }

        override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem == newItem
        }

    }

    inner class VH(var binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val item = getItem(position)

            textDescription.text = item.description

            root.setOnClickListener {
                listener.onclick(item)
            }

            Glide.with(context).load(item.urlToImage).addListener(object :
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
            }).into(image)

        }
    }

}