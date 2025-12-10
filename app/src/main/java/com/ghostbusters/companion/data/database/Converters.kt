package com.ghostbusters.companion.data.database

import androidx.room.TypeConverter
import com.ghostbusters.companion.data.database.entities.TrappedGhostData
import com.ghostbusters.companion.domain.model.CharacterName
import com.ghostbusters.companion.domain.model.ExpansionType
import com.ghostbusters.companion.domain.model.GameType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGameType(value: GameType): String = value.name

    @TypeConverter
    fun toGameType(value: String): GameType = GameType.valueOf(value)

    @TypeConverter
    fun fromExpansionTypeList(value: List<ExpansionType>): String = gson.toJson(value)

    @TypeConverter
    fun toExpansionTypeList(value: String): List<ExpansionType> {
        if (value.isEmpty() || value == "null") return emptyList()
        return try {
            val listType = object : TypeToken<List<ExpansionType>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromCharacterName(value: CharacterName): String = value.name

    @TypeConverter
    fun toCharacterName(value: String): CharacterName = CharacterName.valueOf(value)

    @TypeConverter
    fun fromTrappedGhostList(value: List<TrappedGhostData>): String = gson.toJson(value)

    @TypeConverter
    fun toTrappedGhostList(value: String): List<TrappedGhostData> {
        if (value.isEmpty() || value == "null") return emptyList()
        return try {
            val listType = object : TypeToken<List<TrappedGhostData>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

