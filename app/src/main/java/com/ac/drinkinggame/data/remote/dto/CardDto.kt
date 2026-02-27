package com.ac.drinkinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class CardDto(
  val id: String,
  @SerialName("category_id") val categoryId: String,
  val type: String,
  val content: CardContentDto
)

@Serializable(with = CardContentSerializer::class)
sealed interface CardContentDto

@Serializable
data class TriviaContentDto(
  val question: String,
  @SerialName("question_en") val questionEn: String? = null,
  val answer: String,
  @SerialName("answer_en") val answerEn: String? = null,
  val options: List<String>? = null,
  @SerialName("options_en") val optionsEn: List<String>? = null,
  val penalty: Int
) : CardContentDto

@Serializable
data class ChallengeContentDto(
  val title: String,
  @SerialName("title_en") val titleEn: String? = null,
  val description: String,
  @SerialName("description_en") val descriptionEn: String? = null,
  val penalty: Int
) : CardContentDto

@Serializable
data class RuleContentDto(
  val title: String,
  @SerialName("title_en") val titleEn: String? = null,
  val rule: String,
  @SerialName("rule_en") val ruleEn: String? = null,
  val duration: String? = null,
  @SerialName("duration_en") val durationEn: String? = null
) : CardContentDto

object CardContentSerializer :
  JsonContentPolymorphicSerializer<CardContentDto>(CardContentDto::class) {
  override fun selectDeserializer(element: JsonElement): kotlinx.serialization.DeserializationStrategy<CardContentDto> {
    val jsonObject = element.jsonObject
    return when {
      "question" in jsonObject -> TriviaContentDto.serializer()
      "description" in jsonObject -> ChallengeContentDto.serializer()
      "rule" in jsonObject -> RuleContentDto.serializer()
      else -> throw Exception("No se pudo determinar el tipo de contenido. Llaves: ${jsonObject.keys}")
    }
  }
}
