package com.ikhramchenkov

import com.ikhramchenkov.MoneyTransferApplication.Companion.ROOT_PACKAGE
import com.ikhramchenkov.config.ApplicationConfiguration
import io.dropwizard.ConfiguredBundle
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.hibernate.ScanningHibernateBundle
import javax.inject.Singleton

@Singleton
class ApplicationHibernateBundle :
    ScanningHibernateBundle<ApplicationConfiguration>(ROOT_PACKAGE), ConfiguredBundle<ApplicationConfiguration> {

    override fun getDataSourceFactory(configuration: ApplicationConfiguration): DataSourceFactory {
        return configuration.dataSourceFactory
    }

}