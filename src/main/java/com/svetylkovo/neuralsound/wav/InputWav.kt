package com.svetylkovo.neuralsound.wav

import javafx.stage.FileChooser
import javafx.stage.Stage
import labbookpage.wav.WavFile
import java.io.File


object InputWav {

    var numChannels = 0
    var numFrames = 0L
    var validBits = 0
    var sampleRate = 0L

    var samples = DoubleArray(0)

    private var lastWavFile: File? = null
    private var lastDirectory: File? = null

    fun openAndLoad() {
        FileChooser().apply {
            initialDirectory = lastDirectory
        }.showOpenDialog(Stage())?.let {
            val wavFile = WavFile.openWavFile(it)

            numChannels = wavFile.numChannels
            numFrames = wavFile.numFrames
            validBits = wavFile.validBits
            sampleRate = wavFile.sampleRate

            samples = DoubleArray(numChannels * numFrames.toInt())
            wavFile.readFrames(samples, samples.size)

            if (numChannels == 2) {
                samples = samples.filterIndexed { index, _ -> index % 2 == 0 }.toDoubleArray()
            }

            lastWavFile = it
            lastDirectory = it.parentFile
        }
    }

    fun play() {
        lastWavFile?.let {
            WavPlayer.play(it)
        }
    }

}