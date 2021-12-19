package uz.pdp.breakingnews.dagger.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import uz.pdp.breakingnews.dagger.data.entity.ArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = REPLACE)
    suspend fun addArticle(article: ArticleEntity)

    @Query("select * from articleentity where url=:n")
    suspend fun getArticleByUrl(n: String): ArticleEntity

    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    @Query("select * from ArticleEntity")
    suspend fun getAllArticles(): List<ArticleEntity>

}