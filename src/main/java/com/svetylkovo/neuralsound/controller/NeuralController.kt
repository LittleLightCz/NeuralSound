package com.svetylkovo.neuralsound.controller

import com.svetylkovo.neuralsound.extensions.fromNormalized
import com.svetylkovo.neuralsound.extensions.toNormalized
import com.svetylkovo.neuralsound.network.NeuralNetworkConfig
import com.svetylkovo.neuralsound.wav.InputWav
import com.svetylkovo.neuralsound.wav.WavPlayer
import labbookpage.wav.WavFile
import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.ml.data.basic.BasicMLDataSet
import org.encog.neural.networks.BasicNetwork
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation
import tornadofx.Controller
import java.io.File
import javax.sound.sampled.Clip


class NeuralController : Controller() {

    var neuralNetwork: BasicNetwork? = null

    val outputFileName = "out.wav"
    var lastWavClip: Clip? = null

    fun learn() = runAsync {

        neuralNetwork = with(NeuralNetworkConfig) {

            println("Creating neural network ...")

            BasicNetwork().apply {
                addLayer(BasicLayer(null, true, inputLayerSize))
                addLayer(BasicLayer(ActivationSigmoid(), true, hiddenLayerSize))
                addLayer(BasicLayer(ActivationSigmoid(), true, hiddenLayerSize / 2))
                addLayer(BasicLayer(ActivationSigmoid(), false, outputLayerSize))

                structure.finalizeStructure()
                reset()

                println("Preparing learning dataset ...")

                val inputSize = inputLayerSize
                val outputSize = outputLayerSize

                val trainingMap = InputWav.samples
                    .asSequence()
                    .map { it.toNormalized() }
                    .windowed(inputSize + outputSize, windowStep)
                    .take(maxSamplesToLearn)
                    .map {
                        it.take(inputSize).toDoubleArray() to it.takeLast(outputLayerSize).toDoubleArray()
                    }.toMap()

                val dataSet = BasicMLDataSet(trainingMap.keys.toTypedArray(), trainingMap.values.toTypedArray())

                val train = ResilientPropagation(this, dataSet, learnRate, momentumRate)

                println("Learning ...")

                var lastError = Double.MAX_VALUE
                var errorNotImprovingCounter = 0

                for (i in 1..maxEpochs) {
                    train.iteration()

                    if (lastError == train.error) errorNotImprovingCounter++
                    lastError = train.error

                    println("Epoch $i, Error: ${train.error}")

                    if (train.error < maxLearningError || errorNotImprovingCounter > 5) break
                }


                train.finishTraining()

                println("Learning done! Error: ${train.error}")
            }
        }

    }

    fun generateWav() = runAsync {

        println("Generating WAV")

        neuralNetwork?.run {

            val inputSize = NeuralNetworkConfig.inputLayerSize

            val result = List(inputSize) { 0.5 }.toMutableList()

            val singleSampleResult = DoubleArray(1)

            repeat(NeuralNetworkConfig.outputSamplesCount) {
//                val neuralInput = if (result.size < inputSize) {
//                    List(inputSize - result.size) { 0.5 } + result
//                } else result.takeLast(inputSize)

                val neuralInput = result.takeLast(inputSize)

                compute(neuralInput.toDoubleArray(), singleSampleResult)
                result += singleSampleResult[0]
            }

            println("Mapping to un-normalized form ...")
            val unNormalizedResult = result.map { it.fromNormalized() }
            saveToWav(unNormalizedResult)

            lastWavClip?.close()
            lastWavClip = null

            result
        } ?: emptyList<Double>()
    }

    fun saveToWav(output: List<Double>) {
        println("Saving to wav file ...")
        with(InputWav) {
            WavFile.newWavFile(File(outputFileName), 1, output.size.toLong(), 16, sampleRate).also {
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
