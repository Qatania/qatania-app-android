package com.q1.qatania.util

import kotlinx.serialization.json.Json

val jsonParser = Json {
    ignoreUnknownKeys = true
    isLenient = true
}