package uz.pdp.breakingnews

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import uz.pdp.breakingnews.databinding.ActivityMainBinding
import uz.pdp.breakingnews.madels.languages.LocalHelper
import uz.pdp.breakingnews.preference.MyShared

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var myShared = MyShared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        myShared = MyShared.getInstance(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val boolean = myShared.getList("theme")
        if (boolean == "1") {
            window?.statusBarColor = Color.BLACK
        } else if (boolean == "0"){
            window?.statusBarColor = Color.WHITE
        }
        binding?.apply {
            val navController = (supportFragmentManager.findFragmentById(R.id.my_support_nav) as NavHostFragment).navController
            NavigationUI.setupWithNavController(bottomMain, navController)

            LocalHelper().onAttach(this@MainActivity)
            LocalHelper().getLanguage(this@MainActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.my_support_nav).navigateUp()
    }

    fun hide() {
        if (binding != null) {
            val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.close_dawn)
            binding?.bottomMain?.animation = loadAnimation
            loadAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding?.bottomMain?.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        }
    }

    fun show() {
        if (binding != null) {
            binding?.bottomMain?.visibility = View.VISIBLE
            val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.open_dawn)
            binding?.bottomMain?.animation = loadAnimation
        }
    }

}