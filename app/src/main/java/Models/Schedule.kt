package Models

data class Schedule (
    var start: String = "9",
    var end: String = "12",
    var weekStart: String = "Monday",
    var weekend: String = "Friday"
)