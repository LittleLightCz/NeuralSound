package com.svetylkovo.neuralsound.controller

import com.svetylkovo.neuralsound.network.NeuralNetworkConfig
import com.svetylkovo.neuralsound.wav.InputWav
import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.ml.data.basic.BasicMLDataSet
import org.encog.neural.networks.BasicNetwork
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation
import org.jtransforms.fft.DoubleFFT_1D
import tornadofx.Controller
import kotlin.math.abs


class ManyToManyController : Controller() {

    val wavOutputController by inject<WavOutputController>()

    var neuralNetwork: BasicNetwork? = null

    private var normalizingMultiplier = 1.0

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

                val fft = DoubleFFT_1D(inputSize.toLong())

                val trainingMap = InputWav.samples
                    .asSequence()
                    .windowed(inputSize, inputSize)
                    .map { it.toDoubleArray() }
                    .onEach { fft.realForward(it) }
                    .toList()
                    .also {
                        setMultiplier(it)
                        it.forEach(::normalize)
                    }
                    .windowed(2, 1)
                    .take(maxDataSetSize)
                    .map { it.first() to it.last() }
                    .toMutableList()
                    .also {
                        //create a loop
                        val firstInput = it.first().first
                        val lastOutput = it.last().second
                        it += lastOutput to firstInput
                    }.toMap()

                val dataSet = BasicMLDataSet(trainingMap.keys.toTypedArray(), trainingMap.values.toTypedArray())

                val train = ResilientPropagation(this, dataSet, 0.1, maxLearnStep)

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
            val outputSize = inputSize

            val fft = DoubleFFT_1D(inputSize.toLong())

//            var lastSpectrumResult = DoubleArray(inputSize) { 0.0 }
            var lastSpectrumResult = InputWav.samples.take(inputSize).toDoubleArray()
            fft.realForward(lastSpectrumResult)
            normalize(lastSpectrumResult)

            val neuralResult = DoubleArray(outputSize)

            val unNormalizedSignalResult = ArrayList<Double>()

            repeat(NeuralNetworkConfig.outputSamplesCount / inputSize + 1) {
                compute(lastSpectrumResult, neuralResult)
                lastSpectrumResult = neuralResult.copyOf()

                unNormalize(neuralResult)
                fft.realInverse(neuralResult, true)
                unNormalizedSignalResult.addAll(neuralResult.toTypedArray())
            }

            wavOutputController.saveToWav(unNormalizedSignalResult.toDoubleArray())
            wavOutputController.discardLastWavClip()

            unNormalizedSignalResult
        } ?: emptyList<Double>()
    }

    private fun setMultiplier(samples: List<DoubleArray>) {
        val maxAbs = samples.asSequence()
            .flatMap { it.asSequence() }
            .maxBy { abs(it) } ?: 1.0

        normalizingMultiplier = 0.5 / maxAbs
    }

    private fun normalize(samples: DoubleArray) {
        for (index in samples.indices) {
            samples[index] *= normalizingMultiplier
            samples[index] += 0.5
        }
    }

    private fun unNormalize(samples: DoubleArray) {
        for (index in samples.indices) {
            samples[index] -= 0.5
            samples[index] /= normalizingMultiplier
        }
    }

}
