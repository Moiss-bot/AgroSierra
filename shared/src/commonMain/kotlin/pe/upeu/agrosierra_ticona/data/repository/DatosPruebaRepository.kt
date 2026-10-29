
package pe.upeu.agrosierra_ticona.data.repository

import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase

class DatosPruebaRepository(
    private val database: AgroSierraDatabase
) {

    fun insertarLotesSiEstaVacio() {

        val queries = database.agroDbQueries

        database.transaction {

            val lotesExistentes = queries
                .seleccionarLotes()
                .executeAsList()

            if (lotesExistentes.isNotEmpty()) {
                return@transaction
            }

            queries.insertarLote(
                id = 1L,
                codigo = "LOT-001",
                nombre = "Lote de Palta",
                hectareas = 3.5,
                cultivo = "Palta Hass",
                valle = "Moquegua",
                campaniaCerrada = 0L,
                dosisMaximaPorHectarea = 25.0
            )

            queries.insertarLote(
                id = 2L,
                codigo = "LOT-002",
                nombre = "Lote de Uva",
                hectareas = 5.0,
                cultivo = "Uva",
                valle = "Ilo",
                campaniaCerrada = 0L,
                dosisMaximaPorHectarea = 20.0
            )

            queries.insertarLote(
                id = 3L,
                codigo = "LOT-003",
                nombre = "Lote de Olivo",
                hectareas = 2.8,
                cultivo = "Olivo",
                valle = "Torata",
                campaniaCerrada = 1L,
                dosisMaximaPorHectarea = 15.0
            )
        }
    }
}
