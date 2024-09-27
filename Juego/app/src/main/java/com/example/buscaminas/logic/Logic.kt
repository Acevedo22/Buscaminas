package com.example.buscaminas.logic

class Logic (var size: Int, var mines: Int) {

    class Cell {
        var isRevealed: Boolean = false
        var hasMine: Boolean = false
        var adjacentMines: Int = 0
    }

    private val BOARD_SIZE = size
    private  val MINE_COUNT = mines

        val board: Array<Array<Cell>> = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Cell() } }
        var isGameOver = false

        init {
            placeMines()
            calculateAdjacentMines()
        }

        // Colocar minas aleatoriamente en el tablero
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

        // Calcular el número de minas adyacentes para cada celda
        private fun calculateAdjacentMines() {
            for (row in 0 until BOARD_SIZE) {
                for (col in 0 until BOARD_SIZE) {
                    if (!board[row][col].hasMine) {
                        board[row][col].adjacentMines = countAdjacentMines(row, col)
                    }
                }
            }
        }

        // Contar las minas alrededor de una celda
        private fun countAdjacentMines(row: Int, col: Int): Int {
            var count = 0
            for (i in -1..1) {
                for (j in -1..1) {
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

        // Revelar una celda y comprobar si tiene una mina
        fun revealCell(row: Int, col: Int): Boolean {
            val cell = board[row][col]
            if (cell.isRevealed || isGameOver) {
                return false
            }

            cell.isRevealed = true

            // Si la celda tiene una mina, termina el juego
            if (cell.hasMine) {
                isGameOver = true
                return true
            }

            // Si no tiene minas adyacentes, revela automáticamente las celdas vecinas
            if (cell.adjacentMines == 0) {
                revealAdjacentCells(row, col)
            }

            return false
        }

        // Revelar las celdas adyacentes si no hay minas alrededor
        private fun revealAdjacentCells(row: Int, col: Int) {
            for (i in -1..1) {
                for (j in -1..1) {
                    val newRow = row + i
                    val newCol = col + j
                    if (newRow in 0 until BOARD_SIZE && newCol in 0 until BOARD_SIZE && !board[newRow][newCol].isRevealed) {
                        revealCell(newRow, newCol)
                    }
                }
            }
        }

        // Comprobar si el jugador ha ganado (si todas las celdas sin minas están reveladas)
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