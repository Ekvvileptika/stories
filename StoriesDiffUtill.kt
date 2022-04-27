package az.myaccess.ui.activities.stories

import androidx.recyclerview.widget.DiffUtil
import az.myaccess.data.model.response.StoryModel

class StoriesDiffUtill(
    private val oldlist: ArrayList<StoryModel>,
    private val newlist: ArrayList<StoryModel>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldlist.size
    override fun getNewListSize(): Int = newlist.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition] == newlist[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition].id == newlist[newItemPosition].id
    }
}