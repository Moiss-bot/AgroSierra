package pe.upeu.agrosierra_ticona.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.dsl.module

import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase
import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.data.remote.KtorClientFactory

import pe.upeu.agrosierra_ticona.data.repository.LoteRepositoryImpl
import pe.upeu.agrosierra_ticona.data.repository.LaborRepositoryImpl
import pe.upeu.agrosierra_ticona.data.repository.SesionRepositoryImpl

import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

import pe.upeu.agrosierra_ticona.domain.usecase.ValidarLaborUseCase

import pe.upeu.agrosierra_ticona.presentation.lotes.LotesViewModel

import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteViewModel
import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormViewModel
import pe.upeu.agrosierra_ticona.presentation.detallelote.EliminarLaborViewModel

val appModule = module {

    // 1. CLIENTE HTTP KTOR
    single {
        KtorClientFactory.crearCliente()
    }

    // 2. SERVICIO REST

    single {
        AgroApiService(
            client = get()
        )
    }

    // 3. BASE DE DATOS SQLDELIGHT

    single {
        AgroSierraDatabase(
            driver = get<SqlDriver>()
        )
    }

    // 4. REPOSITORIO DE LOTES

    single<LoteRepository> {
        LoteRepositoryImpl(
            database = get(),
            apiService = get(),
            sesionRepository = get()
        )
    }

    // ==========================================
    // 5. REPOSITORIO DE LABORES
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
    // 6. REPOSITORIO DE SESION
    // ==========================================

    single<SesionRepository> {
        SesionRepositoryImpl(
            apiService = get()
        )
    }

    // ==========================================
    // 7. VALIDACIONES DE NEGOCIO
    // ==========================================

    factory {
        ValidarLaborUseCase()
    }

    // ==========================================
    // 8. VIEWMODELS
    // ==========================================

    factory {
        LotesViewModel(
            loteRepository = get()
        )
    }
    factory {
        DetalleLoteViewModel(
            loteRepository = get(),
            laborRepository = get()
        )
    }
    factory {
        LaborFormViewModel(
            laborRepository = get()
        )
    }
    factory {
        EliminarLaborViewModel(
            laborRepository = get()
        )
    }
}
