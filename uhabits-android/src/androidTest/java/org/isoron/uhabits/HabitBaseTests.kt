package org.isoron.uhabits

import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.text.KButton
import io.qameta.allure.kotlin.Allure
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.UUID
import java.io.File


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

    private fun allureRun(testName: String, block: () -> Unit) {
        val uuid = UUID.randomUUID().toString()
        val start = System.currentTimeMillis()
        var status = "passed"

        try {
            block()
        } catch (t: Throwable) {
            status = "failed"
            throw t
        } finally {
            val stop = System.currentTimeMillis()

            val allureDir = File("/storage/emulated/0/Documents/allure-results")
            allureDir.mkdirs()


            val json = """
            {
              "uuid": "$uuid",
              "historyId": "$testName",
              "testCaseId": "$testName",
              "name": "$testName",
              "fullName": "org.isoron.uhabits.HabitBaseTests.$testName",
              "status": "$status",
              "stage": "finished",
              "start": $start,
              "stop": $stop
            }
        """.trimIndent()

            File(allureDir, "$uuid-result.json").writeText(json)
        }
    }


    @Test
    fun test1_CreateHabit() = allureRun("test1_CreateHabit") {
        Allure.step("Пропустить онбординг") {
            OnboardingScreen { skipButton.click() }
        }

        Allure.step("Создать и сохранить привычку") {
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
    fun test2_MarkAsDone() = allureRun("test2_MarkAsDone") {
        Allure.step("Нажать на левую часть панели чекбоксов") {
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
    fun test3_DeleteHabit() = allureRun("test3_DeleteHabit") {
        Allure.step("Удалить привычку") {
            Thread.sleep(2000)
            MainScreen {
                habitList {
                    childAt<MainScreen.HabitItem>(0) {
                        title.click()
                    }
                }
            }

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
