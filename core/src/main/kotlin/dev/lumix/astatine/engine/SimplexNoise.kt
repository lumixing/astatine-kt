package dev.lumix.astatine.engine

import com.badlogic.gdx.math.MathUtils
import java.util.*

class SimplexNoise {
    private val grad3 = arrayOf(
        intArrayOf(1, 1, 0),
        intArrayOf(-1, 1, 0),
        intArrayOf(1, -1, 0),
        intArrayOf(-1, -1, 0),
        intArrayOf(1, 0, 1),
        intArrayOf(-1, 0, 1),
        intArrayOf(1, 0, -1),
        intArrayOf(-1, 0, -1),
        intArrayOf(0, 1, 1),
        intArrayOf(0, -1, 1),
        intArrayOf(0, 1, -1),
        intArrayOf(0, -1, -1)
    )
    private val p = intArrayOf(
        151, 160, 137, 91, 90, 15,
        131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
        190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
        88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
        77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
        102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
        135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
        5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
        223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
        129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
        251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
        49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
        138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    )

    // To remove the need for index wrapping, float the permutation table length
    private val perm = IntArray(512)

    init {
        for (i in 0..511) perm[i] = p[i and 255]
    }

    fun generateRigid(
        x: Float,
        y: Float,
        H: Float,
        lacunarity: Float,
        octaves: Int,
        offset: Float,
        threshold: Float
    ): Float {
        // get absolute value of signal (this creates the ridges)
        var x = x
        var y = y
        var signal = Math.abs(generate(x, y))
        // invert and translate (note that "offset" should be ~= 1.0)
        signal = offset - signal
        // square the signal, to increase "sharpness" of ridges
        signal *= signal
        var result = signal
        var weight = 1f
        var i = 1
        while (weight > 0.001f && i < octaves) {
            x *= lacunarity
            y *= lacunarity

            // weight successive contributions by previous signal
            weight = signal * threshold
            weight = MathUtils.clamp(weight, 0f, 1f)
            signal = Math.abs(generate(x, y))
            signal = offset - signal
            signal *= signal
            // weight the contribution
            signal *= weight
            result += (signal * Math.pow(lacunarity.toDouble(), (-i * H).toDouble())).toFloat()
            i++
        }
        return result * 2.0f - 1.0f
    }

    fun generate(x: Float, y: Float, octaves: Int, persistence: Float, scale: Float): Float {
        var total = 0f
        var frequency = scale
        var amplitude = 1f

        // We have to keep track of the largest possible amplitude,
        // because each octave adds more, and we need a value in [-1, 1].
        var maxAmplitude = 0f
        for (i in 0 until octaves) {
            total += generate(x * frequency, y * frequency) * amplitude
            frequency *= 2f
            maxAmplitude += amplitude
            amplitude *= persistence
        }
        return total / maxAmplitude
    }

    fun generate(xin: Float, yin: Float): Float {
        val n0: Float
        val n1: Float
        val n2: Float
        val F2 = 0.5f * (Math.sqrt(3.0).toFloat() - 1.0f)
        val s = (xin + yin) * F2
        val i = fastfloor(xin + s)
        val j = fastfloor(yin + s)
        val G2 = (3.0f - Math.sqrt(3.0).toFloat()) / 6.0f
        val t = (i + j) * G2
        val X0 = i - t
        val Y0 = j - t
        val x0 = xin - X0
        val y0 = yin - Y0
        val i1: Int
        val j1: Int
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } else {
            i1 = 0
            j1 = 1
        }
        val x1 = x0 - i1 + G2
        val y1 = y0 - j1 + G2
        val x2 = x0 - 1.0f + 2.0f * G2
        val y2 = y0 - 1.0f + 2.0f * G2
        val ii = i and 255
        val jj = j and 255
        val gi0 = perm[ii + perm[jj]] % 12
        val gi1 = perm[ii + i1 + perm[jj + j1]] % 12
        val gi2 = perm[ii + 1 + perm[jj + 1]] % 12
        var t0 = 0.5f - x0 * x0 - y0 * y0
        if (t0 < 0) n0 = 0.0f else {
            t0 *= t0
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0)
        }
        var t1 = 0.5f - x1 * x1 - y1 * y1
        if (t1 < 0) n1 = 0.0f else {
            t1 *= t1
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1)
        }
        var t2 = 0.5f - x2 * x2 - y2 * y2
        if (t2 < 0) n2 = 0.0f else {
            t2 *= t2
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2)
        }
        return 70.0f * (n0 + n1 + n2)
    }

    fun genGrad(seed: Long) {
        val rnd = Random(seed)
        for (i in 0..254) p[i] = i
        for (i in 0..254) {
            val j = rnd.nextInt(255)
            val nSwap = p[i]
            p[i] = p[j]
            p[j] = nSwap
        }
        for (i in 0..511) perm[i] = p[i and 255]
    }

    companion object {
        // This method is  *lot* faster than using (int)Math.floor(x)
        private fun fastfloor(x: Float): Int {
            return if (x > 0) x.toInt() else x.toInt() - 1
        }

        private fun dot(g: IntArray, x: Float, y: Float): Float {
            return g[0] * x + g[1] * y
        }
    }
}