package com.svetylkovo.neuralsound.controller

import javafx.collections.ObservableList
import labbookpage.wav.WavFile
import org.neuroph.core.data.DataSet
import org.neuroph.core.data.DataSetRow
import org.neuroph.nnet.MultiLayerPerceptron
import org.neuroph.nnet.learning.BackPropagation
import tornadofx.Controller
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import kotlin.math.sin


class NeuralController : Controller() {

    val sampleRate = 8000L

    val inputSamplesCount = 8000

    val inputSize = 100
    val hiddenSize = 2 * inputSize + 1
    val outputSize = 1

    val predictedOutputSamplesCount = 16000

    var neuralNetwork: MultiLayerPerceptron? = null

    val outputFileName = "out.wav"
    var lastWavClip: Clip? = null

    fun learn(input: List<Double>) = runAsync {

        neuralNetwork = MultiLayerPerceptron(inputSize, hiddenSize, outputSize).apply {

            println("Preparing learning dataset ...")

            val samplesToLearnCount = 100

            val dataSet = DataSet(inputSize, outputSize)

            input.asSequence()
                .map { it.toNormalized() }
                .windowed(inputSize + outputSize, 1)
                .take(samplesToLearnCount)
                .forEach {
                    val dataSetRow = DataSetRow(it.take(inputSize).toDoubleArray(), it.takeLast(1).toDoubleArray())
                    dataSet.addRow(dataSetRow)
                }

            println("Learning ...")

            val backPropagation = BackPropagation().apply {
                maxIterations = 1000
            }

            learn(dataSet, backPropagation)

            println("Learning done!")
        }

        lastWavClip?.close()
        lastWavClip = null
    }

    fun generateWav() = runAsync {

        neuralNetwork?.run {

            val result = arrayListOf<Double>()

            repeat(predictedOutputSamplesCount) {
                val neuralInput = if (result.size < inputSize) {
                    List(inputSize - result.size) { 0.5 } + result
                } else result.takeLast(inputSize)

                setInput(*neuralInput.toDoubleArray())
                calculate()
                result += getOutput().toList()
            }

            val normalizedResult = result.map { it.fromNormalized() }
            saveToWav(normalizedResult)
            result
        } ?: emptyList<Double>()
    }

    fun saveToWav(output: List<Double>) {

        WavFile.newWavFile(File(outputFileName),1, output.size.toLong(), 16, sampleRate).also {
            it.writeFrames(output.toDoubleArray(), output.size)
            it.close()
            println("Saved to $outputFileName")
        }
    }

    fun loadBaseSine(input: ObservableList<Double>) {
        input.clear()
        input.addAll(
            (1..inputSamplesCount).map { sin(it.toDouble() / 1000) }
        )
    }

    fun play() = runAsync {

        if (lastWavClip == null) {
            val stream = AudioSystem.getAudioInputStream(File(outputFileName))
            val info = DataLine.Info(Clip::class.java, stream.format)
            lastWavClip = AudioSystem.getLine(info) as Clip
            lastWavClip?.open(stream)
        }

        lastWavClip?.framePosition = 0
        lastWavClip?.start()
    }
}

fun Double.toNormalized() = (this / 2) + 0.5
fun Double.fromNormalized() = (this - 0.5) * 2