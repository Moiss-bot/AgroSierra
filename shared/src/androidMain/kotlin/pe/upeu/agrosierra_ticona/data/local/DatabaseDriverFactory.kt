package pe.upeu.agrosierra_ticona.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AgroSierraDatabase.Schema,
            context = context,
            name = "agrosierra.db"
        )
    }
}
