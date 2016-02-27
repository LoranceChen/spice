package spice.socket.test

import java.nio.ByteBuffer

/**
  *
  */
object ByteBufferTest extends App{
  // Create an empty ByteBuffer with a 10 byte capacity
  val bbuf = ByteBuffer.allocate(10);

  // Get the buffer's capacity
  val capacity = bbuf.capacity(); // 10

  // Use the absolute put().
  // This method effect the position.
  val y = bbuf.put(1,0x01.asInstanceOf[Byte]) // position=0

  println(y.getInt(1))
  // Set the position
  val z = bbuf.position(5);


  // Use the relative put()
  val q = bbuf.put(0xFF.asInstanceOf[Byte])

  // Get the new position
  val pos = bbuf.position(); // 6

  // Get remaining byte count
  val rem = bbuf.remaining(); // 4

  // Set the limit
  val m = bbuf.limit(7); // remaining=1

  // This convenience method sets the position to 0
  val xx = bbuf.rewind(); // remaining=7
  1
}
