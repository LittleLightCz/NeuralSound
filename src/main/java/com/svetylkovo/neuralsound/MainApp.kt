package com.svetylkovo.neuralsound

import com.svetylkovo.neuralsound.view.NeuralSoundView
import tornadofx.App

class MainApp : App(NeuralSoundView::class) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java)
        }
    }
}