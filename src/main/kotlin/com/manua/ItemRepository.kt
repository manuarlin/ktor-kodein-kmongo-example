package com.manua

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import org.litote.kmongo.coroutine.CoroutineCollection

class ItemRepository(kodein: Kodein) {

    private val coroutineCollection by kodein.instance<CoroutineCollection<Item>>()

    fun findAll(): Flow<Item> = coroutineCollection
        .find()
        .publisher.asFlow()
}
