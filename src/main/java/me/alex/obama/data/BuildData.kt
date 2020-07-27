package me.alex.obama.data

import com.google.gson.Gson
import me.alex.obama.util.ClassloaderUtil

class BuildData {

    lateinit var gitVersion: String

    companion object {

        @JvmStatic
        val buildData: BuildData = Gson().fromJson(ClassloaderUtil.readLineAsString("build.json"), BuildData::class.java)

    }
}