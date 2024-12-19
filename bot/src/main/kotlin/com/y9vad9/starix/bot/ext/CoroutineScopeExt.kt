package com.y9vad9.starix.bot.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun CoroutineScope.launchRestartable(
    block: suspend () -> Unit,
) {
    try {
        launch(SupervisorJob() + Dispatchers.Default) {
            block()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        launchRestartable(block)
    }
}