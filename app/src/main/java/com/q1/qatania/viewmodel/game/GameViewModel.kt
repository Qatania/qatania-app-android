package com.q1.qatania.viewmodel.game

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.MainApplication
import com.q1.qatania.model.dto.MessageDTO
import com.q1.qatania.model.dto.MessageType
import com.q1.qatania.model.gameboard.BuildingType
import com.q1.qatania.model.gameboard.Road
import com.q1.qatania.model.gameboard.SettlementPosition
import com.q1.qatania.model.gameboard.TileType
import com.q1.qatania.model.player.PlayerModel
import com.q1.qatania.repository.GameRepository
import com.q1.qatania.repository.GameRepository.DiceState
import com.q1.qatania.repository.LobbyRepository
import com.q1.qatania.repository.PlayerInfoRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GameViewModel : ViewModel() {

    val playerInfoRepository = PlayerInfoRepository.getInstance()
    val gameRepository = GameRepository.getInstance()

    val self = playerInfoRepository.getPlayerId()

    fun cheat(lobbyId: String, tileType: TileType) {
        Log.d("GameViewModel", "Cheating attempt with tile type $tileType")
        gameRepository.cheat(
            self,
            lobbyId,
            tileType
        )
    }

    fun report(lobbyId: String, reportedPlayerId: String) {
        Log.d("GameViewModel", "$self is reporting $reportedPlayerId")
        gameRepository.reportPlayer(
            self,
            lobbyId,
            reportedPlayerId
        )
    }

    fun rollDice(lobbyId: String) {
        gameRepository.rollDice(self, lobbyId)
    }

    fun handleEndTurnClick(lobbyId: String) {
        gameRepository.endTurn(self, lobbyId)
    }

    fun handleRoadTap(lobbyId: String, road: Road) {
        Log.d("GameViewModel", "Tapped road $road")
        gameRepository.buildRoad(self, lobbyId, road.id)
    }

    fun handleTileTap(lobbyId: String, player: PlayerModel?, tileId: Int) {
        Log.d("GameViewModel", "Tapped tile $tileId")

        if (player?.isActivePlayer == true && player.needsToMoveRobber) {
            gameRepository.placeRobber(self, lobbyId, tileId)
        }
    }

    fun handleSettlementTap(lobbyId: String, settlementPosition: SettlementPosition) {
        Log.d("GameViewModel", "Tapped settlement position $settlementPosition")
        val isUpgrade: Boolean = settlementPosition.building?.type == BuildingType.Settlement
        if(isUpgrade) {
            gameRepository.upgradeSettlement(
                self,
                lobbyId,
                settlementPosition.id
            )
        } else {
            gameRepository.buildSettlement(
                self,
                lobbyId,
                settlementPosition.id
            )
        }
    }


    val diceState: StateFlow<DiceState?> = gameRepository.diceFlow
    fun clearDiceState() {
        gameRepository.clearDiceState()
    }

    val gameEndState: StateFlow<GameRepository.GameEndState?> = gameRepository.gameEndFlow
    fun clearGameEndState() {
        gameRepository.clearGameEndState()
    }

    fun submitBankTrade(
        tradeRequest: Pair<Map<TileType, Int>, Map<TileType, Int>>,
        lobbyId: String
    ) {
        gameRepository.submitBankTrade(
            self,
            lobbyId,
            tradeRequest
        )
    }

}