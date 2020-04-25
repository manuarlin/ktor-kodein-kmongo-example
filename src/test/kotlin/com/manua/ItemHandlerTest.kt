package com.manua

import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class ItemHandlerTest : AnnotationSpec() {

    lateinit var itemRepository: ItemRepository

    lateinit var itemHandler: ItemHandler

    @BeforeEach
    fun beforeTest() {
        itemRepository = mockk(relaxUnitFun = true)
        every { itemRepository.findAll() } returns emptyFlow()
        itemHandler = ItemHandler(Kodein {
            bind<ItemRepository>() with singleton { itemRepository }
        })
    }

    @Test
    fun `should call repository`() {
        itemHandler.get()
        verify(exactly = 1) { itemRepository.findAll() }
    }
}