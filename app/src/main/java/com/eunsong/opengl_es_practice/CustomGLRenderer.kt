package com.eunsong.opengl_es_practice

//import android.opengl.GLES20
//import android.opengl.GLSurfaceView
//import android.opengl.Matrix
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10

//package com.eunsong.opengl_es_practice

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.eunsong.opengl_es_practice.shape.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class CustomGLRenderer: GLSurfaceView.Renderer {

    private var triangle: Triangle? = null

    @Volatile
    var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        triangle = Triangle()

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    // GLSurfaceView 크기 변경, 디바이스 화면의 방향전환 등으로 인한
    // geometry 가 바뀔때 호출되는 메소드
    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        //viewport를 설정합니다.
        //specifies the affine transformation of x and y from
        //normalized device coordinates to window coordinates
        //viewport rectangle의 왼쪽 아래를 (0,0)으로 지정하고
        //viewport의 width와 height를 지정합니다.
        GLES20.glViewport(0, 0, width, height)
        //GLSurfaceView 너비와 높이 사이의 비율을 계산합니다.
        val ratio = width.toFloat() / height

        //3차원 공간의 점을 2차원 화면에 보여주기 위해 사용되는 projection matrix를 정의
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)


    // 다시 그려질 떄마다 호출되는 메소드
   override fun onDrawFrame(gl: GL10?) {
        // glClearColor() 에서 설정한 값으로 color buffer를 클리어합니다.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)


        //카메라 위치를 나타내는 Camera view matirx를 정의

        // type 1 - 삼각형
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // type 2 - 삼각뿔용
//        Matrix.setLookAtM(mViewMatrix, 0, 3f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)


        //projection matrix와 camera view matrix를 곱하여 mMVPMatrix 변수에 저장
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        val scratch = FloatArray(16)
        val mRotationMatrix = FloatArray(16)

        Log.i("Angle","$angle")
        //mAngle 각도값을 이용하여 (x,y,z)=(0,0,-1) 축 주위를 회전하는 회전 matrix를 정의합니다.
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, -1.0f)

        //projection matrix와 camera view matrix를 곱하여 얻은 matrix인 mMVPMatrix와
        //회전 matrix mRotationMatrix를 결합합니다.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)

//        triangle?.draw(mMVPMatrix)
        // 회전 적용
        triangle?.draw(scratch)

    }

    companion object{

        fun loadShader(type: Int, shaderCode: String?): Int {

            // 다음 2가지 타입 중 하나로 shader객체를 생성한다.
            // vertex shader type (GLES20.GL_VERTEX_SHADER)
            // 또는 fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            val shader = GLES20.glCreateShader(type)

            // shader객체에 shader source code를 로드합니다.
            GLES20.glShaderSource(shader, shaderCode)

            //shader객체를 컴파일 합니다.
            GLES20.glCompileShader(shader)
            return shader
        }


    }
}

//class CustomGLRenderer : GLSurfaceView.Renderer {
//    private var mTriangle: Triangle? = null
//
//    @Volatile
//    var angle = 0f
//
//    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
//    private val mMVPMatrix = FloatArray(16)
//    private val mProjectionMatrix = FloatArray(16)
//    private val mViewMatrix = FloatArray(16)
//
//    //GLSurfaceView가 생성되었을때 한번 호출되는 메소드입니다.
//    //OpenGL 환경 설정, OpenGL 그래픽 객체 초기화 등과 같은 처리를 할때 사용됩니다.
//    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
//        //shape가 정의된 com.eunsong.opengl_es_practice.shape.Triangle 클래스의 인스턴스를 생성합니다.
//        mTriangle = Triangle()
//
//        //color buffer를 클리어할 때 사용할 색을 지정합니다.
//        //red, green, blue, alpha 순으로 0~1사이의 값을 지정합니다.
//        //여기에서는 검은색으로 지정하고 있습니다.
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//    }
//
//    //GLSurfaceView가 다시 그려질때 마다 호출되는 메소드입니다.
//    override fun onDrawFrame(unused: GL10) {
//        //glClearColor에서 설정한 값으로 color buffer를 클리어합니다.
//        //glClear메소드를 사용하여 클리어할 수 있는 버퍼는 다음 3가지 입니다.
//        //Color buffer (GL_COLOR_BUFFER_BIT)
//        //depth buffer (GL_DEPTH_BUFFER_BIT)
//        //stencil buffer (GL_STENCIL_BUFFER_BIT)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//
//        //카메라 위치를 나타내는 Camera view matirx를 정의
//        Matrix.setLookAtM(mViewMatrix, 0, 3f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
//
//        //projection matrix와 camera view matrix를 곱하여 mMVPMatrix 변수에 저장
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
//        val scratch = FloatArray(16)
//        val mRotationMatrix = FloatArray(16)
//
//        //mAngle 각도값을 이용하여 (x,y,z)=(0,0,-1) 축 주위를 회전하는 회전 matrix를 정의합니다.
//        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, -1.0f)
//
//        //projection matrix와 camera view matrix를 곱하여 얻은 matrix인 mMVPMatrix와
//        //회전 matrix mRotationMatrix를 결합합니다.
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)
//
//        //triangle를 그리는 처리를 하고 있는 draw메소드에 scratch 변수를 넘겨준다.
////        mTriangle!!.draw(mMVPMatrix)
//        mTriangle!!.draw(scratch)
//    }
//
//    //GLSurfaceView의 크기 변경 또는 디바이스 화면의 방향 전환 등으로 인해
//    //GLSurfaceView의 geometry가 바뀔때 호출되는 메소드입니다.
//    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
//        //viewport를 설정합니다.
//        //specifies the affine transformation of x and y from
//        //normalized device coordinates to window coordinates
//        //viewport rectangle의 왼쪽 아래를 (0,0)으로 지정하고
//        //viewport의 width와 height를 지정합니다.
//        GLES20.glViewport(0, 0, width, height)
//        //GLSurfaceView 너비와 높이 사이의 비율을 계산합니다.
//        val ratio = width.toFloat() / height
//
//        //3차원 공간의 점을 2차원 화면에 보여주기 위해 사용되는 projection matrix를 정의
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
//    }
//
//    companion object {
//        fun loadShader(type: Int, shaderCode: String?): Int {
//
//            // 다음 2가지 타입 중 하나로 shader객체를 생성한다.
//            // vertex shader type (GLES20.GL_VERTEX_SHADER)
//            // 또는 fragment shader type (GLES20.GL_FRAGMENT_SHADER)
//            val shader = GLES20.glCreateShader(type)
//
//            // shader객체에 shader source code를 로드합니다.
//            GLES20.glShaderSource(shader, shaderCode)
//
//            //shader객체를 컴파일 합니다.
//            GLES20.glCompileShader(shader)
//            return shader
//        }
//    }
//}