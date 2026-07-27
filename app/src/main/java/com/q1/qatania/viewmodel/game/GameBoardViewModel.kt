package com.q1.qatania.viewmodel.game

import android.util.Log
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.q1.qatania.model.gameboard.GameBoardModel
import com.q1.qatania.repository.GameBoardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.q1.qatania.model.gameboard.Robber

class GameBoardViewModel() : ViewModel() {
    private val _scalingFactor = 20.0 // trial and error xd

    private val gameBoardRepository = GameBoardRepository.getInstance()


    val gameboardState: StateFlow<GameBoardModel?> =
        gameBoardRepository.gameboardState.map { receivedBoard ->

            if (receivedBoard == null) {
                Log.d("GameBoardViewModel", "Received gameboard was null")
                return@map null
            }

            if (receivedBoard.sizeOfHex != 6)
                Log.w(
                    "GameBoardViewModel",
                    "Received Board has a size of Hex of ${receivedBoard.sizeOfHex} and not 6, which means the Positioning of the tiles might be wrong."
                )

            receivedBoard.copy(
                tiles = receivedBoard.tiles.map { receivedTile ->
                    receivedTile.copy(
                        coordinates = receivedTile.coordinates.map { value ->
                            value / _scalingFactor
                        }
                    )
                },
                settlementPositions = receivedBoard.settlementPositions.map { receivedElement ->
                    receivedElement.copy(
                        coordinates = receivedElement.coordinates.map { value ->
                            value / _scalingFactor
                        }
                    )
                },
                roads = receivedBoard.roads.map { receivedElement ->
                    receivedElement.copy(
                        coordinates = receivedElement.coordinates.map { value ->
                            value / _scalingFactor
                        }
                    )
                },
                ports = receivedBoard.ports.map { receivedElement ->
                    receivedElement.copy(
                        portVisuals = receivedElement.portVisuals.copy(
                            buildingSite1Position = receivedElement.portVisuals.buildingSite1Position.map { value ->
                                value / _scalingFactor
                            },
                            buildingSite2Position = receivedElement.portVisuals.buildingSite2Position.map { value ->
                                value / _scalingFactor
                            }
                        )
                    )

                },
                robber = receivedBoard.robber.copy(coordinates = receivedBoard.robber.coordinates.map { value ->
                    value / _scalingFactor})
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


}