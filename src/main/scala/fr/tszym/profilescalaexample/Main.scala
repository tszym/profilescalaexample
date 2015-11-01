package fr.tszym.profilescalaexample

import java.util.concurrent.TimeUnit

import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import nl.grons.metrics.scala.InstrumentedBuilder

object Main {
  def main(args: Array[String]): Unit = {
    val s = (1 to 10).toSeq

    s.foreach {
      x => println(s"OUTPUT => ${TheApplication.computation(x)}")
    }
  }
}

/**
 * This is an utils object
 * (could be that object where we put all your utils functions)
 */
object MetricsContainer {

  /**
   * This is a registry that will be used all around the application.
   * We will create metrics with it.
   */
  val metricRegistry = {
    val registry = new MetricRegistry()

    // This is the easiest metrics reporter. It will print to the console.
    // We can specify rate bases and the frequency of reports (in start).
    ConsoleReporter.forRegistry(registry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
      .start(5, TimeUnit.SECONDS)

    registry
  }
}

/**
 * This trait has to be mixed with the classes and objects you want to profile.
 * Then the `metrics` property will be available in these classes & objects.
 *
 * Here we see that the definition of the registry is in an object in order to
 * have only one instance.
 */
trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = MetricsContainer.metricRegistry
}

/**
 * This is an example object we want to profile.
 */
object TheApplication extends Instrumented {

  // Here we declare a timer
  private[this] val computationTimer = metrics.timer("label of the timer")
  // And a counter
  private[this] val computationCounter = metrics.counter("label of the counter")

  def wait5seconds(): Unit = {
    try {
      Thread.sleep(5 * 1000)
    } catch {
      case e: InterruptedException =>
    }
  }

  /**
   * An expensive computation.
   * We profile its execution time.
   * We also count the times it is called with an increment on a counter.
   * This counter is redundant here because the timer already has one,
   * but this is an example.
   */
  def computation(x: Int): Int = {
    computationCounter.inc()

    computationTimer.time {
      wait5seconds()
      x * 2 + 1
    }
  }

  /**
   * An other way to time an entire function
   */
  def otherComputation(x: Int): Int = computationTimer.time {
    wait5seconds()
    x * 2 + 1
  }
}
