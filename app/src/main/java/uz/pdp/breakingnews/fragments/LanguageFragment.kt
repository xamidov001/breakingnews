package uz.pdp.breakingnews.fragments

import android.graphics.Color
import android.icu.util.ULocale.getLanguage
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import uz.pdp.breakingnews.MainActivity
import uz.pdp.breakingnews.R
import uz.pdp.breakingnews.databinding.FragmentLanguageBinding
import uz.pdp.breakingnews.madels.languages.LocalHelper
import java.util.*


class LanguageFragment : Fragment(R.layout.fragment_language) {

    private val binding by viewBinding(FragmentLanguageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hide()
        val onAttach = LocalHelper().onAttach(requireContext())
        binding.apply {

            backImage.setOnClickListener {
                findNavController().popBackStack()
            }

            title.text = onAttach?.getText(R.string.setting_txt3)
            val language = LocalHelper().getLanguage(root.context)
            when(language?.lowercase(Locale.getDefault())){
                "en".lowercase(Locale.getDefault())->{
                    imageLan1.visibility = View.VISIBLE
                    imageLan2.visibility = View.GONE
                    txtEn.setTextColor(Color.WHITE)
                    txtRu.setTextColor(Color.BLACK)
                    card1.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    card2.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                }
                "ru".lowercase(Locale.getDefault())->{
                    imageLan2.visibility = View.VISIBLE
                    imageLan1.visibility = View.GONE
                    txtRu.setTextColor(Color.WHITE)
                    txtEn.setTextColor(Color.BLACK)
                    card2.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    card1.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                }
            }

            card1.setOnClickListener {
                txtEn.setTextColor(Color.WHITE)
                txtRu.setTextColor(Color.BLACK)
                imageLan1.visibility = View.VISIBLE
                imageLan2.visibility = View.GONE
                card1.setCardBackgroundColor(Color.parseColor("#475AD7"))
                card2.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                LocalHelper().setLocale(root.context, "en")
                activity?.recreate()
            }

            card2.setOnClickListener {
                txtRu.setTextColor(Color.WHITE)
                txtEn.setTextColor(Color.BLACK)
                imageLan2.visibility = View.VISIBLE
                imageLan1.visibility = View.GONE
                card2.setCardBackgroundColor(Color.parseColor("#475AD7"))
                card1.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                LocalHelper().setLocale(root.context, "ru")
                activity?.recreate()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).show()
    }
}