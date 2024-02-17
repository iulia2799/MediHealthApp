package Models

import org.json.JSONObject

data class Test(
    val patientUid: Long,
    val patientName: String,
    val date: java.util.Date,
    val results: JSONObject,
    val department: Department
)
