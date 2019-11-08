import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.JsValue

import scala.io.StdIn

object WebServer extends App {
  implicit val system = ActorSystem("graphql-server")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val route: Route = (post & path("graphql")) {
    entity(as[JsValue])(GraphQLServer.endpoint(_))
  } ~ getFromResource("graphigl.html")

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}