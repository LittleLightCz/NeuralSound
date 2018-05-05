package com.svetylkovo.neuralsound.view

import com.svetylkovo.neuralsound.controller.NeuralController
import com.svetylkovo.neuralsound.exit
import com.svetylkovo.neuralsound.extensions.fitXAxisTo
import com.svetylkovo.neuralsound.extensions.useThinLine
import javafx.scene.chart.NumberAxis
import tornadofx.*

class NeuralSoundView : View("Neural Sound") {

    val neuralController by inject<NeuralController>()

    val input = mutableListOf<Double>().observable()
    val output = mutableListOf<Double>().observable()

    val downsampledSize = 1000

    override val root = vbox {
        shortcut("Esc") { exit() }

        paddingAll = 5.0

        prefWidth = 1000.0
        prefHeight = 800.0

        hbox {
            spacing = 3.0

            button("Learn and Generate WAV").action {
                neuralController.learn(input) success {
                    neuralController.generateWav() ui {
                        output.setAll(it)
                    }
                }
            }
            button("Play last WAV result").action {
                neuralController.play()
            }
        }

        linechart("Input", NumberAxis(), NumberAxis()) {
            animated = false
            createSymbols = false

            input.onChange {
                data.clear()

                series("Input") {
                    input.downsampleTo(downsampledSize)
                        .forEachIndexed { index, value -> data(index, value) }
                }

                useThinLine()
                fitXAxisTo(downsampledSize.toDouble())
            }
        }

        linechart("Output", NumberAxis(), NumberAxis()) {
            animated = false
            createSymbols = false

            output.onChange {
                data.clear()

                series("Output") {
                    output.downsampleTo(downsampledSize)
                        .forEachIndexed { index, value -> data(index, value) }
                }

                useThinLine()
                fitXAxisTo(downsampledSize.toDouble())
            }
        }

    }

    init {
        neuralController.loadBaseSine(input)
    }
}

private fun List<Double>.downsampleTo(targetSize: Int): List<Double> {
    val windowSize = (size / targetSize) * 2
    return asSequence().windowed(windowSize, windowSize)
        .flatMap { sequenceOf(it.min(), it.max()).filterNotNull() }
        .toList()
}
