package com.cbi.app.trs.core.extension

import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.cbi.app.trs.R

fun View.cancelTransition() {
    transitionName = null
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.extendTouch() {
    val parent = this.parent as View
    val extraSpace = 10.dp2px
    parent.post {
        val touchableArea = Rect()
        this.getHitRect(touchableArea)
        touchableArea.top -= extraSpace
        touchableArea.bottom += extraSpace
        touchableArea.left -= extraSpace
        touchableArea.right += extraSpace
        parent.touchDelegate = TouchDelegate(touchableArea, this)
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun ImageView.loadFromUrl(
    url: String?,
    isAnimation: Boolean = false,
    isPlaceHolder: Boolean = false
) {
    if (url == null) return
    val requestBuilder = Glide.with(this.context.applicationContext).load(url).centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    if (isAnimation) requestBuilder.transition(DrawableTransitionOptions.withCrossFade())
    if (isPlaceHolder) requestBuilder.placeholder(R.drawable.place_holder) else requestBuilder.placeholder(
        null
    )
    requestBuilder.into(this)
}

fun ImageView.loadFromUrlFit(
    url: String?,
    isAnimation: Boolean = false,
    isPlaceHolder: Boolean = false
) {
    if (url == null) return
    val requestBuilder = Glide.with(this.context.applicationContext).load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    if (isAnimation) requestBuilder.transition(DrawableTransitionOptions.withCrossFade())
    if (isPlaceHolder) requestBuilder.placeholder(R.drawable.place_holder) else requestBuilder.placeholder(
        null
    )
    requestBuilder.into(this)
}

fun ImageView.loadFromLocal(
    resourceId: Int?,
    isAnimation: Boolean = false,
    isPlaceHolder: Boolean = true,
    resourceIdHolder: Int = R.drawable.place_holder
) {
    if (resourceId == null) return
    val requestBuilder =
        Glide.with(this.context.applicationContext).load(resourceId).centerCrop().thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    if (isAnimation) requestBuilder.transition(DrawableTransitionOptions.withCrossFade())
    if (isPlaceHolder) requestBuilder.placeholder(resourceIdHolder) else requestBuilder.placeholder(
        null
    )
    requestBuilder.into(this)
}


fun ImageView.loadUrlAndPostponeEnterTransition(url: String, activity: FragmentActivity) {
    val target: Target<Drawable> = ImageViewBaseTarget(this, activity)
    Glide.with(context.applicationContext).load(url).into(target)
}

val Int.px2dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dp2px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

private class ImageViewBaseTarget(var imageView: ImageView?, var activity: FragmentActivity?) :
    BaseTarget<Drawable>() {
    override fun removeCallback(cb: SizeReadyCallback) {
        imageView = null
        activity = null
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        imageView?.setImageDrawable(resource)
        activity?.supportStartPostponedEnterTransition()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        activity?.supportStartPostponedEnterTransition()
    }

    override fun getSize(cb: SizeReadyCallback) = cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL)
}
