package az.myaccess.ui.activities.stories.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.navigation.NavController
import az.myaccess.BR
import az.myaccess.R
import az.myaccess.databinding.ActivityStoryNewBinding
import az.myaccess.di.component.FragmentComponent
import az.myaccess.ui.activities.exchange.adapter.fragments.ExchangeListViewModel
import az.myaccess.data.model.response.StoryModel
import az.myaccess.ui.activities.stories.StoryNewActivity
import az.myaccess.ui.activities.stories.customegment.SegmentedProgressBar
import az.myaccess.ui.core.BaseFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import az.myaccess.ui.activities.stories.customegment.CompletedSegmentListener
import az.myaccess.ui.components.loader.LoaderView
import az.myaccess.utils.getFormatedDate
import timber.log.Timber

class StoriesFragment: BaseFragment<ActivityStoryNewBinding, ExchangeListViewModel>(){
    private lateinit var storiesProgressView: SegmentedProgressBar
    private var simplePlayer: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null
    private var progressBar: LoaderView? = null
    private var imageView: ImageView? = null
    private var closeImage: ImageView? = null
    private var logoImage: ImageView? = null
    private var gotostory: Button? = null

    private var title: TextView? = null
    private var desc: TextView? = null

    private var currentPosition: Int = 0
    private var currentList: StoryModel? = null
    private var pressTime = 0L
    private var limit = 500L

    private var currentsegmenttime = 50_00L

    private var requestOptions = RequestOptions()
    override val navController: NavController?
        get() = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_story_new

    override fun performDependencyInjection(buildComponent: FragmentComponent?) {
        buildComponent?.inject(this)
    }

    override fun onResume() {
        super.onResume()

        if (currentList?.medias?.size ?: 0 > 0) {
            listManipulator()


            //storiesProgressView.playSegment(currentsegmenttime)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        simplePlayer = let { SimpleExoPlayer.Builder(requireContext()).build() }

        arguments?.let {
            currentList = it.getParcelable("storyItemList")
        }

        storiesProgressView = viewDataBinding?.stories!!
        progressBar = viewDataBinding?.progressBar
        imageView = viewDataBinding?.slideimage
        playerView = viewDataBinding?.playerView
        closeImage = viewDataBinding?.closeImage
        logoImage = viewDataBinding?.logoImage
        title = viewDataBinding?.title
        desc = viewDataBinding?.desc
        gotostory = viewDataBinding?.gotostory

        //count
        storiesProgressView.setSegmentCount( currentList?.medias?.size!! )

        //listener
        storiesProgressView.setCompletedSegmentListener(object : CompletedSegmentListener {
            override fun onSegmentCompleted(segmentCount: Int) {
                gotoNextPage()
            }
        })

        // bind reverse view -> click
        val reverse = viewDataBinding?.prev
        reverse?.setOnClickListener {
            currentPosition--
            storiesProgressView.setCompletedSegments(currentPosition)
            listManipulator()
        }
        reverse?.setOnTouchListener(onTouchListener)

        // bind skip view -> click
        val skip = viewDataBinding?.next
        skip?.setOnClickListener {
            gotoNextPage()
        }
        skip?.setOnTouchListener(onTouchListener)

        //close
        viewDataBinding?.closeImage?.setOnClickListener {
            (context as StoryNewActivity).closeActivity()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                simplePlayer?.pause()

                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                simplePlayer?.play()
                return@OnTouchListener limit < now - pressTime
            }
        }

        false
    }

    private fun listManipulator() {
        storiesProgressView.pause()
        try {
            currentList?.medias?.let {
                //duration
                currentsegmenttime = it[currentPosition].duration
                //show action button
                if(it[currentPosition].actionUrl != null && it[currentPosition].actionUrl?.isNotEmpty() == true) {
                    gotostory?.isVisible = true
                    gotostory?.text = it[currentPosition].buttonText
                }

                val item = it[currentPosition].media
                val imageLogo = currentList?.logo
                val slidetitle = it[currentPosition].storyName
                val slidedesc =  currentList?.createdDate?.getFormatedDate("dd.MM.yyyy")

                logoImage?.let {
                    val requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(60))
                    Glide.with(this).load(imageLogo)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .apply(requestOptions)
                        .into(it)
                }

                title?.text = slidetitle
                desc?.text = slidedesc

                when {
                    //video player
                    item?.contains(".mp4") == true -> {
                        imageView?.isVisible = false
                        playerView?.isVisible = true

                        if (item != "") {
                            val mediaItem = MediaItem.fromUri(item.toString())
                            playerView?.player = simplePlayer
                            simplePlayer?.setMediaItem(mediaItem)
                            simplePlayer?.prepare()

                            simplePlayer?.addListener(object : Player.Listener {
                                override fun onPlaybackStateChanged(state: Int) {
                                    when (state) {
                                        Player.STATE_BUFFERING -> {
                                            storiesProgressView.pause()
                                            playerView?.visibility = View.GONE
                                            progressBar?.visibility = View.VISIBLE
                                        }
                                        Player.STATE_IDLE -> {
                                            storiesProgressView.pause()
                                            playerView?.visibility = View.GONE
                                            progressBar?.visibility = View.VISIBLE
                                        }
                                        Player.STATE_READY -> {
                                            storiesProgressView.resume()
                                            progressBar?.visibility = View.GONE
                                            playerView?.visibility = View.VISIBLE
                                            simplePlayer?.play()
                                            storiesProgressView.playSegment(currentsegmenttime)
                                        }

                                        else -> {
                                            storiesProgressView.pause()
                                        }
                                    }
                                }

                                override fun onPlayerError(error: ExoPlaybackException) {
                                    when (error.type) {
                                        ExoPlaybackException.TYPE_SOURCE -> Timber.e("TYPE_SOURCE: ${error.sourceException.message}")
                                        ExoPlaybackException.TYPE_RENDERER -> Timber.e("TYPE_RENDERER: ${error.rendererException.message}")
                                        ExoPlaybackException.TYPE_UNEXPECTED -> Timber.e("TYPE_UNEXPECTED: ${error.unexpectedException.message}")
                                        ExoPlaybackException.TYPE_REMOTE -> Timber.e("TYPE_REMOTE")
                                    }
                                }
                            })
                        } else {

                        }
                    }

                    //only image show
                    else -> {
                        storiesProgressView.playSegment(currentsegmenttime)
                        simplePlayer?.stop()
                        playerView?.isVisible = false
                        progressBar?.isVisible = false
                        imageView?.isVisible = true

                        imageView?.let {
                            Glide.with(this).load(item)
                                .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(it)
                        }
                    }
                }

                //click action
                gotostory?.setOnClickListener { view ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it[currentPosition].actionUrl)
                    startActivity(intent)
                }
            }
        }catch (ex: Exception){
            (context as StoryNewActivity).closeActivity()
        }
    }
/*    override fun onDown(e: MotionEvent?): Boolean = false
    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean = false
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //open description in ...
        if (distanceY > 0){
            //val link = storiesResources[counter].info


           *//* val link = currentList[currentPosition].stories[0].mediaUrl

            intent.putExtra("link", link)
            setResult(RESULT_OK, intent)

            finish()
*//*
        }

        return true
    }

    override fun onLongPress(e: MotionEvent?) { }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false*/

    //go to next page
    private fun gotoNextPage(){
        //check size
        if(currentPosition + 1 == currentList?.medias?.size!!){
            //complete play whole story
            val summary = (context as StoryNewActivity).getSliderSize()
            val position = (context as StoryNewActivity).getSelectedItemPosition()

            if(summary > position + 1){
                //increment next step
                (context as StoryNewActivity).getSelectedItemIncrement()

                //nexpage
                (context as StoryNewActivity).movetonextpaging()
            } else {
                (context as StoryNewActivity).closeActivity()
            }
        } else {
            currentPosition++
            storiesProgressView.setCompletedSegments(currentPosition)
            listManipulator()
        }
    }

    override fun onPause() {
        super.onPause()

        currentPosition = 0
        storiesProgressView.reset()
        simplePlayer?.playWhenReady = false
        simplePlayer?.stop()
    }



}
