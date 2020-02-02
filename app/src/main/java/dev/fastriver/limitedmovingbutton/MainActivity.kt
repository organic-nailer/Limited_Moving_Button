package dev.fastriver.limitedmovingbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.math.atan

class MainActivity : AppCompatActivity() {

    var touchGapY: Float = 0f
    var startRawY: Float = 0f

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
                    println("終了座標: ${limitedButton.y}")
                }
            }

            true
        }
    }
}

fun limiter(input: Float, limit: Float, variation: Float): Float {
    return (limit * atan(input * variation) * 2 / PI).toFloat()
}
