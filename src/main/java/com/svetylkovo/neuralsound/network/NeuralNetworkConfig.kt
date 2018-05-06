package com.svetylkovo.neuralsound.network

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.onChange
import kotlin.math.roundToInt

object NeuralNetworkConfig {
    val inputLayerSizeProp = SimpleIntegerProperty(0)
    val hiddenLayerSizeProp = SimpleIntegerProperty(0)
    val maxEpochsProp = SimpleIntegerProperty(100)
    val maxSamplesToLearnProp = SimpleIntegerProperty(100)
    val maxLearningErrorProp = SimpleDoubleProperty(0.001)
    val learnRateProp = SimpleDoubleProperty(0.1)
    val momentumRateProp = SimpleDoubleProperty(0.1)
    val outputSamplesCountProp = SimpleIntegerProperty(8000)
    val windowStepProp = SimpleIntegerProperty(1)

    val inputLayerSize get() = inputLayerSizeProp.get()
    val hiddenLayerSize get() = hiddenLayerSizeProp.get()
    val outputLayerSize = 1
    val maxEpochs get() = maxEpochsProp.get()
    val maxSamplesToLearn get() = maxSamplesToLearnProp.get()
    val maxLearningError get() = this.maxLearningErrorProp.get()
    val learnRate get() = this.learnRateProp.get()
    val momentumRate get() = this.momentumRateProp.get()
    val outputSamplesCount get() = outputSamplesCountProp.get()
    val windowStep get() = windowStepProp.get()

    init {
        inputLayerSizeProp.onChange { hiddenLayerSizeProp.set(2 * it + 1) }
        inputLayerSizeProp.set(600)
    }

    fun setGoodDefaultsFor(inputSamplesSize: Int) {
        inputLayerSizeProp.set((inputSamplesSize * 0.75).roundToInt())
        windowStepProp.set((inputSamplesSize * 0.035).roundToInt())
        maxSamplesToLearnProp.set((inputSamplesSize- inputLayerSize)/ windowStep)
    }
}