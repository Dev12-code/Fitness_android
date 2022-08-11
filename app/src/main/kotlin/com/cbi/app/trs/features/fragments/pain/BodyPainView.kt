package com.cbi.app.trs.features.fragments.pain

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.cbi.app.trs.core.extension.dp2px
import com.cbi.app.trs.core.interactor.DoAsync
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.features.utils.AppLog

class BodyPainView : CardView, View.OnTouchListener {
    private var isLock: Boolean = false
    private var imageResBitmap: ArrayList<Bitmap> = ArrayList()
    private var painAreaList: ArrayList<SystemData.PainArea> = ArrayList()

    var onItemClickListener: OnItemClickListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            outlineSpotShadowColor = Color.parseColor("#2d00ff")
        }
        elevation = 5.dp2px.toFloat()
    }

    fun addBaseImage(imageResId: Int) {
        removeAllViews()
        addView(ImageView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(20, 0, 20, 0)
            setImageResource(imageResId)
            setOnTouchListener(this@BodyPainView)
        })
    }

    fun addBodyParts(painAreaList: ArrayList<SystemData.PainArea>) {
        this.painAreaList = painAreaList
        for (bitmap in imageResBitmap) {
            bitmap.recycle()
        }
        imageResBitmap.clear()
        addView(ImageView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(20, 0, 20, 0)
            visibility = View.INVISIBLE
        })
        DoAsync {
            decodeBitmap(painAreaList)
        }
    }

    fun addHighlightImage(imageResId: Int) {
        addView(ImageView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(20, 0, 20, 0)
            setImageResource(imageResId)
        })
    }

    private fun decodeBitmap(painAreaList: ArrayList<SystemData.PainArea>) {
        for (painPart in painAreaList)
            getImageResID(painPart)?.let { imageResBitmap.add(BitmapFactory.decodeResource(resources, it)) }
    }

    private fun getImageResID(painPart: SystemData.PainArea): Int? {
        return try {
            context.resources.getIdentifier(painPart.pain_area_key, "drawable", context.packageName)
        } catch (e: Exception) {
            null
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (isLock) return false
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val eventX = event.x
            val eventY = event.y
            val eventXY = floatArrayOf(eventX, eventY)

            val invertMatrix = Matrix()
            (v as ImageView).imageMatrix.invert(invertMatrix)

            invertMatrix.mapPoints(eventXY)
            val x = Integer.valueOf(eventXY[0].toInt())
            val y = Integer.valueOf(eventXY[1].toInt())

            selectedChildView(x, y)

            AppLog.e("Duy", "pixel at $x/$y")
            return true
        }

        if (event?.action == MotionEvent.ACTION_UP) {
            getChildAt(1).visibility = View.GONE
            bestIndex?.let {
                getChildAt(1).visibility = View.VISIBLE
                playAnimation(getChildAt(1) as ImageView)
                isLock = true
                Handler().postDelayed({
                    isLock = false
                    getChildAt(1).visibility = View.GONE
                    onItemClickListener?.onItemClick(painAreaList[it], it)
                }, 810)
            }
        }
        return false
    }

    private var bestIndex: Int? = null

    private fun selectedChildView(x: Int, y: Int) {
        if (x < 0 || y < 0) return
        try {
            var bestAlpha = 10
            var bestBitmap: Bitmap? = null
            bestIndex = null

            for ((index, bitmap) in imageResBitmap.withIndex()) {
                var alpha = 0xFF and (bitmap.getPixel(x, y) shr 24)

                if (alpha > bestAlpha) {
                    AppLog.e("Duy", "Alpha = $alpha")
                    bestAlpha = alpha
                    bestBitmap = bitmap
                    bestIndex = index
                }
            }

            bestBitmap?.let {
                (getChildAt(1) as ImageView).setImageBitmap(bestBitmap)
            }
        } catch (e: Exception) {

        }
    }

    fun onDestroy() {
        for (bitmap in imageResBitmap) {
            bitmap.recycle()
        }
    }

    private fun playAnimation(image: ImageView) {
        val animator = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f)
        animator.duration = 200
        animator.repeatCount = 4
        animator.start()
    }

    private fun playLoopAnimation(image: ImageView) {
        val animator = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f)
        animator.repeatCount = Animation.INFINITE
        animator.start()
    }
}