package org.isoron.uhabits

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.edit.KEditText

object CheckmarkPickerScreen : KScreen<CheckmarkPickerScreen>() {
    override val layoutId: Int? = null
    override val viewClass: Class<*>? = null

    val yesButton = KButton { withResourceName("yesBtn") }
    val noButton = KButton { withResourceName("noBtn") }
    val notesField = KEditText { withResourceName("notes") }
}
