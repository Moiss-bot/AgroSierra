
package pe.upeu.agrosierra_ticona.di

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine

import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase

import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.data.remote.KtorClientFactory

import pe.upeu.agrosierra_ticona.data.repository.LoteRepositoryImpl
import pe.upeu.agrosierra_ticona.data.repository.LaborRepositoryImpl
import pe.upeu.agrosierra_ticona.data.repository.SesionRepositoryImpl
import pe.upeu.agrosierra_ticona.data.repository.DatosPruebaRepository

import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

import pe.upeu.agrosierra_ticona.domain.usecase.ValidarLaborUseCase

import pe.upeu.agrosierra_ticona.presentation.login.LoginViewModel
import pe.upeu.agrosierra_ticona.presentation.lotes.LotesViewModel
import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteViewModel
import pe.upeu.agrosierra_ticona.presentation.detallelote.EliminarLaborViewModel
import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormViewModel

val appModule = module {

    // ==========================================
    // 1. CLIENTE HTTP KTOR
    // ==========================================

    single {
        KtorClientFactory.crearCliente(
            engine = get<HttpClientEngine>()
        )
    }

    // ==========================================
    // 2. SERVICIO REST AGROAPI
    // ==========================================

    single {
        AgroApiService(
            client = get()
        )
    }

    // ==========================================
    // 3. BASE DE DATOS SQLDELIGHT
    // ==========================================

    single {
        AgroSierraDatabase(
            driver = get<SqlDriver>()
        )
    }

    // ==========================================
    // 4. REPOSITORIO DE SESIÓN JWT
    // ==========================================

    single<SesionRepository> {
        SesionRepositoryImpl(
            apiService = get<AgroApiService>(),
            database = get<AgroSierraDatabase>()
        )
    }

    // ==========================================
    // 5. REPOSITORIO DE LOTES
    // ==========================================

    single<LoteRepository> {
        LoteRepositoryImpl(
            database = get(),
            apiService = get(),
            sesionRepository = get()
        )
    }

    // ==========================================
    // 6. REPOSITORIO DE LABORES
    // ==========================================

    single<LaborRepository> {
        LaborRepositoryImpl(
            database = get(),
            loteRepository = get(),
            validarLabor = get(),
            apiService = get(),
            sesionRepository = get()
        )
    }

    // ==========================================
    // 7. VALIDACIONES DE NEGOCIO
    // ==========================================

    factory {
        ValidarLaborUseCase()
    }

    // ==========================================
    // 8. VIEWMODEL LOGIN MULTIUSUARIO
    // ==========================================

    viewModel {
        LoginViewModel(
            sesionRepository = get()
        )
    }

    // ==========================================
    // 9. VIEWMODEL LOTES
    // ==========================================

    viewModel {
        LotesViewModel(
            loteRepository = get()
        )
    }

    // ==========================================
    // 10. VIEWMODEL DETALLE DE LOTE
    // ==========================================

    viewModel {
        DetalleLoteViewModel(
            loteRepository = get(),
            laborRepository = get()
        )
    }

    // ==========================================
    // 11. VIEWMODEL FORMULARIO LABOR
    // ==========================================

    viewModel {
        LaborFormViewModel(
            laborRepository = get()
        )
    }

    // ==========================================
    // 12. VIEWMODEL ELIMINAR LABOR
    // ==========================================

    viewModel {
        EliminarLaborViewModel(
            laborRepository = get()
        )
    }

    // ==========================================
    // 13. DATOS DE PRUEBA SQLDELIGHT
    // ==========================================

    single {
        DatosPruebaRepository(
            database = get()
        )
    }
}
