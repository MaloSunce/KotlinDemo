package com.example.project_test.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/*
* Checks the devices internet connectivity
*
* @return true for successful connectivity, false otherwise
*/
@Composable
@OptIn(ExperimentalCoroutinesApi::class)
fun CheckConnectivityStatus(): Boolean {
    val connection by connectivityStatus()

    return connection === ConnectionStatus.Available
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityStatus(): State<ConnectionStatus> {
    val mCtx = LocalContext.current

    return produceState(initialValue = mCtx.currentConnectivityStatus) {
        mCtx.observeConnectivityAsFlow().collect { status: ConnectionStatus ->
            value = status
        }
    }
}

val Context.currentConnectivityStatus: ConnectionStatus
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityStatus(connectivityManager)
    }

private fun getCurrentConnectivityStatus(
    connectivityManager: ConnectivityManager
): ConnectionStatus {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) {
        ConnectionStatus.Available
    }
    else {
        ConnectionStatus.Unavailable
    }
}

fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = NetworkCallback { connectionState -> trySend(connectionState)}

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, callback)

    val currentState = getCurrentConnectivityStatus(connectivityManager)
    trySend(currentState)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

fun NetworkCallback(callback: (ConnectionStatus) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
           callback(ConnectionStatus.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionStatus.Unavailable)
        }
    }
}
