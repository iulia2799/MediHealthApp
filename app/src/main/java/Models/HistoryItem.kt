package Models

import com.example.test.Misc.History

data class HistoryItem(
    var doctorRef: String,
    var patientRef: String,
    var ref: String,
    var type: String,
    var created: Long,
    var title: String,
    var description: String
) {

}