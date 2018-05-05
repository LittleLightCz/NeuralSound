package com.svetylkovo.neuralsound.wav

import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine


object WavPlayer {

    fun getClip(file: File): Clip {
        val stream = AudioSystem.getAudioInputStream(file)
        val info = DataLine.Info(Clip::class.java, stream.format)
        return (AudioSystem.getLine(info) as Clip).apply {
            open(stream)
        }
    }

    fun play(file: File) = play(getClip(file))

    fun play(clip: Clip) {
        clip.framePosition = 0
        clip.start()
    }
}