package dev.fastriver.limitedmovingbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.atan

class MainActivity : AppCompatActivity() {

    private lateinit var buttonGestureDetector: GestureDetector

    private var touchGapY: Float = 0f
    private var startRawY: Float = 0f

    private var originAnimation: SpringAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGestureListener = object: GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                println("速度: Y=$velocityY")
                if(velocityY > 500){
                    originAnimation?.cancel()
                    hideByFlick()
                }

                return super.onFling(e1, e2, velocityX, velocityY)
            }
        }

        buttonGestureDetector = GestureDetector(this, buttonGestureListener)

        limitedButton.setOnTouchListener { _, e ->
            when(e?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchGapY = e.rawY - limitedButton.y
                    startRawY = e.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val direction = e.rawY - startRawY > 0
                    if(direction) {
                        limitedButton.y = e.rawY - touchGapY
                    }
                    else {
                        limitedButton.y = startRawY - touchGapY - limiter(startRawY - e.rawY, 100f, 0.002f)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    goBack2MyRoots(startRawY - touchGapY)
                }
            }

            buttonGestureDetector.onTouchEvent(e)
        }

        showWithDelay()
    }

    private fun goBack2MyRoots(root: Float) {
        originAnimation = limitedButton?.let {
            SpringAnimation(it, DynamicAnimation.Y, root)
        }

        originAnimation?.run {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        }

        originAnimation?.start()
    }

    private fun hideByFlick() {

        val viewHeight = limitedButton?.height ?: 0
        val buttonMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f+16f, resources.displayMetrics)

        val moveAnimation = limitedButton?.let {
            SpringAnimation(it, DynamicAnimation.Y, startRawY + viewHeight + buttonMargin)
        }

        moveAnimation?.run {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        }

        moveAnimation?.start()

        GlobalScope.launch {
            delay(3000)

            showWithDelay()
        }
    }

    private fun showWithDelay() {
        GlobalScope.launch {
            delay(1000)

            val viewHeight = limitedButton?.height ?: 0
            val buttonMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f+16f, resources.displayMetrics)

            val moveAnimation = limitedButton?.let {
                SpringAnimation(it, DynamicAnimation.TRANSLATION_Y,  -viewHeight -buttonMargin)
            }

            moveAnimation?.run {
                spring.stiffness = SpringForce.STIFFNESS_LOW
                spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            }
            withContext(Dispatchers.Main) {
                moveAnimation?.start()
            }
        }
    }
}

fun limiter(input: Float, limit: Float, variation: Float): Float {
    return (limit * atan(input * variation) * 2 / PI).toFloat()
}
