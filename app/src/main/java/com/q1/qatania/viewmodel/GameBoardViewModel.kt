package com.q1.qatania.viewmodel

import androidx.lifecycle.ViewModel
import com.q1.qatania.dataRepository.GameBoardRepository
import kotlinx.coroutines.flow.map

class GameBoardViewModel(
    private val repository: GameBoardRepository
) : ViewModel() {
    private val _scalingFactor = 20.0 // trial and error xd
    val boardFlow = repository.gameboardFlow().map { receivedBoard ->
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