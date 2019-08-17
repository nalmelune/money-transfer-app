package com.ikhramchenkov

import com.hubspot.dropwizard.guice.GuiceBundle
import com.ikhramchenkov.config.GuiceModule
import com.ikhramchenkov.config.ApplicationBinder
import com.ikhramchenkov.config.ApplicationConfiguration
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class MoneyTransferApplication : Application<ApplicationConfiguration>() {

    override fun getName(): String {
        return "moneyTransfer"
    }

    override fun initialize(bootstrap: Bootstrap<ApplicationConfiguration>) {
        val guiceBundle = GuiceBundle.newBuilder<ApplicationConfiguration>()
            .addModule(GuiceModule())
            .setConfigClass(ApplicationConfiguration::class.java)
            .enableAutoConfig(ROOT_PACKAGE)
            .build()

        bootstrap.addBundle(guiceBundle)
    }

    override fun run(
        configuration: ApplicationConfiguration,
        environment: Environment
    ) {
        environment.jersey().packages(ROOT_PACKAGE)
        environment.jersey().register(ApplicationBinder())

    }

    companion object {

        const val ROOT_PACKAGE = "com.ikhramchenkov"

        @JvmStatic
        fun main(args: Array<String>) {
            MoneyTransferApplication().run(*args)
        }
    }
}
