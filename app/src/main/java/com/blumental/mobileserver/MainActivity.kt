package com.blumental.mobileserver

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import java.io.BufferedReader
import java.io.PrintWriter
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

  private lateinit var server: EchoServer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    server = EchoServer(port = 9123, handler = MessageHandler(findViewById(R.id.messages)))

    findViewById<Button>(R.id.run_button)
        .setOnClickListener {
          server.shutdown()
          server.start()
        }

    findViewById<Button>(R.id.cancel_button)
        .setOnClickListener {
          server.shutdown()
        }
  }
}

class MessageHandler(messages: TextView) : Handler() {

  private val messages: WeakReference<TextView> = WeakReference(messages)

  override fun handleMessage(msg: Message) {
    when (msg.what) {
      SENT_MSG -> appendMessage(msg.obj as String, " > server")
      RECV_MSG -> appendMessage(msg.obj as String, " > client")
    }
  }

  private fun appendMessage(text: String, from: String) {
    val textView = messages.get() ?: return
    val original = textView.text
    val new = "$from: $text"
    textView.text = if (original.isBlank()) new else "$original\n$new"
  }
}

private const val SENT_MSG: Int = 42
private const val RECV_MSG: Int = 43

class EchoServer(port: Int, private val handler: Handler) : OneOffTcpServer(port) {

  override fun run(input: BufferedReader, output: PrintWriter) {
    while (true) {
      val line = input.readLine() ?: break
      handler.obtainMessage()
          .apply { what = RECV_MSG; obj = line }
          .let(handler::sendMessage)
      val response = "Echo: $line"
      output.println(response)
      handler.obtainMessage()
          .apply { what = SENT_MSG; obj = response }
          .let(handler::sendMessage)
    }
  }
}