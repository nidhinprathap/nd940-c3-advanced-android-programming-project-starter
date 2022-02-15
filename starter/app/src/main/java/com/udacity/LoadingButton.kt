package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var btnWidthSize = 0
    private var btnHeightSize = 0
    private var lblWidth = 0f

    private var lblTextSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var circleXOffset = lblTextSize / 2

    private lateinit var buttonTitle: String

    private var progressWidth = 0f
    private var progressCircle = 0f

    private var buttonDefaultColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var btnProgressColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var progressBarColor = ContextCompat.getColor(context, R.color.colorAccent)

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                setButtonText(context.getString(R.string.clicked))
                invalidate()
            }
            ButtonState.Loading -> {
                setButtonText(resources.getString(R.string.button_loading))
                startDownloadAnimation()

            }
            ButtonState.Completed -> {
                setButtonText(resources.getString(R.string.button_download))
                onDownloadComplete()
                invalidate()
            }
        }

    }

    private fun onDownloadComplete() {
        valueAnimator.cancel()
        progressWidth = 0f
        progressCircle = 0f
    }

    private fun startDownloadAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0f, btnWidthSize.toFloat())
        valueAnimator.duration = 5000
        valueAnimator.addUpdateListener { animation ->
            progressWidth = animation.animatedValue as Float
            progressCircle = (btnWidthSize.toFloat() / 365) * progressWidth
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                progressWidth = 0f
                if (buttonState == ButtonState.Loading) {
                    buttonState = ButtonState.Loading
                }
            }
        })
        valueAnimator.start()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // UPDATE BUTTON COLOR
        setButtonBGColor(canvas)
        //UPDATE BUTTON PROGRESS
        setProgressOnButton(canvas)
        //UPDATE PROGRESS
        updateProgress(canvas)
        // ADD TEXT TO BUTTON
        drawTextOnButton(canvas)
    }

    private fun setButtonBGColor(canvas: Canvas?) {
        paint.color = buttonDefaultColor
        canvas?.drawRect(0f, 0f, btnWidthSize.toFloat(), btnHeightSize.toFloat(), paint)
    }

    private fun setProgressOnButton(canvas: Canvas?) {
        paint.color = btnProgressColor
        canvas?.drawRect(0f, 0f, progressWidth, btnHeightSize.toFloat(), paint)
    }

    private fun updateProgress(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(
            btnWidthSize / 2 + lblWidth / 2 + circleXOffset,
            btnHeightSize / 2 - lblTextSize / 2
        )
        paint.color = progressBarColor
        canvas?.drawArc(
            RectF(0f, 0f, lblTextSize, lblTextSize),
            0F,
            progressCircle * 0.365f,
            true,
            paint
        )
        canvas?.restore()
    }

    private fun drawTextOnButton(canvas: Canvas?) {
        paint.color = Color.WHITE
        lblWidth = paint.measureText(buttonTitle)
        canvas?.drawText(
            buttonTitle,
            btnWidthSize / 2 - lblWidth / 2,
            btnHeightSize / 2 - (paint.descent() + paint.ascent()) / 2,
            paint
        )
    }

    private fun setButtonText(text: String) {
        buttonTitle = text
    }

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        btnWidthSize = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        btnHeightSize = resolveSizeAndState(
            MeasureSpec.getSize(btnWidthSize),
            heightMeasureSpec,
            0
        )
        setMeasuredDimension(btnWidthSize, btnHeightSize)
    }

    init {
        setButtonText(resources.getString(R.string.button_download))
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            // SETTING DEFAULT VALUES TO STYLE ATTRIBUTES
            buttonDefaultColor = getColor(R.styleable.LoadingButton_buttonDefaultColor, 0)
            btnProgressColor = getColor(R.styleable.LoadingButton_btnProgressColor, 0)
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
        }
    }

}