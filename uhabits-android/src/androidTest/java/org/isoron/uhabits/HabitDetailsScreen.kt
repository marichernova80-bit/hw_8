package org.isoron.uhabits


object HabitDetailsScreen : com.kaspersky.kaspresso.screens.KScreen<HabitDetailsScreen>() {
    override val layoutId: Int? = null
    override val viewClass: Class<*>? = null
    val moreOptions = io.github.kakaocup.kakao.text.KButton { withContentDescription("More options") }
    val deleteMenuButton = io.github.kakaocup.kakao.text.KButton { withText("Delete") }
}