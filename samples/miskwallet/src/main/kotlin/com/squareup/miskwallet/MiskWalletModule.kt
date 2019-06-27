package com.squareup.miskwallet

import com.squareup.miskwallet.actions.EchoFormAction
import com.squareup.miskwallet.actions.HelloWebAction
import com.squareup.miskwallet.actions.HelloWebPostAction
import misk.inject.KAbstractModule
import misk.web.WebActionModule

class MiskWalletModule : KAbstractModule() {
  override fun configure() {
    install(WebActionModule.create<HelloWebAction>())
    install(WebActionModule.create<HelloWebPostAction>())
    install(WebActionModule.create<EchoFormAction>())
  }
}
