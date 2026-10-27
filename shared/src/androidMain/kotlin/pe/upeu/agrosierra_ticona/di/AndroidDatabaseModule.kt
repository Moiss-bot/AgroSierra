package pe.upeu.agrosierra_ticona.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.dsl.module

import pe.upeu.agrosierra_ticona.data.local.DatabaseDriverFactory

val androidDatabaseModule = module {

    single<SqlDriver> {
        DatabaseDriverFactory(get()).createDriver()
    }
}
