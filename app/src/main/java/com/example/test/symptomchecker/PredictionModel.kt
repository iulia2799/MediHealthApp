package com.example.test.symptomchecker

import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter

class PredictionModel {
    private lateinit var interpreter : Interpreter
    fun getModel() { // get model according to the documentation
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("predict_disease_v1", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions)
            .addOnSuccessListener { model: CustomModel? ->
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                }
            }
    }
    //this function must get a 2d list but with only a single row ; the list looks like this : [[item1,item2,......,itemN]]
    // this list has the same shape as the inputs in the trained model and dataset
    fun interpret(inputs: List<List<Int>> = listOf(emptyList())) : String {
        return if(inputs.isEmpty()) {
            "The prompt is incorrect!"
        } else {
            val outputData = ""
            interpreter.run(inputs,outputData)
            outputData.ifEmpty {
                "Sorry, we could not solve this prompt."
            }
        }
        
    }
}