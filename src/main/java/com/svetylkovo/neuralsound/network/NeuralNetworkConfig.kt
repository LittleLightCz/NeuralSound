package com.svetylkovo.neuralsound.network

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.onChange

object NeuralNetworkConfig {

    val inputLayerSizeProp = SimpleIntegerProperty(0)
    val hiddenLayerSizeProp = SimpleIntegerProperty(0)
    val maxSamplesToLearnProp = SimpleIntegerProperty(500)
    val maxLearningIterationsProp = SimpleIntegerProperty(1000)
    val maxLearningErrorProp = SimpleDoubleProperty(0.001)
    val learnRateProp = SimpleDoubleProperty(0.01)
    val outputSamplesCountProp = SimpleIntegerProperty(16000)

    val inputLayerSize get() = inputLayerSizeProp.get()
    val hiddenLayerSize get() = hiddenLayerSizeProp.get()
    val outputLayerSize = 1
    val maxSamplesToLearn get() = maxSamplesToLearnProp.get()
    val maxLearningIterations get() = this.maxLearningIterationsProp.get()
    val maxLearningError get() = this.maxLearningErrorProp.get()
    val learnRate get() = this.learnRateProp.get()
    val outputSamplesCount get() = outputSamplesCountProp.get()

    init {
        inputLayerSizeProp.onChange { hiddenLayerSizeProp.set(2 * it + 1) }
        inputLayerSizeProp.set(600)
    }
}