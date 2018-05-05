package com.svetylkovo.neuralsound.network

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.onChange

object NeuralNetworkConfig {

    val inputLayerSizeProp = SimpleIntegerProperty(0)
    val hiddenLayerSizeProp = SimpleIntegerProperty(1)

    val inputLayerSize get() = inputLayerSizeProp.get()
    val hiddenLayerSize get() = hiddenLayerSizeProp.get()
    val outputLayerSize = 1

    init {
        inputLayerSizeProp.onChange { hiddenLayerSizeProp.set(2 * it + 1) }
        inputLayerSizeProp.set(100)
    }
}