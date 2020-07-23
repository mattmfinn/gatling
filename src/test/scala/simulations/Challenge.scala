package simulations

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

/**
 * User Journey:
 * Create a new game
 * Get a single game (the new one)
 * Delete the game
 * Confirm game is deleted
 *
 * Requirements:
 * Create methods for all API calls
 * Checks & Assertions after each call
 * Pause time between each call
 * Load scenario with runtime params
 * Print message at start & end of test
 */
class Challenge extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  var idNumber = 21
  val random = new Random()
  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int) = {
    random.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random) = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val feeder = Iterator.continually(Map(
    "gameId" -> idNumber,
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, random),
    "reviewScore" -> random.nextInt(100),
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))

  def postNewVideoGame() = {
    feed(feeder)
      .exec(http("Create a video game")
        .post("videogames/")
        .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
        .check(status.is(200)))
      .pause(1)
  }

  def getVideoGameById(id: Int) = {
    exec(http("Get video game by id #")
    .get("videogames/" + id.toString)
    .check(status.is(500)))
      .pause(1)
  }

  def deleteVideoGame(id: Int) = {
    exec(http("Delete video game by id #")
    .delete("videogames/" + id.toString)
    .check(status.is(200)))
      .pause(1)
  }

  val scn = scenario("Create, retrieve, delete and confirm deletion of video game")
    .exec(postNewVideoGame())
    .exec(getVideoGameById(idNumber))
    .exec(deleteVideoGame(idNumber))
    .exec(getVideoGameById(idNumber))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}