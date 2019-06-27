package com.squareup.miskwallet

import misk.config.Config
import misk.jdbc.DataSourceClustersConfig
import misk.metrics.backends.prometheus.PrometheusConfig
import misk.web.WebConfig

data class MiskWalletConfig(
  val web: WebConfig,
  val prometheus: PrometheusConfig,
  val data_source_clusters: DataSourceClustersConfig
) : Config
