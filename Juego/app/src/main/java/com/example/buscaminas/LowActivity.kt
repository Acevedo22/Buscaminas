package com.example.buscaminas

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buscaminas.logic.Logic

class LowActivity : AppCompatActivity() {

    private val BOARD_SIZE = 8  // Definir el tamaño del tablero
    private lateinit var game: Logic
    private lateinit var buttons: Array<Array<Button>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low)

        game = Logic()
        buttons = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Button(this) } }
        val gridLayout: GridLayout = findViewById(R.id.gridLayoutLow)

        gridLayout.rowCount = BOARD_SIZE
        gridLayout.columnCount = BOARD_SIZE

        // Crear los botones y agregarlos al GridLayout
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val button = Button(this)
                button.setOnClickListener {
                    onCellClicked(row, col)
                }
                buttons[row][col] = button
                gridLayout.addView(button)
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (game.isGameOver) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
            return
        }

        val hitMine = game.revealCell(row, col)

        if (hitMine) {
            buttons[row][col].text = "M"
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
        } else {
            updateBoard()
            if (game.isWin()) {
                Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBoard() {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val cell = game.board[row][col]
                val button = buttons[row][col]

                if (cell.isRevealed) {
                    if (cell.hasMine) {
                        button.text = "M"
                    } else {
                        button.text = if (cell.adjacentMines > 0) cell.adjacentMines.toString() else ""
                    }
                    button.isEnabled = false
                }
            }
        }
    }
}



