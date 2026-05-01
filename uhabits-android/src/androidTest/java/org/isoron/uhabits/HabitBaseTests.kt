package org.isoron.uhabits

import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.junit.Rule
import org.junit.Test
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.GeneralLocation
import io.github.kakaocup.kakao.text.KButton

fun clickLeft() = GeneralClickAction(
    Tap.SINGLE,
    GeneralLocation.CENTER_LEFT,
    Press.FINGER,
    0,
    0
)


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HabitBaseTests : TestCase() {

    @get:Rule
    val activityRule = ActivityTestRule(ListHabitsActivity::class.java, true, true)

    @Test
    fun test1_CreateHabit() = run {
        step("Пропустить онбординг") {
            OnboardingScreen { skipButton.click() }
        }

        step("Создать и сохранить привычку") {
            Thread.sleep(1500)
            MainScreen { addButton.click() }
            HabitTypeScreen { yesNoButton.click() }

            Thread.sleep(1500)
            EditHabitScreen {
                nameField.typeText("Test Habit")
                saveButton.click()
            }
        }
    }

    @Test
    fun test2_MarkAsDone() = run {
        step("Нажать на левую часть панели чекбоксов") {
            Thread.sleep(2000)
            MainScreen {
                habitList {
                    childAt<MainScreen.HabitItem>(0) {
                        checkmarkPanel.view.interaction.perform(clickLeft())
                    }
                }
            }

            Thread.sleep(1500)

            CheckmarkPickerScreen {
                yesButton.click()
            }
        }
    }


    @Test
    fun test3_DeleteHabit() = run {
        step("Удалить привычку") {
            Thread.sleep(2000)
            MainScreen { habitList { childAt<MainScreen.HabitItem>(0) { title.click() } } }
            Thread.sleep(1500)
            HabitDetailsScreen {
                moreOptions.click()
                Thread.sleep(500)
                deleteMenuButton.click()
            }
            Thread.sleep(1000)
            KButton { withId(android.R.id.button1) }.click()
        }
    }


}
