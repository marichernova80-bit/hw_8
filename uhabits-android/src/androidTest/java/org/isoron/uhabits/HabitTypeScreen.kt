package org.isoron.uhabits

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton

object HabitTypeScreen : KScreen<HabitTypeScreen>() {
    override val layoutId: Int? = null
    override val viewClass: Class<*>? = null

    val yesNoButton = KButton { withResourceName("buttonYesNo") }
}
