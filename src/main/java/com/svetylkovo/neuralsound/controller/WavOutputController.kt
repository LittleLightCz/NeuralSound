package com.svetylkovo.neuralsound.controller

import com.svetylkovo.neuralsound.wav.InputWav
import com.svetylkovo.neuralsound.wav.WavPlayer
import labbookpage.wav.WavFile
import tornadofx.Controller
import java.io.File
import javax.sound.sampled.Clip


class WavOutputController: Controller() {

    val outputFileName = "out.wav"

    var lastWavClip: Clip? = null

    fun saveToWav(output: List<Double>) {
        println("Saving to wav file ...")
        with(InputWav) {
            WavFile.newWavFile(File(outputFileName), 1, output.size.toLong(), 16, sampleRate).also {
                it.writeFrames(output.toDoubleArray(), output.size)
                it.close()
                println("Saved to $outputFileName")
            }
        }
    }

    fun play() = runAsync {

        if (lastWavClip == null) {
            lastWavClip = WavPlayer.getClip(File(outputFileName))
        }

        lastWavClip?.let {
            WavPlayer.play(it)
        }
    }

    fun discardLastWavClip() {
        lastWavClip?.close()
        lastWavClip = null
    }
}