package com.svetylkovo.neuralsound

import com.svetylkovo.neuralsound.view.NeuralSoundView
import javafx.application.Platform
import tornadofx.App

class NeuralSoundApp : App(NeuralSoundView::class) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(NeuralSoundApp::class.java)
        }
    }
}
fun exit() {
    Platform.exit()
    System.exit(0)
}