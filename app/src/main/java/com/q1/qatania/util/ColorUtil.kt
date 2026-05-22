package com.q1.qatania.util

import dev.romainguy.kotlin.math.Float4

fun hexToFloat4(hexString: String?): Float4 {
    val hex: String = hexString ?: "#FFFFFF"
    val clean = hex.trimStart('#')
    val r = clean.substring(0, 2).toInt(16) / 255f
    val g = clean.substring(2, 4).toInt(16) / 255f
    val b = clean.substring(4, 6).toInt(16) / 255f
    val a = 1f
    return Float4(r, g, b, a)
}