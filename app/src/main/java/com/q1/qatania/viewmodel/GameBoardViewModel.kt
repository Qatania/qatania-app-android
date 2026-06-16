package com.q1.qatania.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.q1.qatania.repository.GameBoardRepository
import kotlinx.coroutines.flow.map

class GameBoardViewModel(
    private val repository: GameBoardRepository
) : ViewModel() {
    private val _scalingFactor = 20.0 // trial and error xd

    val boardFlow = repository.gameboardFlow().map { receivedBoard ->
        if (receivedBoard.sizeOfHex != 6)
            Log.w(
                "Gameboard",
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
                        buildingSite1Position = receivedElement.portVisuals.buildingSite1Position.map {value ->
                            value / _scalingFactor
                        },
                        buildingSite2Position = receivedElement.portVisuals.buildingSite2Position.map {value ->
                            value / _scalingFactor
                        }
                    )
                )
            }
        )
    }
}