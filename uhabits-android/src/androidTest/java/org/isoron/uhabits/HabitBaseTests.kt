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
import java.io.File
import java.util.UUID

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

    private fun skipOnboardingIfVisible() {
        try {
            OnboardingScreen { skipButton.click() }
        } catch (_: Throwable) {
        }
    }

    private fun createHabit(name: String) {
        device.uiDevice.waitForIdle()

        MainScreen { addButton.click() }
        HabitTypeScreen { yesNoButton.click() }

        device.uiDevice.waitForIdle()

        EditHabitScreen {
            nameField.typeText(name)
            saveButton.click()
        }

        device.uiDevice.waitForIdle()
    }
    private fun assertHabitIsDisplayed(name: String) {
        device.uiDevice.waitForIdle()

        MainScreen {
            habitList {
                childWith<MainScreen.HabitItem> {
                    withDescendant {
                        withText(name)
                    }
                } perform {
                    title.hasText(name)
                    checkmarkPanel.isDisplayed()
                }
            }
        }
    }

    private fun assertHabitIsNotDisplayed(name: String) {
        device.uiDevice.waitForIdle()

        var isDisplayed = false

        try {
            MainScreen {
                habitList {
                    childWith<MainScreen.HabitItem> {
                        withDescendant {
                            withText(name)
                        }
                    } perform {
                        title.hasText(name)
                    }
                }
            }

            isDisplayed = true
        } catch (_: Throwable) {
            isDisplayed = false
        }

        if (isDisplayed) {
            throw AssertionError("Habit '$name' was not deleted")
        }
    }


    private fun assertCheckmarkPickerIsOpened() {
        CheckmarkPickerScreen {
            yesButton.isDisplayed()
        }
    }


    @Test
    fun test1_CreateHabit() = allureRun("test1_CreateHabit") {
        val habitName = "Test Habit"

        Allure.step("Пропустить онбординг, если он отображается") {
            skipOnboardingIfVisible()
        }

        Allure.step("Создать и сохранить привычку") {
            createHabit(habitName)
        }

        Allure.step("Проверить, что привычка отображается в списке") {
            assertHabitIsDisplayed(habitName)
        }
    }

    @Test
    fun test2_MarkAsDone() = allureRun("test2_MarkAsDone") {
        val habitName = "Test Habit For Mark"

        Allure.step("Пропустить онбординг, если он отображается") {
            skipOnboardingIfVisible()
        }

        Allure.step("Создать привычку для отметки выполнения") {
            createHabit(habitName)
        }

        Allure.step("Проверить, что созданная привычка отображается в списке") {
            assertHabitIsDisplayed(habitName)
        }

        Allure.step("Нажать на левую часть панели чекбоксов у созданной привычки") {
            device.uiDevice.waitForIdle()

            MainScreen {
                habitList {
                    childWith<MainScreen.HabitItem> {
                        withDescendant {
                            withText(habitName)
                        }
                    } perform {
                        checkmarkPanel.view.interaction.perform(clickLeft())
                    }
                }
            }
        }

        Allure.step("Проверить, что открылся выбор статуса выполнения") {
            assertCheckmarkPickerIsOpened()
        }

        Allure.step("Выбрать статус выполнения Да") {
            CheckmarkPickerScreen {
                yesButton.click()
            }

            device.uiDevice.waitForIdle()
        }

        Allure.step("Проверить, что привычка осталась в списке после отметки выполнения") {
            assertHabitIsDisplayed(habitName)
        }
    }


    @Test
    fun test3_DeleteHabit() = allureRun("test3_DeleteHabit") {
        val habitName = "Test Habit For Delete"

        Allure.step("Пропустить онбординг, если он отображается") {
            skipOnboardingIfVisible()
        }

        Allure.step("Создать привычку для удаления") {
            createHabit(habitName)
        }

        Allure.step("Проверить, что созданная привычка отображается в списке") {
            assertHabitIsDisplayed(habitName)
        }

        Allure.step("Удалить созданную привычку") {
            device.uiDevice.waitForIdle()

            MainScreen {
                habitList {
                    childWith<MainScreen.HabitItem> {
                        withDescendant {
                            withText(habitName)
                        }
                    } perform {
                        title.click()
                    }
                }
            }

            device.uiDevice.waitForIdle()

            HabitDetailsScreen {
                moreOptions.click()
                device.uiDevice.waitForIdle()
                deleteMenuButton.click()
            }

            device.uiDevice.waitForIdle()

            KButton { withId(android.R.id.button1) }.click()

            device.uiDevice.waitForIdle()
        }

        Allure.step("Проверить, что удаленная привычка больше не отображается в списке") {
            assertHabitIsNotDisplayed(habitName)
        }
    }
}
