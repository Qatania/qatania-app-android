package com.q1.qatania.model.player

import com.q1.qatania.model.gameboard.TileType
import kotlinx.serialization.Serializable


@Serializable
data class PlayerModel(
    val id: String,
    val username: String?,
    val color: String,
    val isHost: Boolean,
    val isReady: Boolean,
    val isActivePlayer: Boolean,
    val canRollDice: Boolean,
    val isSetupRound: Boolean,
    val needsToMoveRobber: Boolean = false,
    val victoryPoints: Int,
    val resources: Map<TileType, Int>
)