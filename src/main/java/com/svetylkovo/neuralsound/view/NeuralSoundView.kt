package com.svetylkovo.neuralsound.view

import com.svetylkovo.neuralsound.controller.NeuralController
import com.svetylkovo.neuralsound.exit
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*

class NeuralSoundView : View("Neural Sound") {

    val neuralController by inject<NeuralController>()

    val input = mutableListOf<Double>().observable()
    val output = mutableListOf<Double>().observable()

    val downsampledSize = 500

    override val root = vbox {
        shortcut("Esc") { exit() }

        paddingAll = 5.0

        prefWidth = 600.0
        prefHeight = 600.0

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

            input.onChange {
                series("Input") {
                    data.setAll(
                        input.downsampleTo(downsampledSize)
                            .asSequence()
                            .withIndex()
                            .map { (index, value) -> XYChart.Data<Number, Number>(index, value) }
                            .toList()
                    )
                }
            }

        }

        linechart("Output", NumberAxis(), NumberAxis()) {
            animated = false

            output.onChange {
                data.clear()
                series("Output") {
                    data.setAll(
                        output.downsampleTo(downsampledSize)
                            .asSequence()
                            .withIndex()
                            .map { (index, value) -> XYChart.Data<Number, Number>(index, value) }
                            .toList()
                    )
                }
            }

        }

    }

    init {
        neuralController.loadBaseSine(input)
    }
}

private fun <T> List<T>.downsampleTo(targetSize: Int): List<T> {
    val stepSize = size / targetSize
    return filterIndexed { index, _ -> index % stepSize == 0 }
}

