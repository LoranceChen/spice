package spice.socket.handle

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.CompletionHandler
import java.nio.charset.Charset

import spice.javasocket.SocketHelper
import spice.socket.session.{BufferState, Attachment}

/**
  */
class ReadWriteHandler extends CompletionHandler[Integer, Attachment] {
  /**
    * callback by AsynchronousSocketChannel.read(newAttach.buffer, newAttach, rwHandler);
    * read - write loop
    *
    * @param result normal read value is 5 , what's -1 means?
    * @param attach same as newAttach
    */
  def completed(result: Integer, attach: Attachment): Unit = {
    if (result == -1) {
      try {
        attach.client.close()
        SocketHelper.log(s"Stopped listening to the client - ${attach.client.getRemoteAddress} reason: result=-1")
      }
      catch {
        case ex: IOException =>
          SocketHelper.log(s"IOException - Stopped listening to the client - result=-1")
      }
    }
    else {
      attach.status match {
        case BufferState.Spare =>
          attach.status = BufferState.OnRead
          attach.buffer.flip
          val limits = attach.buffer.limit
          val bytes = new Array[Byte](limits)
          attach.buffer.get(bytes, 0, limits)
          val cs = Charset.forName("UTF-8")
          val msg = new String(bytes, cs)
          SocketHelper.log(s"Client says - $msg")

          attach.buffer.clear()
          attach.status = BufferState.Spare
        case _ => //enter wait queue
          Unit
      }
    }
  }

  def failed(e: Throwable, attach: Attachment) {
    e.printStackTrace
  }
}

