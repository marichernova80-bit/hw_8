package org.isoron.uhabits

import android.os.Bundle
import android.util.Log
import androidx.test.runner.AndroidJUnitRunner
import java.io.File

class AllureFixRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        val allureDir = File(targetContext.getExternalFilesDir(null), "allure-results")
        allureDir.mkdirs()

        Log.e("ALLURE_PATH", allureDir.absolutePath)
        System.setProperty("allure.results.directory", allureDir.absolutePath)

        super.onCreate(arguments)
    }
}
