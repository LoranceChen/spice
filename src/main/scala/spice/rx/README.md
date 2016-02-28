## Rx Style Guide
### When to use Rx?
1. If out of our program will get a series of event from our environment and we will operate these event for different purpose.
Such as click-event from mouse and we deal special combines by click spare time and count(eg. third-right-click aim to X skill :),
or keyboard-event also useful and expressive with Rx Style.
2. If we want do some things, not decide yet, according to a operation result.Its also useful by Rx.
At this condition, we could call the "according operation" to a Observable/Theme and call "some things" as Observer.
We can use Observable.subscribe(Observer) do one things, I think it will be easy understand if could write Observer.subscribe(Observable)

### Difference with Future?
Future used at - we do a operation and the result will back. Besides, we able to handle the will-result continue our works.
Such as Async and any other task long-time-cost operation.
Future for Async will make call-back warped in Promise and make call-back more straight then use a call-back function barely.
More popularly saying, Future do long-time-cost task using put them to another thread and continue work with them at current thread.

As for Rx, above we have see, it master at a out system events stream.
But Rx also can transfer Future to Rx style that will be meaningful if we will do a series of not decide things according the future result.

### Detail use case
#### Adapt call-back
Put call-back method body to `onNext` body.

####Hot and Cold observables: ——————Chapter6 - Subscriptions
"In Rx, Observable objects that only emit events only when subscriptions exist are called cold observables.
On the other hand, some Observable objects emit events even when there are no associated subscriptions."
keyboard or mouse events are hot observables.
The advantage of using a hot observable is not need to instantiate multiple theme objects.
(根据这个原则,可以通过触发事件是否使用了create方法进行粗略判断)

####对Observable[Observable[String]]的订阅：
1. concat: 以外部事件触发的顺序合并内部事件
::在一个依赖先后顺序的事件中需要使用这种方式,以确保先前的事件触发了.
::如果一个事件触发失败,它之后的事件是不会触发的.
2. flatten: 以内部事件触发的先后顺序为准
::先后顺序无关时使用