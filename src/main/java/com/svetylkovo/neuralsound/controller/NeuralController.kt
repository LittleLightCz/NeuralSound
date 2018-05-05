package com.svetylkovo.neuralsound.controller

import com.svetylkovo.neuralsound.network.NeuralNetworkConfig
import com.svetylkovo.neuralsound.wav.InputWav
import com.svetylkovo.neuralsound.wav.WavPlayer
import labbookpage.wav.WavFile
import org.neuroph.core.data.DataSet
import org.neuroph.core.data.DataSetRow
import org.neuroph.nnet.MultiLayerPerceptron
import org.neuroph.nnet.learning.BackPropagation
import tornadofx.Controller
import java.io.File
import javax.sound.sampled.Clip


class NeuralController : Controller() {

    var neuralNetwork: MultiLayerPerceptron? = null

    val outputFileName = "out.wav"
    var lastWavClip: Clip? = null

    fun learn() = runAsync {

        neuralNetwork = with(NeuralNetworkConfig) {

            MultiLayerPerceptron(inputLayerSize, hiddenLayerSize, outputLayerSize).apply {

                println("Preparing learning dataset ...")

                val inputSize = inputLayerSize
                val outputSize = outputLayerSize

                val dataSet = DataSet(inputSize, outputSize)

                InputWav.samples
                    .asSequence()
                    .map { it.toNormalized() }
                    .windowed(inputSize + outputSize, 1)
                    .take(maxSamplesToLearn)
                    .forEach {
                        val dataSetRow = DataSetRow(it.take(inputSize).toDoubleArray(), it.takeLast(1).toDoubleArray())
                        dataSet.addRow(dataSetRow)
                    }

                println("Learning ...")

                val backPropagation = BackPropagation().apply {
                    maxIterations = maxLearningIterations
                    maxError = maxLearningError
                    learningRate = learnRate
                }

                learn(dataSet, backPropagation)

                println("Learning done! Error: ${backPropagation.totalNetworkError}")
            }
        }

        lastWavClip?.close()
        lastWavClip = null
    }

    fun generateWav() = runAsync {

        println("Generating WAV")

        neuralNetwork?.run {

            val result = InputWav.samples.toMutableList()
            val inputSize = NeuralNetworkConfig.inputLayerSize

            repeat(NeuralNetworkConfig.outputSamplesCount) {
//                val neuralInput = if (result.size < inputSize) {
//                    List(inputSize - result.size) { 0.5 } + result
//                } else result.takeLast(inputSize)

                val neuralInput = result.takeLast(inputSize)

                inputNeurons.forEachIndexed { index, neuron ->
                    neuron.setInput(neuralInput[index])
                }

                calculate()
                result += getOutput().toList()
            }

            println("Mapping to result form ...")
            val wavResult = result.map { it.fromNormalized() }
            saveToWav(wavResult)
            result
        } ?: emptyList<Double>()
    }

    fun saveToWav(output: List<Double>) {
        println("Saving to wav file ...")
        with(InputWav) {
            WavFile.newWavFile(File(outputFileName), 1, output.size.toLong(), 32, sampleRate).also {
                it.writeFrames(output.toDoubleArray(), output.size)
                it.close()
                println("Saved to $outputFileName")
            }
        }
    }

    fun play() = runAsync {

        if (lastWavClip == null) {
            lastWavClip = WavPlayer.getClip(File(outputFileName))
        }

        lastWavClip?.let {
            WavPlayer.play(it)
        }
    }
}

fun Double.toNormalized() = (this / 2) + 0.5
fun Double.fromNormalized() = (this - 0.5) * 2