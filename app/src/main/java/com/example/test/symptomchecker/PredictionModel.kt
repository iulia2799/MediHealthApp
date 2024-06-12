package com.example.test.symptomchecker

import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter

class PredictionModel {
    private lateinit var interpreter: Interpreter
    fun getModel() { // get model according to the documentation
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                "prediction_v3", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                }
            }
    }

    //this function must get a 2d list but with only a single row ; the list looks like this : [[item1,item2,......,itemN]]
    // this list has the same shape as the inputs in the trained model and dataset
    fun interpret(inputs: List<Int>): Array<FloatArray>? {
        if (inputs.size != 132) {
            return null
        }

        val expanded = preprocessData(inputs)
        val outputData = Array(1) { FloatArray(41) }

        interpreter.run(expanded, outputData)

        return outputData
    }


    private fun preprocessData(inputs: List<Int>): LongArray {
        val processedData = LongArray(inputs.size)
        for (i in inputs.indices) {
            processedData[i] = inputs[i].toLong()
        }
        return processedData
    }


}