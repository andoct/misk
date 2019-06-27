package com.squareup.miskwallet;

import com.google.common.collect.ImmutableList;
import misk.MiskApplication;
import misk.MiskRealServiceModule;
import misk.config.ConfigModule;
import misk.config.MiskConfig;
import misk.environment.Environment;
import misk.environment.EnvironmentModule;
import misk.resources.ResourceLoader;
import misk.web.MiskWebModule;

public class MiskWalletJavaApp {
  public static void main(String[] args) {
    Environment environment = Environment.fromEnvironmentVariable();
    MiskWalletJavaConfig config = MiskConfig.load(MiskWalletJavaConfig.class, "miskwallet",
        environment, ImmutableList.of(), ResourceLoader.Companion.getSYSTEM());

    new MiskApplication(
        new MiskRealServiceModule(),
        new MiskWebModule(config.web),
        new MiskWalletJavaModule(),
        new ConfigModule<>(MiskWalletJavaConfig.class, "miskwallet", config),
        new EnvironmentModule(environment)
    ).run(args);
  }
}
