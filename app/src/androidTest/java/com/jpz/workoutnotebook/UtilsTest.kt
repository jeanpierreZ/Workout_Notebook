package com.jpz.workoutnotebook

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.platform.app.InstrumentationRegistry
import com.jpz.workoutnotebook.utils.MyUtils
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.inject


class UtilsTest : KoinComponent {

    private val myUtils: MyUtils by inject()

    @Test
    fun isOnlineTest() {
        // Context of the app under test
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        val connMgr = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connMgr.activeNetwork
        val activeNetwork = connMgr.getNetworkCapabilities(network)
        // Set wifi connection
        activeNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        assertTrue(myUtils.isOnline(context))
    }
}