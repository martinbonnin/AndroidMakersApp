package fr.paug.androidmakers.model

class Day(val year: Int,
          val month: Int, // from 1 to 12
          val dayOfMonth: Int // from 1 to 31
) {
    companion object {
        fun fromString(s: String): Day? {
            return try {
                val a = s.split("-")
                Day(a[0].toInt(), a[1].toInt(), a[2].toInt())
            } catch (e: Exception) {
                return null
            }
        }
    }
}