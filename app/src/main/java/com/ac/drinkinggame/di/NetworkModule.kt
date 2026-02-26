package com.ac.drinkinggame.di

import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.createKtorClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single { createKtorClient() }
    singleOf(::GameApiService)
}
