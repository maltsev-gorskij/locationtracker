package ru.lyrian.location_tracker.utility.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import ru.lyrian.location_tracker.utility.BuildVersion
import javax.inject.Inject

open class NetworkState @Inject constructor(private val context: Context, private val buildVersion: BuildVersion) {
    fun checkNetworkState(): Boolean = if (buildVersion.isBelowAndroid10()) {
        val connectivityManager = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        networkInfo?.isConnectedOrConnecting == true
    } else {
        val connectivityManager = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}