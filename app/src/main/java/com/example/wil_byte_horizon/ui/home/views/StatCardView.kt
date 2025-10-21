package com.example.wil_byte_horizon.ui.home.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.example.wil_byte_horizon.R
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MaterialCardView(context, attrs, defStyle) {

    private val valueTv: TextView
    private val labelTv: TextView

    private var targetValue: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_stat_card_internal, this, true)
        valueTv = findViewById(R.id.txtValue)
        labelTv = findViewById(R.id.txtLabel)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
            labelTv.text = a.getString(R.styleable.StatCardView_labelText) ?: ""
            a.recycle()
        }
    }

    fun setLabel(text: CharSequence) {
        labelTv.text = text
    }

    fun setValueImmediate(value: Int) {
        targetValue = value
        valueTv.text = value.toString()
    }

    fun animateTo(value: Int, durationMs: Long = 700L, steps: Int = 40) {
        targetValue = value
        if (steps <= 0 || durationMs <= 0) {
            setValueImmediate(value)
            return
        }
        // Lightweight animation without coroutines
        val stepDelay = durationMs / steps
        var i = 0
        removeCallbacks(animRunnable)
        animRunnable = Runnable {
            val t = i / steps.toFloat()
            val eased = 1f - (1f - t) * (1f - t) // simple ease-out
            val current = (targetValue * eased).roundToInt()
            valueTv.text = current.toString()
            i++
            if (i <= steps) {
                postDelayed(animRunnable, stepDelay)
            } else {
                valueTv.text = targetValue.toString()
            }
        }
        post(animRunnable)
    }

    private var animRunnable: Runnable = Runnable {}
}
