package com.svetylkovo.neuralsound.fft

import com.svetylkovo.neuralsound.wav.InputWav
import org.jtransforms.fft.DoubleFFT_1D
import org.junit.Test
import java.io.File


class FFTTest {

    @Test
    fun fftTest() {

        InputWav.load(File("sineBassFreq.wav"))

        val input = InputWav.samples.take(1024).toDoubleArray()

        val fft = DoubleFFT_1D(input.size.toLong())
        fft.realForward(input)
        fft.realInverse(input, true)

    }
}