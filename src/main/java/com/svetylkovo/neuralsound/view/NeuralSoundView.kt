package com.svetylkovo.neuralsound.view

import com.svetylkovo.neuralsound.controller.NeuralController
import com.svetylkovo.neuralsound.exit
import com.svetylkovo.neuralsound.extensions.fitXAxisTo
import com.svetylkovo.neuralsound.extensions.useThinLine
import com.svetylkovo.neuralsound.wav.InputWav
import javafx.scene.chart.NumberAxis
import tornadofx.*

class NeuralSoundView : View("Neural Sound") {

    val neuralController by inject<NeuralController>()

    val inputSamples = mutableListOf<Double>().observable()
    val outputSamples = mutableListOf<Double>().observable()

    val downsampledSize = 1000

    override val root = vbox {
        shortcut("Esc") { exit() }

        paddingAll = 5.0

        prefWidth = 1000.0
        prefHeight = 800.0

        hbox {
            spacing = 3.0

            button("Load WAV").action {
                InputWav.openAndLoad()
                inputSamples.setAll(InputWav.samples.toList())
            }

            button("Learn and Generate WAV").action {
                neuralController.learn() success {
                    neuralController.generateWav() ui { outputSamples.setAll(it) }
                }
            }

            button("Re-generate WAV").action {
                neuralController.generateWav() ui { outputSamples.setAll(it) }
            }

            button("Play WAV Input").action {
                InputWav.play()
            }
        }

        linechart("Input", NumberAxis(), NumberAxis()) {
            animated = false
            createSymbols = false

            inputSamples.onChange {
                data.clear()

                series("Input") {
                    inputSamples.downsampleTo(downsampledSize)
                        .forEachIndexed { index, value -> data(index, value) }
                }

                useThinLine()
                fitXAxisTo(downsampledSize.toDouble())
            }
        }

        hbox {
            button("Play WAV result").action {
                neuralController.play()
            }
        }

        linechart("Output", NumberAxis(), NumberAxis()) {
            animated = false
            createSymbols = false

            outputSamples.onChange {
                data.clear()

                series("Output") {
                    outputSamples.downsampleTo(downsampledSize)
                        .forEachIndexed { index, value -> data(index, value) }
                }

                useThinLine()
                fitXAxisTo(downsampledSize.toDouble())
            }
        }
    }

}

private fun List<Double>.downsampleTo(targetSize: Int): List<Double> {
    val windowSize = (size / targetSize) * 2
    return asSequence().windowed(windowSize, windowSize)
        .flatMap { sequenceOf(it.min(), it.max()).filterNotNull() }
        .toList()
}
