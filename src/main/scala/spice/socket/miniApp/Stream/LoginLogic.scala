package spice.socket.miniApp.Stream

import java.nio.channels.AsynchronousSocketChannel

import rx.lang.scala.Observable
import spice.socket.miniApp.protocol.Login
import spice.socket.presentation.DeCoding

/**
  *
  */
class LoginLogic( val connectionObs: Observable[AsynchronousSocketChannel],
                  val readDeCodingObs: Observable[DeCoding[_]]) extends LoginStream {
  def listen: Unit = {
    connectionObs.subscribe()
  }
  /**
    * on client connected
    * send a greet to him
    *
    * @param login
    * @return
    */
  def authenIdentify(login: Login) = {
    if(login.account == "admin" && login.password == "12345")
      "welcome administrator"
    else
      s"hello - ${login.account}"
  }

  /**
    * on received a message from client
    * response client with a message contains prefix of "I hear,"
    */
  def response(login: Login, message: String, asynchronousSocketChannel: AsynchronousSocketChannel) = {
//    asynchronousSocketChannel.write(s"I hear, [${login.account}], you said [$message]")
  }

  /**
    * on received a message from client
    * println the message with prefix of s"${login.name} say - "
    */
  def printMessage(login: Login, message: String) = {
    println(s"]${login.account}] say [$message]")
  }

  connectionObs
}

trait LoginStream extends BaseStream {
  /** client connection obs */
  val connectionObs: Observable[AsynchronousSocketChannel]
  val readDeCodingObs: Observable[DeCoding[_]]
}