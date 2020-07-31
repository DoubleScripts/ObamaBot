package me.alex.obama.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

object SwearCheck {
    private const val SWEAR_WORDS_URL_EN = "https://raw.githubusercontent.com/chucknorris-io/swear-words/master/en"
    private const val SWEAR_WORDS_URL_ES = "https://raw.githubusercontent.com/chucknorris-io/swear-words/master/es"
    private var swearWords: MutableList<String> = ArrayList()
    private val HTTP_CLIENT = OkHttpClient()

    @Throws(IOException::class)
    private fun readUrl(url: String): List<String> {
        val request = Request.Builder()
                .url(url)
                .build()
        val strings: MutableList<String> = ArrayList()

        HTTP_CLIENT.newCall(request).execute().use { response ->

            response.body!!.source().use { source ->
                while (true) {
                    val line = source.readUtf8Line() ?: break
                    strings.add(line)
                }
            }

        }
        return strings
    }

    /**
     *
     * @param message message to check
     *
     * @return A list of swear words found
     */
    @JvmStatic
    fun containsSwear(message: String): List<String> {
        val strings: MutableList<String> = ArrayList()

        // Ignore casing
        swearWords.filter { message.toLowerCase().contains(Regex("\\b${it.toLowerCase()}\\b")) }
                .toCollection(strings)

        return strings
    }

    init {
        try {
            swearWords.addAll(readUrl(SWEAR_WORDS_URL_EN))
            swearWords. addAll(readUrl(SWEAR_WORDS_URL_ES))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}