package spice.language.type_tag

import scala.reflect.runtime.universe._

/**
  * components of its type representation can be references to type parameters or abstract types
  */
trait Tag {

  /**
    *
    * @param x
    * @param tag if not exist in context, compiler will create one
    * @tparam T
    */
  def paramInfoImp[T](x: T)(implicit tag: TypeTag[T]): Unit = {
    val targs = tag.tpe match { case TypeRef(_, _, args) => args }
    println(s"paramInfoImp - type of $x has type arguments $targs")
  }

  /**
    * above method simplify as this
    * @param x
    * @tparam T
    */
  def paramInfo[T: TypeTag](x: T): Unit = {
    val targs = typeOf[T] match { case TypeRef(_, _, args) => args }
    println(s"paramInfo - type of $x has type arguments $targs")
  }

  /**
    * Another use case
    * NOTE: at this scala version(2.11.7),T will recode automatically
    * @param x
    * @tparam T use TypeTag or not are sames
    */
  def matchInfo[T](x: T) = {
    x match {
      case i:Int => println("matchInfo - Int")
      case s:String => println("matchInfo - String")
      case "a" :: "b" :: Nil => println("""matchInfo - List("a", "b")""")
      case a :: b => println("matchInfo - 非空List")
      case _ => println("matchInfo - other type")
    }
  }
}

/**
  *
  */
object Test extends App with Tag{

  paramInfo(Vector("1"))

  matchInfo("a" :: "b" :: Nil)
  matchInfo(1)
  matchInfo(1::2::Nil)
  matchInfo(Vector("1"))


  //TODO mark form: http://stackoverflow.com/questions/12218641/scala-what-is-a-typetag-and-how-do-i-use-it
  class Foo
  class Bar extends Foo

  def meth[A: TypeTag](xs: List[A]) = xs match {
    case _: List[String] => println("meth - list of strings")
    case _: List[Foo] => println("meth - list of foos")
    case _ => println("meth - other type")
  }

  //not enough yet
  meth[Foo](List(new Foo()))
}
