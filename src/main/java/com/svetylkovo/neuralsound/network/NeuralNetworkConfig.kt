package com.svetylkovo.neuralsound.network

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty

object NeuralNetworkConfig {
    val inputLayerSizeProp = SimpleIntegerProperty(1024)
    val hiddenLayerSizeProp = SimpleIntegerProperty(50)

    val maxEpochsProp = SimpleIntegerProperty(100)
    val maxDataSetSizeProp = SimpleIntegerProperty(100)
    val maxLearningErrorProp = SimpleDoubleProperty(0.001)

    val maxLearnStepProp = SimpleDoubleProperty(0.01)

    val outputSamplesCountProp = SimpleIntegerProperty(3000)

    val useLoopProp = SimpleBooleanProperty(true)

    //getters
    val inputLayerSize get() = inputLayerSizeProp.get()
    val hiddenLayerSize get() = hiddenLayerSizeProp.get()
    val outputLayerSize get() = inputLayerSize
    val maxEpochs get() = maxEpochsProp.get()
    val maxDataSetSize get() = maxDataSetSizeProp.get()
    val maxLearningError get() = this.maxLearningErrorProp.get()
    val maxLearnStep get() = this.maxLearnStepProp.get()
    val outputSamplesCount get() = outputSamplesCountProp.get()
    val useLoop get() = useLoopProp.get()

}