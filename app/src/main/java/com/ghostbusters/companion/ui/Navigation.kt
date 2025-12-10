package com.ghostbusters.companion.ui

sealed class Screen(val route: String) {
    object GameList : Screen("game_list")
    object GameSetup : Screen("game_setup")
    object GameDetail : Screen("game_detail/{gameId}") {
        fun createRoute(gameId: Long) = "game_detail/$gameId"
    }
    object CharacterSheet : Screen("character_sheet/{characterId}") {
        fun createRoute(characterId: Long) = "character_sheet/$characterId"
    }
}

