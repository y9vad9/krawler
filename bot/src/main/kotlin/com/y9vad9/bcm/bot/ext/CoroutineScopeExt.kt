package com.y9vad9.bcm.bot.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.launchRestartable(
    block: suspend () -> Unit
) {
    try {
        launch {
            block()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        launchRestartable(block)
    }
}