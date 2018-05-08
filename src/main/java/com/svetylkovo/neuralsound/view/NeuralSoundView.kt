package com.svetylkovo.neuralsound.view

import com.svetylkovo.neuralsound.controller.WavOutputController
import com.svetylkovo.neuralsound.exit
import com.svetylkovo.neuralsound.extensions.fitXAxisTo
import com.svetylkovo.neuralsound.extensions.useThinLine
import com.svetylkovo.neuralsound.network.NeuralNetworkConfig
import com.svetylkovo.neuralsound.wav.InputWav
import javafx.scene.chart.NumberAxis
import javafx.scene.control.TabPane
import tornadofx.*

class NeuralSoundView : View("Neural Sound") {

    val wavOutputController by inject<WavOutputController>()

    val inputSamples = mutableListOf<Double>().observable()
    val outputSamples = mutableListOf<Double>().observable()

    val downsampledSize = 1000

    override val root = vbox {
        shortcut("Esc") { exit() }

        paddingAll = 5.0
        spacing = 2.0

        prefWidth = 1000.0
        prefHeight = 850.0

        hbox {
            spacing = 3.0

            button("Load WAV").action {
                InputWav.openAndLoad()
                inputSamples.setAll(InputWav.samples.toList())
            }

            button("Play WAV result").action {
                wavOutputController.play()
            }

            button("Play WAV Input").action {
                InputWav.play()
            }
        }

        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Many-to-many samples generator", find<ManyToManySamplesGeneratorView>().root)
            tab("Sine based modulator", find<SineBasedModulatorView>().root)
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

        checkbox(
            "Use input samples as a kicker for the first neural output generation",
            NeuralNetworkConfig.useInputSamplesAsKicker
        )
    }

}

private fun List<Double>.downsampleTo(targetSize: Int): List<Double> {
    return if (size > targetSize) {
        val windowSize = (size / targetSize) * 2
        asSequence().windowed(windowSize, windowSize)
            .flatMap { sequenceOf(it.min(), it.max()).filterNotNull() }
            .toList()
    } else this
}
