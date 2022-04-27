package az.myaccess.ui.activities.stories.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import az.myaccess.data.model.response.StoryModel


class StoriesPagerAdapter(
    val fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {
    private var currentList: ArrayList<StoryModel> = arrayListOf()

    fun emmitData(list: ArrayList<StoryModel>){
        currentList.clear()
        currentList.addAll(list)
    }

    override fun getItemCount(): Int = currentList.size

    override fun createFragment(position: Int): Fragment {
        val datalist = currentList[position]
        val targetFragment = StoriesFragment()
        val bndl = Bundle()
        bndl.putParcelable("storyItemList", datalist)
        targetFragment.arguments = bndl

        return targetFragment
    }
}