package com.eunsong.opengl_es_practice.shape

import android.opengl.GLES20
import com.eunsong.opengl_es_practice.CustomGLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Triangle {

    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer? = null

    private val vertexShaderCode =  // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +  // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}"
    private val fragmentShaderCode = ("precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}")

    private val program: Int

    init {


        //1.ByteBuffer를 할당 받습니다.
        val byteBuffer = ByteBuffer.allocateDirect( // (number of coordinate values * 4 bytes per float)
                triangleCoords.size * 4)

        //2. ByteBuffer에서 사용할 엔디안을 지정합니다.
        //버퍼의 byte order로써 디바이스 하드웨어의 native byte order를 사용
        byteBuffer.order(ByteOrder.nativeOrder())


        //3. ByteBuffer를 FloatBuffer로 변환합니다.
        vertexBuffer = byteBuffer.asFloatBuffer().apply {

            //4. float 배열에 정의된 좌표들을 FloatBuffer에 저장합니다.
            put(triangleCoords)

            //5. 읽어올 버퍼의 위치를 0으로 설정한다. 첫번째 좌표부터 읽어오게됨
            position(0)
        }


        //vertex shader 타입의 객체를 생성하여 vertexShaderCode에 저장된 소스코드를 로드한 후,
        //   컴파일합니다.
        val vertexShader: Int = com.eunsong.opengl_es_practice.CustomGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode)

        //fragment shader 타입의 객체를 생성하여 fragmentShaderCode에 저장된 소스코드를 로드한 후,
        //  컴파일합니다.
        val fragmentShader: Int = com.eunsong.opengl_es_practice.CustomGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode)

        // Program 객체를 생성한다.
        program = GLES20.glCreateProgram()

        // vertex shader를 program 객체에 추가
        GLES20.glAttachShader(program, vertexShader)

        // fragment shader를 program 객체에 추가
        GLES20.glAttachShader(program, fragmentShader)

        // program객체를 OpenGL에 연결한다. program에 추가된 shader들이 OpenGL에 연결된다.
        GLES20.glLinkProgram(program)
    }

    private var mPositionHandle = 0
    private var mColorHandle = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var mMVPMatrixHandle = 0

    fun draw(mvpMatrix: FloatArray?) {
        //렌더링 상태(Rendering State)의 일부분으로 program을 추가한다.
        GLES20.glUseProgram(program)

        // program 객체로부터 vertex shader의'vPosition 멤버에 대한 핸들을 가져옴
        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition")

        //triangle vertex 속성을 활성화 시켜야 렌더링시 반영되서 그려짐
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // triangle vertex 속성을 vertexBuffer에 저장되어 있는 vertex 좌표들로 정의한다.
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)


        // program 객체로부터 fragment shader의 vColor 멤버에 대한 핸들을 가져옴
        mColorHandle = GLES20.glGetUniformLocation(program, "vColor")

        //triangle 렌더링시 사용할 색으로 color변수에 정의한 값을 사용한다.
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)

        //program 객체로부터 vertex shader 타입의 객체에 정의된 uMVPMatrix에 대한 핸들을 획득합니다.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        //projection matrix와 camera view matrix를 곱하여 얻어진  mMVPMatrix 변수의 값을
        // vertex shader 객체에 선언된 uMVPMatrix 멤버에게로 넘겨줍니다.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0) //TODO

        //vertex 갯수만큼 triangle 을 렌더링한다.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        //vertex 속성을 비활성화 한다.
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object{
        //0. float 배열에 삼각형의 vertex를 위한 좌표를 넣습니다.
        const val COORDS_PER_VERTEX = 3

        // 삼각형
        var triangleCoords = floatArrayOf( //넣는 순서는 반시계 방향입니다.
                0.0f, 0.622008459f, 0.0f,  // 상단 vertex
                -0.5f, -0.311004243f, 0.0f,  // 왼쪽 아래 vertex
                0.5f, -0.311004243f, 0.0f // 오른쪽 아래 vertex
        )

        // 삼각뿔
//        var triangleCoords: FloatArray = floatArrayOf(
//                //front
//                0.0f, 0.5f, 0.0f,  // 상단 vertex
//                -0.5f, -0.5f, 0.5f,  // 왼쪽 아래 vertex
//                0.5f, -0.5f, 0.5f,  // 오른쪽 아래 vertex
//                //right
//                0.0f, 0.5f, 0.0f,  // 상단 vertex
//                0.5f, -0.5f, 0.5f,  // 왼쪽 아래 vertex
//                0.5f, -0.5f, -0.5f,  // 오른쪽 아래 vertex
//                //back
//                0.0f, 0.5f, 0.0f,  // 상단 vertex
//                0.5f, -0.5f, -0.5f,  // 왼쪽 아래 vertex
//                -0.5f, -0.5f, -0.5f,  // 오른쪽 아래 vertex
//                //left
//                0.0f, 0.5f, 0.0f,  // 상단 vertex
//                -0.5f, -0.5f, -0.5f,  // 왼쪽 아래 vertex
//                -0.5f, -0.5f, 0.5f // 오른쪽 아래 vertex
//        )

    }
}
