package com.example.buscaminas.logic

/**
 * Clase principal que maneja la lógica del juego Buscaminas.
 *
 * @param size Tamaño del tablero (tamaño x tamaño).
 * @param mines Número de minas en el tablero.
 */
class Logic(var size_columns: Int, var size_rows: Int, var mines: Int) {

    /**
     * Clase interna que representa una celda del tablero.
     *
     * @property isRevealed Indica si la celda ha sido revelada.
     * @property hasMine Indica si la celda contiene una mina.
     * @property isFlagged Indica si la celda está marcada con una bandera.
     * @property adjacentMines Número de minas adyacentes a la celda.
     */
    class Cell {
        var isRevealed: Boolean = false
        var hasMine: Boolean = false
        var isFlagged: Boolean = false
        var adjacentMines: Int = 0
    }

    private val boardSizeColumns = size_columns
    private val boardSizeRows = size_rows
    private val mineCount = mines
    val board: Array<Array<Cell>> = Array(boardSizeColumns) { Array(boardSizeRows) { Cell() } }
    var isGameOver = false

    init {
        require(mineCount <= boardSizeColumns * boardSizeRows) { "El número de minas excede el tamaño del tablero." }
        placeMines()
        calculateAdjacentMines()
    }

    /**
     * Coloca las minas aleatoriamente en el tablero.
     */
    private fun placeMines() {
        var placedMines = 0
        while (placedMines < mineCount) {
            val row = (0 until boardSizeColumns).random()
            val col = (0 until boardSizeRows).random()

            if (!board[row][col].hasMine) {
                board[row][col].hasMine = true
                placedMines++
            }
        }
    }

    /**
     * Calcula el número de minas adyacentes para cada celda del tablero.
     */
    private fun calculateAdjacentMines() {
        for (row in 0 until boardSizeColumns) {
            for (col in 0 until boardSizeRows) {
                if (!board[row][col].hasMine) {
                    board[row][col].adjacentMines = countAdjacentMines(row, col)
                }
            }
        }
    }

    /**
     * Cuenta el número de minas adyacentes a una celda específica.
     *
     * @param row Fila de la celda.
     * @param col Columna de la celda.
     * @return Número de minas adyacentes.
     */
    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue // Saltar la celda actual
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until boardSizeColumns && newCol in 0 until boardSizeRows && board[newRow][newCol].hasMine) {
                    count++
                }
            }
        }
        return count
    }

    /**
     * Revela una celda del tablero. Si la celda contiene una mina, el juego termina.
     * Si no tiene minas adyacentes, se revelan también las celdas adyacentes.
     *
     * @param row Fila de la celda.
     * @param col Columna de la celda.
     * @return Verdadero si la celda contiene una mina, falso en caso contrario.
     */
    fun revealCell(row: Int, col: Int): Boolean {
        if (row !in 0 until boardSizeColumns || col !in 0 until boardSizeRows || board[row][col].isRevealed || isGameOver) {
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

    /**
     * Revela las celdas adyacentes a una celda vacía.
     *
     * @param row Fila de la celda.
     * @param col Columna de la celda.
     */
    private fun revealAdjacentCells(row: Int, col: Int) {
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue // Saltar la celda actual
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until boardSizeColumns && newCol in 0 until boardSizeRows && !board[newRow][newCol].isRevealed) {
                    revealCell(newRow, newCol)
                }
            }
        }
    }

    /**
     * Verifica si el jugador ha ganado la partida.
     * El jugador gana si todas las celdas sin minas están reveladas y todas las celdas con minas están marcadas con bandera.
     *
     * @return Verdadero si el jugador ha ganado, falso en caso contrario.
     */
    fun isWin(): Boolean {
        return board.all { row ->
            row.all { cell ->
                (!cell.hasMine && cell.isRevealed) || (cell.hasMine && cell.isFlagged)
            }
        }
    }
}
