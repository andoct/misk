package misk.web.resources

import misk.resources.ResourceLoader
import misk.scope.ActionScoped
import misk.security.authz.Unauthenticated
import misk.web.Get
import misk.web.Post
import misk.web.HttpCall
import misk.web.RequestContentType
import misk.web.Response
import misk.web.ResponseBody
import misk.web.ResponseContentType
import misk.web.actions.NotFoundAction
import misk.web.actions.WebAction
import misk.web.mediatype.MediaTypes
import misk.web.toResponseBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okio.BufferedSink
import okio.BufferedSource
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StaticResourceAction
 * This data class is used with Guice multibindings. Register instances by calling `multibind()`
 * in a `KAbstractModule`:
 * ```
 * multibind<StaticResourceEntry>().toInstance(StaticResourceEntry(...))
 * ```
 */
@Singleton
class StaticResourceAction @Inject constructor(
  @JvmSuppressWildcards private val clientHttpCall: ActionScoped<HttpCall>,
  private val resourceLoader: ResourceLoader,
  private val resourceEntryFinder: ResourceEntryFinder
) : WebAction {

  @Get("/{path:.*}")
  @Post("/{path:.*}")
  @RequestContentType(MediaTypes.ALL)
  @ResponseContentType(MediaTypes.ALL)
  @Unauthenticated // TODO(adrw) https://github.com/square/misk/issues/429
  fun action(): Response<ResponseBody> {
    val httpCall = clientHttpCall.get()
    return getResponse(httpCall)
  }

  fun getResponse(httpCall: HttpCall): Response<ResponseBody> {
    val entry =
        (resourceEntryFinder.staticResource(httpCall.url) as StaticResourceEntry?
            ?: return NotFoundAction.response(httpCall.url.encodedPath().drop(1)))
    return MatchedResource(entry).getResponse(httpCall)
  }

  private enum class Kind {
    NO_MATCH,
    RESOURCE,
    RESOURCE_DIRECTORY,
  }

  private inner class MatchedResource(var matchedEntry: StaticResourceEntry) {
    fun getResponse(httpCall: HttpCall): Response<ResponseBody> {
      val urlPath = httpCall.url.encodedPath()
      return when (exists(urlPath)) {
        Kind.NO_MATCH -> when {
          !urlPath.endsWith("/") -> redirectResponse(normalizePathWithQuery(httpCall.url))
        // actually return the resource, don't redirect. Path must stay the same since this will be handled by React router
          urlPath.endsWith("/") -> resourceResponse(
              normalizePath(matchedEntry.url_path_prefix))
          else -> null
        }
        Kind.RESOURCE -> resourceResponse(urlPath)
        Kind.RESOURCE_DIRECTORY -> resourceResponse(normalizePathWithQuery(httpCall.url))
      } ?: NotFoundAction.response(httpCall.url.encodedPath().drop(1))
    }

    /** Returns true if the mapped path exists on either the resource path or file system. */
    private fun exists(urlPath: String): Kind {
      val resourcePath = matchedEntry.resourcePath(urlPath)
      if (resourceLoader.exists(resourcePath)) return Kind.RESOURCE
      if (resourceLoader.list(resourcePath).isNotEmpty()) return Kind.RESOURCE_DIRECTORY
      return Kind.NO_MATCH
    }

    /** Returns a source to the mapped path, or null if it doesn't exist. */
    fun open(urlPath: String): BufferedSource? {
      val resourcePath = matchedEntry.resourcePath(urlPath)
      return when {
        resourceLoader.exists(resourcePath) -> resourceLoader.open(resourcePath)!!
        else -> null
      }
    }

    private fun normalizePath(urlPath: String): String {
      return when {
        urlPath.endsWith("/") -> "${urlPath}index.html"
        !urlPath.endsWith("/") -> "$urlPath/"
        else -> urlPath
      }
    }

    private fun normalizePathWithQuery(url: HttpUrl): String {
      return if (url.encodedQuery().isNullOrEmpty()) normalizePath(url.encodedPath())
      else normalizePath(url.encodedPath()) + "?" + url.encodedQuery()
    }

    private fun resourceResponse(resourcePath: String): Response<ResponseBody>? {
      return when (exists(resourcePath)) {
        Kind.RESOURCE -> {
          val responseBody = object : ResponseBody {
            override fun writeTo(sink: BufferedSink) {
              open(resourcePath)!!.use {
                sink.writeAll(it)
              }
            }
          }
          Response(
              body = responseBody,
              headers = Headers.of("Content-Type", MediaTypes.fromFileExtension(
                  resourcePath.substring(resourcePath.lastIndexOf('.') + 1)).toString()))
        }
        else -> null
      }
    }

    private fun redirectResponse(urlPath: String): Response<ResponseBody> {
      return Response(
          body = "".toResponseBody(),
          statusCode = HttpURLConnection.HTTP_MOVED_TEMP,
          headers = Headers.of("Location", urlPath))
    }
  }
}
