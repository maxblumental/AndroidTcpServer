package com.blumental.mobileserver

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.SocketException

/**
 * An abstract TCP server that serves only one request and then stops.
 * It needs to be started again for accepting another request.
 * In order to implement the class you only need to override [run].
 */
abstract class OneOffTcpServer(private val port: Int) {

  private val lock = Any()
  private var socket: ServerSocket? = null

  fun start() {
    GlobalScope.launch(Dispatchers.IO) {
      val newSocket: ServerSocket
      synchronized(lock) {
        if (socket?.isClosed == false) {
          Log.e(TAG, "start(): a non-closed socket is present, ignore call")
          return@launch
        }

        try {
          newSocket = ServerSocket(port)
        } catch (e: SocketException) {
          Log.e(TAG, "caught exception during socket creation", e)
          return@launch
        }

        socket = newSocket
      }

      try {
        val clientSocket = newSocket.accept()
        Log.d(TAG, "accepted ${clientSocket.inetAddress}")
        clientSocket?.usingIo { input, output ->
          run(input, output)
          Log.d(TAG, "stopped")
        }
      } catch (e: SocketException) {
        Log.e(TAG, "caught exception while working with socket", e)
      }
    }
  }

  /**
   * Logic of serving a client.
   *
   * @param input - read from client socket
   * @param output - write to client socket
   */
  protected abstract fun run(input: BufferedReader, output: PrintWriter)

  fun shutdown() = synchronized(lock) {
    if (socket?.isBound == true) {
      socket?.close()
    }
    socket = null
  }
}

private const val TAG = "OneOffTcpServer"
