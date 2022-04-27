package az.myaccess.ui.activities.stories

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import az.myaccess.data.model.response.StoryModel
import az.myaccess.databinding.StoryRecyclerItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.story_recycler_item.view.*

class StoriesAdapter(
    private val listener: (StoryModel, Int) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    private val storiesList: ArrayList<StoryModel> = ArrayList()
    private lateinit var context: Context
    private var requestOptions = RequestOptions()
    private val roundedCorenerRadisu = 16

    fun setImagesList(newList: ArrayList<StoryModel>) {
        val du = StoriesDiffUtill(storiesList, newList)
        val dc = DiffUtil.calculateDiff(du)

        storiesList.clear()
        storiesList.addAll(newList)

        dc.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = StoryRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(roundedCorenerRadisu))
        context = parent.context
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(storiesList[position])
    }

    override fun getItemCount(): Int = storiesList.count()

    inner class ViewHolder(itemView: StoryRecyclerItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindData(imageUris: StoryModel) {

            Glide.with(context)
                .load(imageUris.logo)
                .apply(requestOptions)
                .into(itemView.storyViewImage)

            itemView.setOnClickListener {
                listener.invoke(storiesList[adapterPosition], adapterPosition)
            }
        }
    }
}