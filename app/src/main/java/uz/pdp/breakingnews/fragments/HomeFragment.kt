package uz.pdp.breakingnews.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import uz.pdp.breakingnews.App
import uz.pdp.breakingnews.R
import uz.pdp.breakingnews.adapters.RecAdapterNews
import uz.pdp.breakingnews.adapters.SliderAdapter
import uz.pdp.breakingnews.dagger.data.database.MyDatabaseHelper
import uz.pdp.breakingnews.dagger.data.entity.ArticleEntity
import uz.pdp.breakingnews.databinding.FragmentHomeBinding
import uz.pdp.breakingnews.databinding.ItemTabBinding
import uz.pdp.breakingnews.madels.languages.LocalHelper
import uz.pdp.breakingnews.utils.NewsResource
import uz.pdp.breakingnews.viewmodels.MyViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {

    private lateinit var binding : FragmentHomeBinding
    @Inject
    lateinit var myViewModel: MyViewModel
    @Inject
    lateinit var myDatabaseHelper: MyDatabaseHelper
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var list: ArrayList<ArticleEntity>
    private lateinit var listTab: java.util.ArrayList<String>
    private lateinit var recAdapterNews: RecAdapterNews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding.apply {
            val onAttach = LocalHelper().onAttach(requireContext())
            txt1.text = onAttach?.getText(R.string.home_txt1)
            text2.text = onAttach?.getText(R.string.home_txt2)
            recommendedTxt.text = onAttach?.getText(R.string.home_txt3)
            see.text = onAttach?.getText(R.string.home_txt4)
            loadTabs()
            sliderAdapter = SliderAdapter(requireContext(), object : SliderAdapter.OnClickSliderListener{
                override fun onClick(article: ArticleEntity) {
                    val bundle = Bundle()
                    bundle.putSerializable("art", article)
                    findNavController().navigate(R.id.articleFragment, bundle)
                }

                override fun onClickSave(article: ArticleEntity) {
                    launch {
                        if (article.isSave) {
                            myDatabaseHelper.getHelper().addArticle(article)
                        } else {
                            myDatabaseHelper.getHelper().deleteArticle(article)
                        }
                    }
                }

            })
            viewPager.adapter = sliderAdapter
            recAdapterNews = RecAdapterNews(requireContext(), object : RecAdapterNews.OnCardClicked{
                override fun onclick(articleEntity: ArticleEntity) {
                    val bundle = Bundle()
                    bundle.putSerializable("art", articleEntity)
                    findNavController().navigate(R.id.articleFragment, bundle)
                }
            })
            recycle.adapter = recAdapterNews

            launch {
                myViewModel.fetchNews().collect {
                    when(it) {

                        is NewsResource.Loading -> {

                        }
                        is NewsResource.Success -> {
                            monkeyAnim.visibility = View.GONE
                            list = ArrayList()
                            list.addAll(it.list)
                            setViewPager()
                        }
                        is NewsResource.Error -> {

                        }

                    }
                }
            }
            launch {
                myViewModel.fetchCategory("general").collect {
                    when(it) {
                        is NewsResource.Loading -> {

                        }
                        is NewsResource.Success -> {
                            monkeyAnim1.visibility = View.GONE
                            recAdapterNews.submitList(it.list)
                        }
                        is NewsResource.Error -> {

                        }

                    }
                }
            }

            searchView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    searchView.setCompoundDrawables(null, null, null,null)
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search_icon, 0)
                } else if (!hasFocus){
                    if (searchView.text.toString().isEmpty()) {
                        searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_icon, 0, R.drawable.ic_microphone_1, 0)
                    }
                }
            }

            see.setOnClickListener {
                val sheet = BottomSheetFragment.newInstance("general")
                sheet.show(childFragmentManager, "DemoBottomSheetFragment")
            }

            searchView.setOnTouchListener(object : View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_UP) {
                        val leftEdgeOfRightDrawable = searchView.right - searchView.compoundDrawables[2].bounds.width()
                        if (event.rawX >= leftEdgeOfRightDrawable && !searchView.isFocused) {
                            if (SpeechRecognizer.isRecognitionAvailable(requireContext())) {
                                Toast.makeText(
                                    requireContext(),
                                    "Speech recognition is not available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!")
                                getResult.launch(intent)
                            }
                            return true
                        } else if(event.rawX >= leftEdgeOfRightDrawable && searchView.isFocused) {
                            if (searchView.text.toString().isNotEmpty()) {
                                val sheet = BottomSheetFragment.newInstance(searchView.text.toString())
                                sheet.show(childFragmentManager, "DemoBottomSheetFragment")
                            } else {
                                Toast.makeText(requireContext(), "Write query", Toast.LENGTH_SHORT).show()
                            }
                            return false
                        }
                    }
                    return false
                }

            })

            setTab()

            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val itemTabBinding = ItemTabBinding.bind(tab!!.customView!!)
                    itemTabBinding.card.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    itemTabBinding.text.setTextColor(Color.WHITE)
                    launch {
                        myViewModel.fetchCategory(itemTabBinding.text.text.toString()).collect {
                            when(it) {
                                is NewsResource.Loading -> {
                                    monkeyAnim.visibility = View.VISIBLE
                                    viewPager.visibility = View.INVISIBLE
                                }
                                is NewsResource.Success -> {
                                    viewPager.visibility = View.VISIBLE
                                    monkeyAnim.visibility = View.GONE
                                    list = ArrayList()
                                    list.clear()
                                    list.addAll(it.list)
                                    sliderAdapter.submitList(list)
                                    sliderAdapter.notifyDataSetChanged()
                                }
                                is NewsResource.Error -> {

                                }

                            }
                        }
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val itemTabBinding = ItemTabBinding.bind(tab!!.customView!!)
                    itemTabBinding.card.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                    itemTabBinding.text.setTextColor(Color.parseColor("#7C82A1"))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }


        return binding.root
    }

    private fun setTab() {
        binding.apply {

            for (s in listTab) {
                tablayout.addTab(tablayout.newTab())
            }

            listTab.forEachIndexed { index, s ->
                val tab = tablayout.getTabAt(index)
                val itemTabBinding = ItemTabBinding.inflate(layoutInflater)
                itemTabBinding.text.text = s.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                if (index == 0) {
                    itemTabBinding.card.setCardBackgroundColor(Color.parseColor("#475AD7"))
                    itemTabBinding.text.setTextColor(Color.WHITE)
                } else {
                    itemTabBinding.card.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                    itemTabBinding.text.setTextColor(Color.parseColor("#7C82A1"))
                }
                tab?.customView = itemTabBinding.root
            }

            val tab = (tablayout.getChildAt(0) as ViewGroup).getChildAt(0)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = 50
            tab.layoutParams = layoutParams
            tablayout.requestLayout()
        }
    }


    private fun setViewPager() {
        binding.apply {

            sliderAdapter.submitList(list)

            viewPager.clipToPadding = false
            viewPager.clipChildren = false
            viewPager.offscreenPageLimit = 2
            viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(30))
            viewPager.setPageTransformer(compositePageTransformer)

        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val resultStr = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.searchView.requestFocus()
            binding.searchView.setText(resultStr?.get(0).toString())

        }
    }

    private fun loadTabs() {
        listTab = ArrayList()
        listTab.add("business")
        listTab.add("entertainment")
        listTab.add("general")
        listTab.add("health")
        listTab.add("science")
        listTab.add("sports")
        listTab.add("technology")
    }

    override val coroutineContext: CoroutineContext
        get() = Job()+Dispatchers.Main
}