package com.photoshooto.data.di

import com.photoshooto.data.repository.HomeRepository
import com.photoshooto.data.source.remote.HomeRepo
import org.koin.dsl.module

val repositoryModule = module {
    single<HomeRepo> { HomeRepository(get(), get()) }
}