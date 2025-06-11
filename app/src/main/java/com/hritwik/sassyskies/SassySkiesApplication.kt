package com.hritwik.sassyskies

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SassySkiesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}