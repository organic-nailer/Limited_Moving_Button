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

    private lateinit var buttonGestureDetector: GestureDetector

    private var directionFlag: Boolean = true

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
                if(directionFlag && velocityY > 500){
                    originAnimation?.cancel()
                    println("2low")
                    move(500f)

                    directionFlag = !directionFlag
                }
                else if(!directionFlag && velocityY < -500) {
                    originAnimation?.cancel()
                    println("2high")
                    move(-500f)

                    directionFlag = !directionFlag
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
                    val direction = e.rawY - startRawY > 0 == directionFlag
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

    private fun move(distance: Float) {
        val moveAnimation = limitedButton?.let {
            SpringAnimation(it, DynamicAnimation.TRANSLATION_Y, distance)
        }

        moveAnimation?.run {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        }

        moveAnimation?.start()
    }
}

fun limiter(input: Float, limit: Float, variation: Float): Float {
    return (limit * atan(input * variation) * 2 / PI).toFloat()
}
