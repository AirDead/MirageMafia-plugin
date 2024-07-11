package com.mirage.mafiagame.network

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object NetUtil {

    fun readInt(buffer: ByteBuffer): Int {
        return buffer.int
    }

    fun readFloat(buffer: ByteBuffer): Float {
        return buffer.float
    }

    fun readString(buffer: ByteBuffer): String {
        val length = buffer.int
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }
}
