package org.isoron.uhabits

import android.view.View
import android.widget.TextView
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.common.views.KView
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int? = null
    override val viewClass: Class<*>? = null

    val addButton = KButton { withResourceName("actionCreateHabit") }

    val habitList = KRecyclerView(
        builder = {
            withMatcher(androidx.test.espresso.matcher.ViewMatchers.withClassName(Matchers.containsString("HabitCardListView")))
        },
        itemTypeBuilder = { itemType(::HabitItem) }
    )

    class HabitItem(parent: Matcher<View>) : KRecyclerItem<HabitItem>(parent) {
        val title = KTextView(parent) {
            withMatcher(androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(TextView::class.java))
        }
        val checkmarkPanel = KView(parent) {
            withMatcher(androidx.test.espresso.matcher.ViewMatchers.withClassName(Matchers.containsString("CheckmarkPanelView")))
        }
    }

}
