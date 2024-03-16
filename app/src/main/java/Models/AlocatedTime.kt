package Models

data class AlocatedTime(
    var userId: String,
    var start: Long,
    var end: Long,
    var description: String
)
