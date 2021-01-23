package io.github.openminigameserver.replay.io.entity

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JacksonAnnotationsInside
@JsonSerialize(keyUsing = EntityKeySerializer::class)
@JsonDeserialize(keyUsing = EntityKeyDeserializer::class)
@JsonIdentityReference(alwaysAsId = true)
annotation class RecordableEntityAsKey
