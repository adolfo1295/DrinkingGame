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
  val answer: String,
  val options: List<String>? = null,
  val penalty: Int
) : CardContentDto

@Serializable
data class ChallengeContentDto(
  val title: String,
  val description: String,
  val penalty: Int
) : CardContentDto

@Serializable
data class RuleContentDto(
  val title: String,
  val rule: String,
  val duration: String? = null
) : CardContentDto

/**
 * Nota técnica: Supabase devuelve el JSON con el campo 'type' en la raíz.
 * Sin embargo, como 'content' es un objeto anidado, el serializador estándar 
 * de Kotlinx no tiene acceso al 'type' del padre fácilmente sin un custom serializer 
 * en el padre o una estructura aplanada. 
 * 
 * Para solucionar el error 'null', ajustaremos el selector para que maneje la 
 * estructura interna o lanzaremos una excepción descriptiva.
 */
object CardContentSerializer :
  JsonContentPolymorphicSerializer<CardContentDto>(CardContentDto::class) {
  override fun selectDeserializer(element: JsonElement): kotlinx.serialization.DeserializationStrategy<CardContentDto> {
    val jsonObject = element.jsonObject

    // Intentamos determinar el tipo por las llaves presentes en 'content'
    // ya que el 'type' está en el objeto padre (CardDto)
    return when {
      "question" in jsonObject -> TriviaContentDto.serializer()
      "description" in jsonObject -> ChallengeContentDto.serializer()
      "rule" in jsonObject -> RuleContentDto.serializer()
      else -> throw Exception("No se pudo determinar el tipo de contenido. Llaves: ${jsonObject.keys}")
    }
  }
}
