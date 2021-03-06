package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Play.current
import scala.util.matching.Regex
import play.api.Play
import scala.collection.JavaConverters._
import org.webjars.WebJarAssetLocator

/**
 * A Play framework controller that is able to resolve WebJar paths.
 * <p>org.webjars.play.webJarFilterExpr can be used to declare a regex for the
 * files that should be looked for when searching within WebJars. By default
 * all files are searched for.
 */
class WebJarAssets(assetsBuilder: AssetsBuilder) extends Controller {

  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"

  val webJarFilterExpr = current.configuration.getString(WebjarFilterExprProp).getOrElse(WebjarFilterExprDefault)

  val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, Play.application.classloader))

  /**
   * Controller Method to serve a WebJar asset
   * 
   * @param file the file to serve
   * @return the Action that serves the file
   */
  def at(file: String): Action[AnyContent] = {
    assetsBuilder.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)
  }

  /**
   * Locate a file in a WebJar
   *
   * @example Passing in `jquery.min.js` will return `jquery/1.8.2/jquery.min.js` assuming the jquery WebJar version 1.8.2 is on the classpath
   *
   * @param file the file or partial path to find
   * @return the path to the file (sans-the webjars prefix)
   *
   */
  def locate(file: String): String = {
    webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

  /**
   * Locate a file in a WebJar
   *
   * @param webjar the WebJar artifactId
   * @param file the file or partial path to find
   * @return the path to the file (sans-the webjars prefix)
   *
   */
  def locate(webjar: String, file: String): String = {
    webJarAssetLocator.getFullPath(webjar, file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

}

object WebJarAssets extends WebJarAssets(Assets)
