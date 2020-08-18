package me.alex.obama.util

import kotlin.random.Random

object Randomizer {

    @JvmStatic
    fun randomNumber(min: Int = 0, max: Int): Int {
        return Random.nextInt(max - min) + min;
    }

    @JvmStatic
    fun randomNumber(min: Long = 0, max: Long): Long {
        return Random.nextLong(max - min) + min;
    }
}