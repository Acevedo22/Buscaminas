package com.example.buscaminas.logic

class Logic(var size: Int, var mines: Int) {

        class Cell {
        var isRevealed: Boolean = false
        var hasMine: Boolean = false
        var isFlagged: Boolean = false  // Añadir esta propiedad
        var adjacentMines: Int = 0

        // Otros métodos y propiedades que puedas necesitar
    }
    private val BOARD_SIZE = size
    private val MINE_COUNT = mines
    val board: Array<Array<Cell>> = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Cell() } }
    var isGameOver = false

    init {
        require(MINE_COUNT <= BOARD_SIZE * BOARD_SIZE) { "Number of mines exceeds board size." }
        placeMines()
        calculateAdjacentMines()
    }

    private fun placeMines() {
        var placedMines = 0
        while (placedMines < MINE_COUNT) {
            val row = (0 until BOARD_SIZE).random()
            val col = (0 until BOARD_SIZE).random()

            if (!board[row][col].hasMine) {
                board[row][col].hasMine = true
                placedMines++
            }
        }
    }

    private fun calculateAdjacentMines() {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                if (!board[row][col].hasMine) {
                    board[row][col].adjacentMines = countAdjacentMines(row, col)
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue // Skip the current cell
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until BOARD_SIZE && newCol in 0 until BOARD_SIZE) {
                    if (board[newRow][newCol].hasMine) {
                        count++
                    }
                }
            }
        }
        return count
    }

    fun revealCell(row: Int, col: Int): Boolean {
        if (row !in 0 until BOARD_SIZE || col !in 0 until BOARD_SIZE || board[row][col].isRevealed || isGameOver) {
            return false
        }

        val cell = board[row][col]
        cell.isRevealed = true

        if (cell.hasMine) {
            isGameOver = true
            return true
        }

        if (cell.adjacentMines == 0) {
            revealAdjacentCells(row, col)
        }

        return false
    }

    private fun revealAdjacentCells(row: Int, col: Int) {
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue // Skip the current cell
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until BOARD_SIZE && newCol in 0 until BOARD_SIZE && !board[newRow][newCol].isRevealed) {
                    revealCell(newRow, newCol)
                }
            }
        }
    }

    fun isWin(): Boolean {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val cell = board[row][col]
                if (!cell.isRevealed && !cell.hasMine) {
                    return false
                }
            }
        }
        return true
    }
}
