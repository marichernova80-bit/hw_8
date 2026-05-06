package org.isoron.uhabits

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import java.io.File

class AllureFixRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        // SDK 36: пишем только во внутреннюю память
        val allurePath = File(targetContext.filesDir, "allure-results").absolutePath
        File(allurePath).mkdirs()

        System.setProperty("allure.results.directory", allurePath)
        // Не добавляем никаких слушателей здесь!

        super.onCreate(arguments)
    }
}
