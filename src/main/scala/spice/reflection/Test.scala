package spice.reflection

import scala.reflect.runtime.{universe => ru}

/**
  * http://docs.scala-lang.org/overviews/reflection/overview.html
  * reflection has the ability to:
  *  1. inspect the type of that object, including generic types,
  *  2. to instantiate new objects,
  *  3. or to access or invoke members of that object.
  */
class Reflection {
  /**
    * Inspecting a Runtime Type (Including Generic Types at Runtime)
    */
  def inspectType = {
    val l = List(1, 2, 3)
    def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
    //get variable type by universe.TypeTag
    val theType = getTypeTag(l).tpe
    //get members of theType
    val decls = theType.decls.take(10)
  }

  /**
    * Instantiating a Type at Runtime
    */
  def instante = {
    case class Person(name: String)
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val classPerson = ru.typeOf[Person].typeSymbol.asClass
    val cm = m.reflectClass(classPerson)
    val ctor = ru.typeOf[Person].decl(ru.termNames.CONSTRUCTOR).asMethod
    val ctorm = cm.reflectConstructor(ctor)
    val p = ctorm("Mike")
  }
  /**
    * Accessing and Invoking Members of Runtime Types
    */
  def access = {
    case class Purchase(name: String, orderNumber: Int, var shipped: Boolean)
    val p = Purchase("Jeff Lebowski", 23819, false)
    val m = ru.runtimeMirror(p.getClass.getClassLoader)
    val shippingTermSymb = ru.typeOf[Purchase].decl(ru.TermName("shipped")).asTerm
    val im = m.reflect(p)
    val shippingFieldMirror = im.reflectField(shippingTermSymb)
    shippingFieldMirror.get
    shippingFieldMirror.set(true)
    shippingFieldMirror.get
  }

  /**
    * Runtime Classes in Java vs. Runtime Types in Scala
    */
  def javaVSScala = {
    class E {
      type T
      val x: Option[T] = None
    }

    class C extends E
    class D extends C

    val c = new C {
      type T = String
    }
    val d = new D {
      type T = String
    }

    //using Java reflection on Scala classes might return surprising or incorrect results.
    c.getClass.isAssignableFrom(d.getClass)

    // in Scala, we obtain runtime types.
    def m[T: ru.TypeTag, S: ru.TypeTag](x: T, y: S): Boolean = {
      val leftTag = ru.typeTag[T]
      val rightTag = ru.typeTag[S]
      leftTag.tpe <:< rightTag.tpe
    }
    m(d, c)
  }
}
