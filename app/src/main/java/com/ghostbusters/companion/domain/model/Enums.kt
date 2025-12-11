package com.ghostbusters.companion.domain.model

enum class GameType {
    GHOSTBUSTERS,
    GHOSTBUSTERS_II
}

enum class ExpansionType {
    PLAZM_PHENOMENON,
    SEA_FRIGHT
}

enum class CharacterName {
    PETER_VENKMAN,
    EGON_SPENGLER,
    WINSTON_ZEDDEMORE,
    RAY_STANTZ,
    LOUIS_TULLY,
    SLIMER;

    fun getDisplayName(): String = when (this) {
        PETER_VENKMAN -> "Peter Venkman"
        EGON_SPENGLER -> "Egon Spengler"
        WINSTON_ZEDDEMORE -> "Winston Zeddemore"
        RAY_STANTZ -> "Ray Stantz"
        LOUIS_TULLY -> "Louis Tully"
        SLIMER -> "Slimer"
    }

    fun getProtonStreamColor(): CharacterColor = when (this) {
        EGON_SPENGLER -> CharacterColor.ORANGE
        PETER_VENKMAN -> CharacterColor.RED
        RAY_STANTZ -> CharacterColor.YELLOW
        WINSTON_ZEDDEMORE -> CharacterColor.PURPLE
        LOUIS_TULLY -> CharacterColor.KHAKI
        SLIMER -> CharacterColor.GREEN
    }

    fun requiresExpansion(): ExpansionType? = when (this) {
        LOUIS_TULLY -> ExpansionType.PLAZM_PHENOMENON
        SLIMER -> ExpansionType.SEA_FRIGHT
        else -> null
    }
}

enum class CharacterColor(val hex: Long) {
    ORANGE(0xFFFF8C00),
    RED(0xFFDC143C),
    YELLOW(0xFFFFD700),
    PURPLE(0xFF9370DB),
    KHAKI(0xFFF0E68C),
    GREEN(0xFF32CD32)
}

enum class Level(val minXp: Int, val maxXp: Int) {
    LEVEL_1(1, 4),
    LEVEL_2(5, 10),
    LEVEL_3(11, 18),
    LEVEL_4(19, 29),
    LEVEL_5(30, 30);

    companion object {
        fun fromXp(xp: Int): Level = when {
            xp <= 4 -> LEVEL_1
            xp <= 10 -> LEVEL_2
            xp <= 18 -> LEVEL_3
            xp <= 29 -> LEVEL_4
            else -> LEVEL_5
        }
    }
}

