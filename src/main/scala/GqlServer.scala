import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import sangria.ast.Document
import sangria.execution._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import model.db.{AuthorDao, BookDao}
import sangria.marshalling.sprayJson._


object GraphQLServer {
  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {
    val JsObject(fields) = requestJSON
    val JsString(query) = fields("query")

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }

        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }

        complete(executeGraphQLQuery(queryAst, operation, variables))

      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }

  }

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext) = {
    Executor.execute(
      GqlSchema.schema,
      query,
      GqlContext(AuthorDao, BookDao),
      variables = vars,
      operationName = operation,
      deferredResolver = GqlSchema.resolver
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }

}
