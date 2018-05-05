package com.svetylkovo.neuralsound.extensions

import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis


fun LineChart<Number, Number>.fitXAxisTo(targetSize: Double) {
    (xAxis as NumberAxis).run {
        isAutoRanging = false
        upperBound = targetSize
    }
}

fun <X, Y> LineChart<X, Y>.useThinLine() {
    lookup(".chart-series-line")?.style = "-fx-stroke-width: 1;"
}
