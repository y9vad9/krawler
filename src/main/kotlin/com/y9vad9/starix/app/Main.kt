package com.y9vad9.starix.app

import com.github.ajalt.clikt.command.main

suspend fun main(args: Array<String>): Unit = MainCommand().main(args)