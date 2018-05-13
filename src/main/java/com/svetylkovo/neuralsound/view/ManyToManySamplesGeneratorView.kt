package com.svetylkovo.neuralsound.view

import com.svetylkovo.neuralsound.controller.ManyToManyController
import com.svetylkovo.neuralsound.controller.WavOutputController
import com.svetylkovo.neuralsound.network.NeuralNetworkConfig
import tornadofx.*


class ManyToManySamplesGeneratorView : View() {

    val mainView by inject<NeuralSoundView>()

    val manyToManyController by inject<ManyToManyController>()

    val wavOutputController by inject<WavOutputController>()

    override val root = vbox {
        form {
            fieldset("Neural network parameters:") {
                flowpane {
                    hgap = 5.0

                    vbox {
                        field("Input layer:") {
                            textfield(NeuralNetworkConfig.inputLayerSizeProp)
                        }
                        field("Hidden layer:") {
                            textfield(NeuralNetworkConfig.hiddenLayerSizeProp)
                        }
                        field("Max dataset size:") {
                            textfield(NeuralNetworkConfig.maxDataSetSizeProp)
                        }
                        field("Output samples:") {
                            textfield(NeuralNetworkConfig.outputSamplesCountProp)
                        }
                    }
                    vbox {
                        field("Max epochs:") {
                            textfield(NeuralNetworkConfig.maxEpochsProp)
                        }
                        field("Max learn error:") {
                            textfield(NeuralNetworkConfig.maxLearningErrorProp)
                        }
                        field("Learn step:") {
                            textfield(NeuralNetworkConfig.maxLearnStepProp)
                        }
                    }
                }

                hbox {
                    paddingBottom = 5.0
                    checkbox("Use loop", NeuralNetworkConfig.useLoopProp)
                }

                hbox {
                    spacing = 3.0

                    button("Learn/Generate/Play").action {
                        manyToManyController.learn() success {
                            manyToManyController.generateWav() ui {
                                mainView.outputSamples.setAll(it)
                                wavOutputController.play()
                            }
                        }
                    }

                    button("Re-generate WAV").action {
                        manyToManyController.generateWav() ui { mainView.outputSamples.setAll(it) }
                    }
                }
            }
        }

    }

}