package com

import com.Util.{getWeatherFromInt, getWeatherFromString}
import org.jsoup.Jsoup
import weather.{Weather, WeatherSource}

object Util {
  object Weather extends Enumeration {
    type Weather = Value
    val Rain, Clouds, Sun, Snow = Value
  }

  import Weather._
  def getWeatherFromString(description: String): String = {
    (description.toLowerCase() match {
      case description if description.contains("rain") => Weather.Rain
      case description if description.contains("cloud") => Weather.Clouds
      case description if description.contains("snow") => Weather.Snow
      case _ => Weather.Sun
    }).toString
  }
  def getWeatherFromInt(description: String): String = {
    if (description.replaceAll("[^0-9]", "").toInt > 0)
      Weather.Rain.toString
    else
      Weather.Sun.toString
  }
}

object Scraper {
  def getWeatherStatsChan5(day: Int): Weather = {
    println(s"chan5 day: $day")
    def selectorTemplate(day: Int, subSelector: String) = s"#body > div.ksl-header-menu-container > div.ksl-header-menu-container__content > div.pagebody > div > div.weatherContainerPanel > div > div > div.weatherPanelInfoContainer > div:nth-child(3) > div:nth-child(${2 * (day - 1) + 1}) > ${subSelector}"
    val lowSelector = selectorTemplate(day, "div.lowTemp")
    val highSelector = selectorTemplate(day, "div.highTemp")
    val weatherSelector = selectorTemplate(day, "div > picture > source")
    val doc = Jsoup.connect("https://www.ksl.com/weather/forecast").get()
    val low = doc.select(lowSelector).text().trim.toInt
    val high = doc.select(highSelector).text().trim.toInt
    val weather = getWeatherFromString(doc.select(weatherSelector).attr("srcset"))
    val zeroBasedDaysOut = day - 1
    Weather(high, low, weather, WeatherSource.chan5.toString, daysOut = zeroBasedDaysOut)
  }

  def getWeatherStatsChan2(day: Int): Weather = {
    println(s"chan2 day: $day")
    def selectorTemplate(day: Int, subSelector: String) = s".WeatherWeek-module_WeekContainer__hkVI > div:nth-child(${day})  $subSelector"
    val lowSelector = selectorTemplate(day, "div[class^=WeatherDay-module_low]")
    val highSelector = selectorTemplate(day, "div[class^=WeatherDay-module_highlow]")
    val weatherSelector = selectorTemplate(day, "div[class^=WeatherDay-module_precip]")
    val doc = Jsoup.connect("https://kutv.com/weather").get()
    val high = doc.select(highSelector)
    val Array(highVal, lowVal) = high.text().trim.replaceAll("[^0-9 ]","").split(" ").map(_.toInt)
    val weather = getWeatherFromInt(doc.select(weatherSelector).text())
    val zeroBasedDaysOut = day - 1
    Weather(highVal, lowVal, weather, WeatherSource.chan2.toString, daysOut = zeroBasedDaysOut)
  }

}
