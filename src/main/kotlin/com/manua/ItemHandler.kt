package com.manua

import kotlinx.coroutines.flow.Flow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ItemHandler(kodein: Kodein) {

    private val itemRepository by kodein.instance<ItemRepository>()

    fun get(): Flow<Item> {
        return itemRepository.findAll()
    }

}