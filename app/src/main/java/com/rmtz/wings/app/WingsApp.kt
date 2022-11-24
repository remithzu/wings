package com.rmtz.wings.app

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class WingsApp: MultiDexApplication() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}