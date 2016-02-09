package spice.macros


/**
  *
//  */
//class MyMacro[T, R] {
//  def m(x: T): R = macro implRef
//  def assert(cond: Boolean, msg: Any) = macro Asserts.assertImpl
//  assertImpl(c)(<[ x < 10 ]>, <[ “limit exceeded” ]>)
//}
//
//import scala.reflect.macros
//import scala.language.experimental.macros
//
//object Asserts {
//  def raise(msg: Any) = throw new AssertionError(msg)
//  def assertImpl(c: Context)
//                (cond: c.Expr[Boolean], msg: c.Expr[Any]) : c.Expr[Unit] =
//    if (assertionsEnabled)
//      <[ if (!cond) raise(msg) ]>
//  else
//  <[ () ]>
//}


import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation


object helloMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._
    val result = {
      annottees.map(_.tree).toList match {
        case q"object $name extends ..$parents { ..$body }" :: Nil =>
          q"""
            object $name extends ..$parents {
              def hello: ${typeOf[String]} = "hello"
              ..$body
            }
          """
      }
    }
    c.Expr[Any](result)
  }
}

class hello extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro helloMacro.impl
}

