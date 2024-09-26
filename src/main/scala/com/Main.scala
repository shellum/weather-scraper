package com

import akka.actor.{Actor, ActorSystem, Props}
import com.Scraper.{getWeatherStatsChan2, getWeatherStatsChan5}

import java.util.Date
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class WeatherScraper extends Actor {
  def receive: Receive = {
    case "scrape" => {
      val start = new Date()
      println(s"Pulling weather at ${start.toString}")
      implicit val executionContext: ExecutionContext = ExecutionContext.global
      val funcs = List(getWeatherStatsChan2(_), getWeatherStatsChan5(_))
      val days = Range.inclusive(1, 7)

      val weatherFutures = for (f <- funcs; d <- days) yield Future {
        (f(d))
      }
      val weatherResults = Await.result(Future.sequence(weatherFutures), 180.seconds)
      for (day <- weatherResults) {
        println(s"storing data")
        MysqlUtil.runQuery("insert into weather (low, high, weather, channel, time, days_out) values (" + day.low + ", " + day.high + ", '" + day.weather + "', '" + day.source + "', now(), " + day.daysOut + ")")
      }
      println(weatherResults)
      MysqlUtil.closeConnection()
      val end = new Date()
      println(s"scrape took ${(end.getTime - start.getTime) / 1000} seconds")
    }
  }
}

object Main extends App {
  Class.forName("com.mysql.cj.jdbc.Driver")
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  val actorSystem = ActorSystem("weather-scraper")
  val actor = actorSystem.actorOf(Props[WeatherScraper], "weather-scraper")
  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 0.seconds,
    receiver = actor,
    message = "scrape",
    interval = 24.hours
  )
}