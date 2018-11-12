package com.blumental.mobileserver

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun Socket.usingIo(body: (input: BufferedReader, writer: PrintWriter) -> Unit) =
    reader.use { input ->
      writer.use { output ->
        body(input, output)
      }
    }

private val Socket.reader: BufferedReader
  get() = BufferedReader(InputStreamReader(getInputStream()))

private val Socket.writer: PrintWriter
  get() = PrintWriter(getOutputStream(), true)