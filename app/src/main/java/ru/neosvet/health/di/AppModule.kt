package ru.neosvet.health.di

import org.koin.dsl.module
import ru.neosvet.health.data.FakeRepository
import ru.neosvet.health.data.Repository
import ru.neosvet.health.viewmodel.AddViewModel
import ru.neosvet.health.viewmodel.ListViewModel

val appModule = module {
    single { ListViewModel(get()) }
    single { AddViewModel(get()) }
    single<Repository> { FakeRepository() }
}