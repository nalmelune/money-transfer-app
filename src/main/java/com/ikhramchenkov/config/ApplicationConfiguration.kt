package com.ikhramchenkov.config

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

class ApplicationConfiguration(
    @JsonProperty("database") val dataSourceFactory: DataSourceFactory
) : Configuration()