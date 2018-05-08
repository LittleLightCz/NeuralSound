package com.svetylkovo.neuralsound.network

import com.svetylkovo.neuralsound.extensions.fromNormalized
import com.svetylkovo.neuralsound.extensions.toNormalized
import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.ml.data.basic.BasicMLDataSet
import org.encog.neural.networks.BasicNetwork
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation
import org.junit.Test
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


class NeuralNetworkTest {

    @Test
    fun neuralNetworkTestEncog() {

        println("Testing Encog framework ...")

        val input = ((-10 until 10).map { it / 10.0 } + (10 downTo -9).map { it / 10.0 }).toMutableList()
        input += input

        val inputSize = 60
        val outputSize = 1
        val trainingDataSetSize = 100

        val network = BasicNetwork().apply {
            addLayer(BasicLayer(null, true, inputSize))
            addLayer(BasicLayer(ActivationSigmoid(), true, 2 * inputSize + 1))
            addLayer(BasicLayer(ActivationSigmoid(), false, 1))

            structure.finalizeStructure()
            reset()

            val trainingMap = input.asSequence()
                .map { it.toNormalized() }
                .windowed(inputSize + outputSize, 3)
                .take(trainingDataSetSize)
                .map {
                    it.take(inputSize).toDoubleArray() to it.takeLast(1).toDoubleArray()
                }.toMap()

            val dataSet = BasicMLDataSet(trainingMap.keys.toTypedArray(), trainingMap.values.toTypedArray())

            val train = ResilientPropagation(this, dataSet)

            println("Learning ... (data size: ${dataSet.size()})")

            var epoch = 1

            do {
                train.iteration()
                println("Epoch ${epoch++}, error ${train.error}")
            } while (train.error > 0.0001)

            train.finishTraining()

            println("Neural network ready!")
        }

        //output test
        val result = input.take(inputSize).map { it.toNormalized() }.toMutableList()
        val singleSampleResult = DoubleArray(1)

        repeat((input.size * 20) - inputSize) {
            network.compute(result.takeLast(inputSize).toDoubleArray(), singleSampleResult)
            result += singleSampleResult[0]
        }

        val unNormalizedResult = result.map { it.fromNormalized() }
        unNormalizedResult.forEach { println(it) }

        val selection = StringSelection(unNormalizedResult.joinToString("\n"))
        val clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(selection, selection)

    }

    @Test
    fun neuralNetworkTestNtoNSamplesGeneration() {

        println("Testing many-to-many neural network  ...")

        val input = ((-10 until 10).map { it / 10.0 } + (10 downTo -9).map { it / 10.0 }).toMutableList()
        input += input

        val inputSize = 20
        val hiddenSize = 2*inputSize+1
        val outputSize = inputSize

        val network = BasicNetwork().apply {
            addLayer(BasicLayer(null, true, inputSize))
            addLayer(BasicLayer(ActivationSigmoid(), true, hiddenSize))
//            addLayer(BasicLayer(ActivationSigmoid(), true, hiddenSize / 2))
            addLayer(BasicLayer(ActivationSigmoid(), false, outputSize))

            structure.finalizeStructure()
            reset()

            val trainingMap = input.asSequence()
                .map { it.toNormalized() }
                .windowed(inputSize, inputSize)
                .windowed(2, 1)
                .map {
                     it.first().toDoubleArray() to it.last().toDoubleArray()
                }.toMap()

            val dataSet = BasicMLDataSet(trainingMap.keys.toTypedArray(), trainingMap.values.toTypedArray())

            val train = ResilientPropagation(this, dataSet, 0.1, 0.0001)

            println("Learning ... (data size: ${dataSet.size()})")

            var epoch = 1

            do {
                train.iteration()
                println("Epoch ${epoch++}, error ${train.error}")
            } while (train.error > 0.0001)

            train.finishTraining()

            println("Neural network ready!")
        }

        //output test
        val result = input.take(inputSize).map { it.toNormalized() }.toMutableList()
        val neuralOutputResult = DoubleArray(outputSize)

        repeat((input.size / inputSize) * 4) {
            network.compute(result.takeLast(inputSize).toDoubleArray(), neuralOutputResult)
            result += neuralOutputResult.toList()
        }

        val unNormalizedResult = result.map { it.fromNormalized() }
        unNormalizedResult.forEach { println(it) }

        val selection = StringSelection(unNormalizedResult.joinToString("\n"))
        val clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(selection, selection)

    }
}