package com.eunsong.opengl_es_practice
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent


class CustomGLSurfaceView(context: Context): GLSurfaceView(context) {

    private val renderer: CustomGLRenderer
    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private var mPreviousX = 0f
    private var mPreviousY = 0f
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        super.dispatchTouchEvent(event)
        Log.i("dispatchTouchEvent", "angle: ${renderer.angle}")
        event?.let { e ->
            val x = e.x
            val y = e.y
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {

                    var dx = x - mPreviousX
                    var dy = y - mPreviousY

                    // reverse direction of rotation above the mid-line
                    if (y > height / 2) {
                        dx = dx * -1
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < width / 2) {
                        dy = dy * -1
                    }
                    renderer.angle = renderer.angle + (dx + dy) * TOUCH_SCALE_FACTOR
                    requestRender()
                }
            }
            mPreviousX = x
            mPreviousY = y
            return true
        }
        return false
    }

    init {
        setEGLContextClientVersion(2)
        renderer = CustomGLRenderer()
        setRenderer(renderer)

        // requestRender 울릴 경우에만 화면을 다시 그리게 됩니다.
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}
