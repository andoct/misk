package com.squareup.miskwallet.model

import misk.hibernate.DbTimestampedEntity
import misk.hibernate.DbUnsharded
import misk.hibernate.Id
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Table

@Entity
@Table(name = "wallet_key_trees")
class DbWalletKeyTree() : DbUnsharded<DbWalletKeyTree>, DbTimestampedEntity {
  @javax.persistence.Id
  @GeneratedValue
  override lateinit var id: Id<DbWalletKeyTree>

  @Column(nullable = false, updatable = false)
  lateinit var seed: ByteArray

  @Column
  override lateinit var created_at: Instant

  @Column
  override lateinit var updated_at: Instant

  constructor(
    seed: ByteArray
  ) : this() {
    this.seed = seed
  }

//  fun toModel() = Customer(
//      id = id,
//      token = token
//  )
}
