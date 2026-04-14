package dk.soerensen.chorechamp.model

import androidx.annotation.DrawableRes
import dk.soerensen.chorechamp.R

object DragonHelper {

    fun getStageName(completedChoresCount: Int): String = when {
        completedChoresCount >= 56 -> "🐲 Adult Dragon"
        completedChoresCount >= 36 -> "🐉 Teenage Dragon"
        completedChoresCount >= 21 -> "🐣 Baby Dragon"
        else -> "🥚 Dragon Egg"
    }

    @DrawableRes
    fun getDragonImage(completedChoresCount: Int, dragonType: Int): Int {
        return when {
            completedChoresCount >= 56 -> when (dragonType) {
                2 -> R.drawable.voksendrage2
                3 -> R.drawable.voksendrage3
                else -> R.drawable.voksendrage1
            }
            completedChoresCount >= 36 -> when (dragonType) {
                2 -> R.drawable.teenagedrage2
                3 -> R.drawable.teenagedrage3
                else -> R.drawable.teenagedrage1
            }
            completedChoresCount >= 21 -> when (dragonType) {
                2 -> R.drawable.babydrage2
                3 -> R.drawable.babydrage3
                else -> R.drawable.babydrage1
            }
            else -> when (dragonType) {
                2 -> R.drawable.aegdrage2
                3 -> R.drawable.aegdrage3
                else -> R.drawable.aegdrage1
            }
        }
    }

    @DrawableRes
    fun getSelectionImage(dragonType: Int): Int = when (dragonType) {
        2 -> R.drawable.drage2
        3 -> R.drawable.drage3
        else -> R.drawable.drage1
    }
}
