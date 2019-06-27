package com.squareup.miskwallet.scaffolding

import com.squareup.miskwallet.MiskWalletConfig
import com.squareup.miskwallet.model.DbWalletKeyTree
import misk.hibernate.HibernateEntityModule
import misk.hibernate.HibernateModule
import misk.inject.KAbstractModule
import javax.inject.Qualifier

class StorageModule(
  private val config: MiskWalletConfig
) : KAbstractModule() {
  override fun configure() {
    for (cluster in config.data_source_clusters.values) {
      install(HibernateModule(MiskWallet::class, cluster.writer))
    }

    install(object : HibernateEntityModule(MiskWallet::class) {
      override fun configureHibernate() {
        addEntities(DbWalletKeyTree::class)
      }
    })
  }
}

@Qualifier
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class MiskWallet