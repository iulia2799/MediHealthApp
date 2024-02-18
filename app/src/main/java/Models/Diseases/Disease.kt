package Models.Diseases

import Models.Department

data class Disease(
    val uid: Long,
    val name: String,
    val description: String,
    val symptoms: List<String>,
    val type: Department,
    val treatment: String,
    val cure: String,
    val curable: Boolean
)
