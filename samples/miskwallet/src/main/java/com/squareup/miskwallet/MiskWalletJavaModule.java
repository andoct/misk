package com.squareup.miskwallet;

import com.google.inject.AbstractModule;
import misk.web.WebActionModule;

public class MiskWalletJavaModule extends AbstractModule {
  @Override protected void configure() {
    // TODO how to handle deprecating WebActionModule
    install(WebActionModule.create(HelloJavaAction.class));
  }
}
