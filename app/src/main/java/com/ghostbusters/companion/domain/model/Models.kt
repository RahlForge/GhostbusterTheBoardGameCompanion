package com.ghostbusters.companion.domain.model

data class Ghost(
    val id: String,
    val rating: Int, // 1, 2, or 3
    val name: String = "Ghost"
)

data class CharacterStats(
    val characterName: CharacterName,
    val move: Int,
    val drive: Int,
    val lineOfSight: Int
)

enum class AbilityType {
    PASSIVE,           // Always active (e.g., stat bonuses)
    TAPPABLE,          // User can activate manually
    TRIGGERED_BY_DICE, // Will be implemented with dice rolling
    TEAM_PASSIVE       // Affects all Ghostbusters
}

data class CharacterAbility(
    val level: Level,
    val description: String,
    val abilityType: AbilityType = AbilityType.PASSIVE,
    val requiresAction: Boolean = false,  // Does this ability consume an action?
    val xpGain: Int = 0                   // XP gained when ability is used
) {
    companion object {
        fun getAbilitiesForCharacter(
            characterName: CharacterName,
            gameType: GameType
        ): List<CharacterAbility> {
            // For now, only Ghostbusters (base game) is implemented
            return when (gameType) {
                GameType.GHOSTBUSTERS -> getGhostbustersAbilities(characterName)
                GameType.GHOSTBUSTERS_II -> getGhostbusters2Abilities(characterName)
            }
        }

        private fun getGhostbustersAbilities(characterName: CharacterName): List<CharacterAbility> {
            return when (characterName) {
                CharacterName.PETER_VENKMAN -> listOf(
                    CharacterAbility(
                        Level.LEVEL_1,
                        "When a Ghost Slimes you on your turn, gain 1 XP.",
                        AbilityType.TAPPABLE,
                        requiresAction = true,  // Requires action and will switch it to slime
                        xpGain = 1
                    ),
                    CharacterAbility(
                        Level.LEVEL_2,
                        "Once during your turn, if an adjacent Ghostbuster would get Slimed, swap spaces with that Ghostbuster and you get Slimed instead.",
                        AbilityType.TAPPABLE,
                        requiresAction = true
                    ),
                    CharacterAbility(
                        Level.LEVEL_3,
                        "Gain 1 Extra Action",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_4,
                        "When you hit a Ghost with a 6: You may take a free Combat Action against it.",
                        AbilityType.TRIGGERED_BY_DICE
                    ),
                    CharacterAbility(
                        Level.LEVEL_5,
                        "All Ghostbuster's Line of Sight is increased by 1 space.",
                        AbilityType.TEAM_PASSIVE
                    )
                )

                CharacterName.EGON_SPENGLER -> listOf(
                    CharacterAbility(
                        Level.LEVEL_1,
                        "When you roll a 1 on a Combat Roll, gain 1 XP (count only final results).",
                        AbilityType.TRIGGERED_BY_DICE,
                        xpGain = 1
                    ),
                    CharacterAbility(
                        Level.LEVEL_2,
                        "Once during your turn, you may re-roll a failed Proton Roll.",
                        AbilityType.TRIGGERED_BY_DICE
                    ),
                    CharacterAbility(
                        Level.LEVEL_3,
                        "Gain 1 Extra Action",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_4,
                        "When you hit a Ghost: Roll two Proton Dice during your next Combat Action this turn and choose one.",
                        AbilityType.TRIGGERED_BY_DICE
                    ),
                    CharacterAbility(
                        Level.LEVEL_5,
                        "All Ghostbusters may reroll 1's on the Proton Die once per Combat Action.",
                        AbilityType.TEAM_PASSIVE
                    )
                )

                CharacterName.RAY_STANTZ -> listOf(
                    CharacterAbility(
                        Level.LEVEL_1,
                        "When you spend an action to remove Slime from another Ghostbuster, gain 1 XP.",
                        AbilityType.TAPPABLE,
                        xpGain = 1
                    ),
                    CharacterAbility(
                        Level.LEVEL_2,
                        "Once during your turn, you may remove a Slime from yourself for free.",
                        AbilityType.TAPPABLE
                    ),
                    CharacterAbility(
                        Level.LEVEL_3,
                        "Gain 1 Extra Action",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_4,
                        "When you hit a Ghost: You may remove a Slime from yourself or a Ghostbuster within Line of Sight.",
                        AbilityType.TRIGGERED_BY_DICE
                    ),
                    CharacterAbility(
                        Level.LEVEL_5,
                        "All Ghostbusters may reroll the Movement Die once per failed Proton Roll.",
                        AbilityType.TEAM_PASSIVE
                    )
                )

                CharacterName.WINSTON_ZEDDEMORE -> listOf(
                    CharacterAbility(
                        Level.LEVEL_1,
                        "For every 3 combined Ghost classes you deposit at a time, gain 1 XP.",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_2,
                        "Once during your turn, you make take a free Deposit Trapped Ghosts Action, and into a deposit space within Line of Sight.",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_3,
                        "Gain 1 Extra Action",
                        AbilityType.PASSIVE
                    ),
                    CharacterAbility(
                        Level.LEVEL_4,
                        "When you hit a Ghost: You make take a free Move Action.",
                        AbilityType.TRIGGERED_BY_DICE
                    ),
                    CharacterAbility(
                        Level.LEVEL_5,
                        "All Ghostbusters' Move Actions are increased by 1 space.",
                        AbilityType.TEAM_PASSIVE
                    )
                )

                // For GB2 expansion characters, return placeholder abilities for now
                CharacterName.LOUIS_TULLY, CharacterName.SLIMER -> listOf(
                    CharacterAbility(Level.LEVEL_1, "Ability to be defined", AbilityType.PASSIVE),
                    CharacterAbility(Level.LEVEL_2, "Ability to be defined", AbilityType.PASSIVE),
                    CharacterAbility(Level.LEVEL_3, "Gain 1 Extra Action", AbilityType.PASSIVE),
                    CharacterAbility(Level.LEVEL_4, "Ability to be defined", AbilityType.PASSIVE),
                    CharacterAbility(Level.LEVEL_5, "Ability to be defined", AbilityType.PASSIVE)
                )
            }
        }

        private fun getGhostbusters2Abilities(characterName: CharacterName): List<CharacterAbility> {
            // GB2 abilities will be implemented later
            return listOf(
                CharacterAbility(Level.LEVEL_1, "GB2 Ability - To be defined", AbilityType.PASSIVE),
                CharacterAbility(Level.LEVEL_2, "GB2 Ability - To be defined", AbilityType.PASSIVE),
                CharacterAbility(Level.LEVEL_3, "Gain 1 Extra Action", AbilityType.PASSIVE),
                CharacterAbility(Level.LEVEL_4, "GB2 Ability - To be defined", AbilityType.PASSIVE),
                CharacterAbility(Level.LEVEL_5, "GB2 Ability - To be defined", AbilityType.PASSIVE)
            )
        }

        fun getBaseStats(characterName: CharacterName): CharacterStats {
            // All characters start with the same base stats
            return CharacterStats(
                characterName = characterName,
                move = 2,
                drive = 6,
                lineOfSight = 3
            )
        }
    }
}

