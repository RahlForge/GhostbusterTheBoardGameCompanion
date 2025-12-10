package com.ghostbusters.companion.domain.model

data class Ghost(
    val id: String,
    val rating: Int, // 1, 2, or 3
    val name: String = "Ghost"
)

data class CharacterAbility(
    val level: Level,
    val description: String
) {
    companion object {
        fun getDefaultAbilities(characterName: CharacterName): List<CharacterAbility> {
            return listOf(
                CharacterAbility(Level.LEVEL_1, "Level 1 Ability - To be defined"),
                CharacterAbility(Level.LEVEL_2, "Level 2 Ability - To be defined"),
                CharacterAbility(Level.LEVEL_3, "Gain 1 Extra Action"),
                CharacterAbility(Level.LEVEL_4, "Level 4 Ability - To be defined"),
                CharacterAbility(Level.LEVEL_5, "Level 5 Ability - To be defined")
            )
        }
    }
}

