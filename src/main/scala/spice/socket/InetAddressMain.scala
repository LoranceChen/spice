package spice.socket

import java.net.{UnknownHostException, InetAddress}

/**
  *Internet protocol uses the IP addresses to deliver packets.
  * http://www.java2s.com/Tutorials/Java/Java_Network/index.html
  * todo:what's the CanonicalHostName? what situation should use Canonical HostName?
  */
object InetAddressMain extends App {
  // Print www.yahoo.com address details
  printAddressDetails("www.baidu.com")

  // Print the loopback address details
  printAddressDetails(null)

  // Print the loopback address details using IPv6 format
  printAddressDetails("::1")

  def printAddressDetails(host: String) = {
    System.out.println("Host '" + host + "' details starts...")
    val addr = InetAddress.getByName(host)
    System.out.println("Host  IP  Address: " + addr.getHostAddress)
    System.out.println("Canonical  Host  Name: "
      + addr.getCanonicalHostName)

    val timeOutinMillis = 10000
    System.out.println("isReachable(): "
      + addr.isReachable(timeOutinMillis))
    System.out.println("isLoopbackAddress(): " + addr.isLoopbackAddress)
  }

  /**
    * http://www.avajava.com/tutorials/lessons/how-do-i-use-a-host-name-to-look-up-an-ip-address.html
    */
  stuff("www.baidu.com")

  def stuff(host: String) = {
    try {
      val inetLocalAddress = InetAddress.getLocalHost
      displayStuff("local host", inetLocalAddress)
      System.out.print("--------------------------")
      val inetAddress = InetAddress.getByName(host)
      displayStuff(host, inetAddress)
      System.out.print("--------------------------")
      val inetAddressArray = InetAddress.getAllByName(host)

      var x = 1
      for (iAddr <- inetAddressArray) {
        x = x + 1
        displayStuff(s"$host - $x", iAddr)
      }
    } catch {
      case e: UnknownHostException => e.printStackTrace();
    }
  }
  def displayStuff(whichHost: String, inetAddress: InetAddress) {
    System.out.println("--------------------------")
    System.out.println("Which Host:" + whichHost)
    System.out.println("Canonical Host Name:" + inetAddress.getCanonicalHostName)
    System.out.println("Host Name:" + inetAddress.getHostName)
    System.out.println("Host Address:" + inetAddress.getHostAddress)
  }
}
