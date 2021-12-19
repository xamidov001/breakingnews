package uz.pdp.breakingnews.adapters

import android.content.Context
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
import uz.pdp.breakingnews.R
import uz.pdp.breakingnews.dagger.data.entity.ArticleEntity
import uz.pdp.breakingnews.databinding.SliderItemBinding
import uz.pdp.breakingnews.madels.news.Article

class SliderAdapter(var context: Context, var listener: OnClickSliderListener) : ListAdapter<ArticleEntity, SliderAdapter.VH>(MyDiffUtil()) {

    interface OnClickSliderListener {
        fun onClick(article: ArticleEntity)

        fun onClickSave(article: ArticleEntity)
    }

    class MyDiffUtil: DiffUtil.ItemCallback<ArticleEntity>() {
        override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem.urlToImage == newItem.urlToImage
        }

        override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
            return oldItem == newItem
        }

    }

    inner class VH(var binding: SliderItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val article = getItem(position)

            root.setOnClickListener {
                listener.onClick(article)
            }

            if (article.isSave) {
                bookmark.setImageResource(R.drawable.bookmark_selected)
            } else {
                bookmark.setImageResource(R.drawable.ic_bookmark__1)
            }

            cardSave.setOnClickListener {
                article.isSave = !article.isSave
                if (article.isSave) {
                    bookmark.setImageResource(R.drawable.bookmark_selected)
                } else {
                    bookmark.setImageResource(R.drawable.ic_bookmark__1)
                }
                listener.onClickSave(article)
            }

            authorTxt.text = "by ${article.author}"

            definationTxt.text = article.title

            Glide.with(context).load(article.urlToImage).addListener(object :
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


}