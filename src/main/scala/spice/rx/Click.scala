package spice.rx

import scala.swing._
import scala.swing.event._
import rx.lang.scala._
import spice.concurrent._
/**
 *
 */
object SchedulerSwing extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Swing Observables"
    val button = new Button {
      text = "Click"
    }
    contents = button
    val buttonClicks = Observable.create[Button] { obs =>
      button.reactions += {
        case ButtonClicked(_) => obs.onNext(button)
      }
      Subscriber()
    }
    buttonClicks.subscribe(_ => log("button clicked"))
  }
}