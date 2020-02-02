package dev.fastriver.limitedmovingbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.math.atan

class MainActivity : AppCompatActivity() {

    private var touchGapY: Float = 0f
    private var startRawY: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        limitedButton.setOnTouchListener { _, e ->
            when(e?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchGapY = e.rawY - limitedButton.y
                    startRawY = e.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    //limitedButton.y = e.rawY - touchGapY
                    limitedButton.y = startRawY - touchGapY - limiter(startRawY - e.rawY, 100f, 0.002f)
                }

                MotionEvent.ACTION_UP -> {
                    goBack2MyRoots(startRawY - touchGapY)
                }
            }

            true
        }
    }

    fun goBack2MyRoots(root: Float) {
        val originAnimation = limitedButton?.let {
            SpringAnimation(it, DynamicAnimation.Y, root)
        }

        originAnimation?.run {
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            spring.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        }

        originAnimation?.start()
    }

}

fun limiter(input: Float, limit: Float, variation: Float): Float {
    return (limit * atan(input * variation) * 2 / PI).toFloat()
}
