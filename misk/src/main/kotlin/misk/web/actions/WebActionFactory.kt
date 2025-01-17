package misk.web.actions

import com.google.inject.Injector
import com.google.inject.Provider
import misk.ApplicationInterceptor
import misk.MiskDefault
import misk.asAction
import misk.scope.ActionScope
import misk.web.BoundAction
import misk.web.ConnectWebSocket
import misk.web.DispatchMechanism
import misk.web.Get
import misk.web.Grpc
import misk.web.NetworkInterceptor
import misk.web.PathPattern
import misk.web.Post
import misk.web.WebActionBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

@Singleton
internal class WebActionFactory @Inject constructor(
  private val injector: Injector,
  private val userProvidedApplicationInterceptorFactories: List<ApplicationInterceptor.Factory>,
  private val userProvidedNetworkInterceptorFactories: List<NetworkInterceptor.Factory>,
  @MiskDefault private val miskNetworkInterceptorFactories: List<NetworkInterceptor.Factory>,
  @MiskDefault private val miskApplicationInterceptorFactories: List<ApplicationInterceptor.Factory>,
  private val webActionBindingFactory: WebActionBinding.Factory,
  private val scope: ActionScope
) {

  /** Returns the bound actions for `webActionClass`. */
  fun <A : WebAction> newBoundAction(
    webActionClass: KClass<A>,
    pathPrefix: String = "/"
  ): List<BoundAction<A>> {
    // Find the function with Get, Post, or ConnectWebSocket annotation. Only one such function is
    // allowed.
    val actionFunctions = webActionClass.members.mapNotNull {
      if (it.findAnnotation<Get>() != null ||
          it.findAnnotation<Post>() != null ||
          it.findAnnotation<ConnectWebSocket>() != null ||
          it.findAnnotation<Grpc>() != null) {
        it as? KFunction<*>
            ?: throw IllegalArgumentException("expected $it to be a function")
      } else null
    }

    require(actionFunctions.isNotEmpty()) {
      "no @Get, @Post, @ConnectWebSocket, or @Grpc annotations on ${webActionClass.simpleName}"
    }

    require(actionFunctions.size == 1) {
      val actionFunctionNames = actionFunctions.joinToString(", ") { it.name }
      "multiple annotated methods on ${webActionClass.simpleName}: $actionFunctionNames"
    }

    // Bind providers for each supported HTTP method.
    val actionFunction = actionFunctions.first()
    val get = actionFunction.findAnnotation<Get>()
    val post = actionFunction.findAnnotation<Post>()
    val connectWebSocket = actionFunction.findAnnotation<ConnectWebSocket>()
    val grpc = actionFunction.findAnnotation<Grpc>()

    // TODO(adrw) fix this using first provider below so that WebAction::class or WebAction can be passed in
    // val provider = Providers.of(theInstance)

    val provider = injector.getProvider(webActionClass.java)

    val result: MutableList<BoundAction<A>> = mutableListOf()

    require(pathPrefix.last() == '/')
    val effectivePrefix = pathPrefix.dropLast(1)

    if (get != null) {
      result += newBoundAction(provider, actionFunction,
          effectivePrefix + get.pathPattern, DispatchMechanism.GET)
    }
    if (post != null) {
      result += newBoundAction(provider, actionFunction,
          effectivePrefix + post.pathPattern, DispatchMechanism.POST)
    }
    if (connectWebSocket != null) {
      result += newBoundAction(provider, actionFunction,
          effectivePrefix + connectWebSocket.pathPattern, DispatchMechanism.WEBSOCKET)
    }
    if (grpc != null) {
      result += newBoundAction(provider, actionFunction,
          effectivePrefix + grpc.pathPattern, DispatchMechanism.GRPC)
    }

    return result
  }

  private fun <A : WebAction> newBoundAction(
    provider: Provider<A>,
    function: KFunction<*>,
    pathPattern: String,
    dispatchMechanism: DispatchMechanism
  ): BoundAction<A> {
    // NB: The response media type may be omitted; in this case only generic return types (String,
    // ByteString, ResponseBody, etc) are supported
    val action = function.asAction(dispatchMechanism)

    // Ensure that default interceptors are called before any user provided interceptors
    val networkInterceptors =
        miskNetworkInterceptorFactories.mapNotNull { it.create(action) } +
            userProvidedNetworkInterceptorFactories.mapNotNull { it.create(action) }

    val applicationInterceptors =
        miskApplicationInterceptorFactories.mapNotNull { it.create(action) } +
            userProvidedApplicationInterceptorFactories.mapNotNull { it.create(action) }

    val parsedPathPattern = PathPattern.parse(pathPattern)

    val webActionBinding = webActionBindingFactory.create(action, parsedPathPattern)

    return BoundAction(
        scope,
        provider,
        networkInterceptors,
        applicationInterceptors,
        webActionBinding,
        parsedPathPattern,
        action
    )
  }
}
