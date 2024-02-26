package io.github.terickson87.croprecordsservice.domain.handlers.notes

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class IdValidatorTest : BehaviorSpec ({

    given("Integer ID") {
        val input = IdValidator.Input(17.toString())

        `when`("validate") {
            val result = IdValidator.validate(input)

            then("returns Success")
            result.shouldBeTypeOf<IdValidator.Output.Success>().also {
                it.id.shouldBe(17)
            }
        }
    }

    given("Non Integer ID") {
        val input = IdValidator.Input("String")

        `when`("validate") {
            val result = IdValidator.validate(input)

            then("returns Output.BadId")
            result.shouldBeTypeOf<IdValidator.Output.BadId>().also {
                it.id.shouldBe(input.id)
            }
        }
    }

    given("null id") {
        val input = IdValidator.Input(null)

        `when`("handle") {
            val result = IdValidator.validate(input)

            then("returns Output.MissingID")
            result.shouldBeTypeOf<IdValidator.Output.MissingID>()
        }
    }
})