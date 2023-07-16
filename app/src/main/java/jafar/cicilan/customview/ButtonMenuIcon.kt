package jafar.cicilan.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import jafar.cicilan.R

class ButtonMenuIcon : FrameLayout {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
    }

    private fun setTitle(title: String) {
//        text = title
    }

    private fun setSubTtle(subtitle: String) {
//        text = subtitle
    }

    private fun setIcon(icon : Drawable) {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}