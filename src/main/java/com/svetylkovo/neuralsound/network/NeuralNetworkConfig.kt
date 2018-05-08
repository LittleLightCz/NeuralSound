package com.svetylkovo.neuralsound.network

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlin.math.roundToInt

object NeuralNetworkConfig {
    val inputLayerSizeProp = SimpleIntegerProperty(100)
    val hiddenLayerSizeProp = SimpleIntegerProperty(100)

    val maxEpochsProp = SimpleIntegerProperty(100)
    val maxDataSetSizeProp = SimpleIntegerProperty(100)
    val maxLearningErrorProp = SimpleDoubleProperty(0.001)

    val maxLearnStepProp = SimpleDoubleProperty(0.001)

    val outputSamplesCountProp = SimpleIntegerProperty(8000)

    val useInputSamplesAsKicker = SimpleBooleanProperty(false)

    //getters
    val inputLayerSize get() = inputLayerSizeProp.get()
    val hiddenLayerSize get() = hiddenLayerSizeProp.get()
    val outputLayerSize get() = inputLayerSize
    val maxEpochs get() = maxEpochsProp.get()
    val maxDataSetSize get() = maxDataSetSizeProp.get()
    val maxLearningError get() = this.maxLearningErrorProp.get()
    val maxLearnStep get() = this.maxLearnStepProp.get()
    val outputSamplesCount get() = outputSamplesCountProp.get()

    fun setGoodDefaultsFor(inputSamplesSize: Int) {
        inputLayerSizeProp.set((inputSamplesSize * 0.10).roundToInt())
        maxDataSetSizeProp.set(500)
    }
}