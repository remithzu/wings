package com.rmtz.wings.commons

import android.annotation.SuppressLint
import android.content.Context
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {
    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
    }

    fun String.toNumber(): Long {
        return substring(indexOfFirst { it.isDigit() }, indexOfLast { it.isDigit() } + 1)
            .filter { it.isDigit() || it == '.' }.toLong()
    }

    @SuppressLint("SimpleDateFormat")
    fun Context.getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return formatter.format(date)
    }
}