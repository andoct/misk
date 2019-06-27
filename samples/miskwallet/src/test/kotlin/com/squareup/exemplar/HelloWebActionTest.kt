package com.squareup.miskwallet

import com.squareup.miskwallet.actions.HelloResponse
import com.squareup.miskwallet.actions.HelloWebAction
import misk.testing.MiskTest
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@MiskTest
class HelloWebActionTest {
  @Test
  fun happyPath() {
    assertThat(HelloWebAction().hello("sandy", Headers.of(), null, null))
        .isEqualTo(HelloResponse("YO", "SANDY"))
  }
}
