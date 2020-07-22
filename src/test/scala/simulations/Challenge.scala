package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

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

  val httpConf = http.baseUrl("https://localhost:8080/app/")
    .header("Accept", "application/json")

  def postNewVideoGame() = {
    feed()
      .exec(http("Create a video game")
        .post("videogames/")
        .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
        .check(status.is(200)))
      .pause(1)
  }

  def getVideoGameByName(name: String) = {

  }

  def deleteVideoGame(name: String) = {

  }

  val scn = scenario("Create, retrieve, delete and confirm deletion of video game")
    .exec(postNewVideoGame())
    .pause(5)
    .exec(getVideoGameByName())
    .pause(5)
    .exec(deleteVideoGame())
    .pause(5)
    .exec(getVideoGameByName())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}