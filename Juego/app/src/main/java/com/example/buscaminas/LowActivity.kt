package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.buscaminas.logic.Logic
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class LowActivity : AppCompatActivity() {

    private val BOARD_SIZE = 8  // Definir el tamaño del tablero
    private val MINES_COUNT = 10 //Definir la cantidad de minas del tablero
    private lateinit var game: Logic
    private lateinit var btnReset: Button
    private lateinit var buttons: Array<Array<Button>>
    private var timeInSeconds = 0 // Tiempo transcurrido en segundos
    private lateinit var cronometroTextView: TextView
    private lateinit var handler: Handler
    private var runnable: Runnable? = null
    private var isChronometerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low)
        btnReset = findViewById(R.id.btnReset)
        cronometroTextView = findViewById(R.id.cronometro)
        handler = Handler(Looper.getMainLooper())

        game = Logic(BOARD_SIZE, MINES_COUNT)
        buttons = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Button(this) } }
        val gridLayout: GridLayout = findViewById(R.id.gridLayoutLow)

            // Crear los botones y agregarlos al GridLayout
            for (row in 0 until 8) {
                for (col in 0 until 8) {
                    val button = Button(this)
                    button.setOnClickListener {
                        onCellClicked(row, col)
                    }
                    buttons[row][col] = button

                    // Configuración de LayoutParams para cada botón
                    val params = GridLayout.LayoutParams().apply {
                        width = 100 // Usar ancho flexible
                        height = 100// Usar alto flexible
                        setMargins(5, 5, 5, 5)
                        rowSpec = GridLayout.spec(row, 1f) // Espacio en la fila
                        columnSpec = GridLayout.spec(col, 1f) // Espacio en la columna
                    }
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    button.layoutParams = params
                    gridLayout.addView(button)
                }
            }
            btnReset.setOnClickListener {
                restartGame() // Acción que reinicia el juego
            }
        // Iniciar el cronómetro
        startChronometer()
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (game.isGameOver) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
            //Parar cronometro cuando pierda
            stopChronometer()
            return
        }

        val hitMine = game.revealCell(row, col)

        if (hitMine) {
            buttons[row][col].text = "M"
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            //Parar cronometro cuando pierda
            stopChronometer()
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
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.greenclear))
                    }
                    button.isEnabled = false
                }
            }
        }
    }

    // Método para reiniciar el juego
    private fun restartGame() {
        // Limpiar el tablero de botones
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                buttons[row][col].text = "" // Limpiar el texto de los botones
            }
        }
        // Otras variables del juego también pueden restablecerse aquí, como un marcador o turno
    }

    // Función para iniciar el cronómetro
    private fun startChronometer() {
        runnable = object : Runnable {
            override fun run() {
                timeInSeconds++
                val minutes = timeInSeconds / 60
                val seconds = timeInSeconds % 60

                // Actualizar el TextView con el tiempo formateado
                cronometroTextView.text = String.format("%02d:%02d", minutes, seconds)

                // Repetir la acción cada segundo
                handler.postDelayed(this, 1000)
            }
        }
        runnable?.let { handler.post(it) }
        isChronometerRunning = true
    }

    // Función para detener el cronómetro
    private fun stopChronometer() {
        runnable?.let { handler.removeCallbacks(it) }
        isChronometerRunning = false
    }

    // Puedes agregar esta función para reiniciar el cronómetro junto con el reinicio del juego
    private fun resetChronometer() {
        stopChronometer()
        timeInSeconds = 0
        cronometroTextView.text = "00:00"
        startChronometer() // Iniciar de nuevo si es necesario
    }

}



