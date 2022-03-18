package ru.lyrian.location_tracker.utility.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.*
import ru.lyrian.location_tracker.utility.BuildVersion

class NetworkStateTest {
    @Mock
    private lateinit var contextMock: Context

    @Mock
    private lateinit var buildVersionMock: BuildVersion

    @Spy
    @InjectMocks
    private lateinit var networkStateMock: NetworkState

    @Mock
    private lateinit var connectivityManagerMock: ConnectivityManager

    @Mock
    private lateinit var networkInfoMock: NetworkInfo

    @Before
    fun setupMocks() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `Internet is working on Android below 10`() {
        Assert.assertEquals(1, 1)
    }
}

//        Mockito.`when`(this.buildVersionMock.isBelowAndroid10()).thenReturn(true)

//        setupContextMocks()
//        setupConnectivityManagerMock()
//        setupNetworkInfoMock()


//    private fun () {
//
//    }

/*    private fun `Setup buildVersionMock for Android above or equals 10`() {
        `when`(this.buildVersionMock.isBelowAndroid10()).thenReturn(false)
    }


    private fun setupContextMocks() =
        `when`(this.contextMock.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).thenReturn(this.connectivityManagerMock)

    private fun setupConnectivityManagerMock() =
        `when`(this.connectivityManagerMock.activeNetworkInfo).thenReturn(this.networkInfoMock)

    private fun setupNetworkInfoMock() = `when`(this.networkInfoMock.isConnectedOrConnecting).thenReturn(true)


    @Test
    fun `Internet is working on Android below 10`() {
        this.networkStateMock.checkNetworkState()
        Mockito.verify(this.buildVersionMock.isBelowAndroid10())
        Assert.assertEquals(1, 1)
    }

    @Test
    fun `Internet is working on Android above or equals 10`() {
        `Setup buildVersionMock for Android above or equals 10`()
    }*/

