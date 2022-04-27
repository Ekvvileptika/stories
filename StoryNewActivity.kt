package az.myaccess.ui.activities.stories

import android.os.Bundle
import az.myaccess.R
import az.myaccess.ui.activities.stories.fragment.CubeTransformer
import androidx.viewpager2.widget.ViewPager2
import az.myaccess.BR
import az.myaccess.data.model.response.StoryModel
import az.myaccess.data.model.response.StorySettings
import az.myaccess.databinding.ActivityStoryBinding
import az.myaccess.di.component.ActivityComponent
import az.myaccess.ui.activities.forgetPassword.ForgetPasswordViewModel
import az.myaccess.ui.activities.stories.fragment.StoriesPagerAdapter
import az.myaccess.ui.core.BaseActivity


class StoryNewActivity :  BaseActivity<ActivityStoryBinding, ForgetPasswordViewModel>() {
    private lateinit var pager: ViewPager2
    private var list: java.util.ArrayList<StoryModel> = arrayListOf()
    private var mainstorycposition = 0

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_story

    override fun performDependencyInjection(buildComponent: ActivityComponent?) {
        buildComponent!!.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_story)

        //get data
        val adapter = StoriesPagerAdapter(this)
        list = intent.getParcelableArrayListExtra<StoryModel>(StorySettings.STORY_ALL_IMAGES) as java.util.ArrayList<StoryModel>
        mainstorycposition = intent?.getIntExtra(StorySettings.STORY_CURRENT_INDEX_KEY, 0) ?: 0
        adapter.emmitData(list)

        pager = findViewById(R.id.storypager)
        pager.adapter = adapter
        pager.setCurrentItem(mainstorycposition)
        pager.setPageTransformer( CubeTransformer() )

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                mainstorycposition = position
            }
        })
    }

    private fun openAgainActivity() {
        /*if (currentPosition >= 0 && currentPosition <= currentList.size - 1) {
            val intent = Intent(this, StoryNewActivity::class.java)
            intent.putParcelableArrayListExtra(StorySettings.STORY_ALL_IMAGES, currentList)
            intent.putExtra(StorySettings.STORY_CURRENT_INDEX_KEY, currentPosition)
            startActivity(intent)
        }*/
    }

    fun movetonextpaging(){
        pager.setCurrentItem(mainstorycposition, true)
    }

    fun getSliderSize(): Int = list.size
    fun getSelectedItemPosition(): Int = mainstorycposition
    fun getSelectedItemIncrement(){
        mainstorycposition += 1
    }

    fun closeActivity(){
        finish()
    }

    override fun onFragmentDetached(tag: String?) {

    }
}