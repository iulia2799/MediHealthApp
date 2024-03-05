package Models.Forms

data class GridItem(val label: String, val text: String)

val loginItems = listOf<GridItem>(
    GridItem("First Name",""),
    GridItem("Last Name",""),
    GridItem("Email",""),
    GridItem("Password",""),
)