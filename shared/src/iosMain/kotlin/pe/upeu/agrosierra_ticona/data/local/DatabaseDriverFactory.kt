package pe.upeu.agrosierra_ticona.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AgroSierraDatabase.Schema,
            name = "agrosierra.db"
        )
    }
}
