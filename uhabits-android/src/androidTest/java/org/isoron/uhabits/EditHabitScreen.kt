package org.isoron.uhabits

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton

object EditHabitScreen : KScreen<EditHabitScreen>() {
    override val layoutId: Int? = null
    override val viewClass: Class<*>? = null

    val nameField = KEditText { withResourceName("nameInput") }
    val notesField = KEditText { withResourceName("notesInput") }

    val saveButton = KButton { withResourceName("buttonSave") }

}
