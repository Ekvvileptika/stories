package az.myaccess.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryModelMain(
    @SerializedName("data")
    val data: ArrayList<StoryModel>
): Parcelable

@Parcelize
data class StoryModel(
    @SerializedName("id")
    val id: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("medias")
    val medias: ArrayList<StoryModelUnit>,
    @SerializedName("createdDate")
    val createdDate: String? = ""
) : Parcelable

@Parcelize
data class StoryModelUnit(
    @SerializedName("id")
    val id: String?,
    @SerializedName("buttonText")
    val buttonText: String?,
    @SerializedName("media")
    val media: String?,
    @SerializedName("storyName")
    val storyName: String?,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("actionUrl")
    val actionUrl: String?
) : Parcelable

object StorySettings{
    val STORY_ALL_IMAGES = "STORY_ALL_IMAGES"
    val STORY_CURRENT_INDEX_KEY = "STORY_CURRENT_INDEX_KEY"
}