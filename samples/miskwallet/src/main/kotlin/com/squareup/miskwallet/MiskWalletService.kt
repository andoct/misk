package com.squareup.miskwallet

import com.squareup.miskwallet.scaffolding.StorageModule
import misk.MiskApplication
import misk.MiskRealServiceModule
import misk.config.ConfigModule
import misk.config.MiskConfig
import misk.environment.Environment
import misk.environment.EnvironmentModule
import misk.metrics.backends.prometheus.PrometheusMetricsModule
import misk.web.MiskWebModule

fun main(args: Array<String>) {
  val environment = Environment.fromEnvironmentVariable()
  val config = MiskConfig.load<MiskWalletConfig>("miskwallet", environment)
  MiskApplication(
      MiskRealServiceModule(),
      MiskWebModule(config.web),
      MiskWalletModule(),
      ConfigModule.create("miskwallet", config),
      EnvironmentModule(environment),
      PrometheusMetricsModule(config.prometheus),
      StorageModule(config)
  ).run(args)
}
