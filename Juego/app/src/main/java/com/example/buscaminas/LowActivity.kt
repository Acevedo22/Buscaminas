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
    private val MINES_COUNT = 10 // Definir la cantidad de minas del tablero
    private lateinit var game: Logic
    private lateinit var btnReset: Button
    private lateinit var btnCambio: Button // Botón para cambiar entre modo
    private lateinit var buttons: Array<Array<Button>>
    private var timeInSeconds = 0 // Tiempo transcurrido en segundos
    private lateinit var cronometroTextView: TextView
    private lateinit var handler: Handler
    private var runnable: Runnable? = null
    private var isChronometerRunning = false // Variable para controlar el estado del cronómetro
    private var isFlagMode = false // Estado del modo de bandera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low)
        btnReset = findViewById(R.id.btnReset)
        btnCambio = findViewById(R.id.btnCambio) // Inicializar el botón de cambio
        cronometroTextView = findViewById(R.id.cronometro)
        handler = Handler(Looper.getMainLooper())

        game = Logic(BOARD_SIZE, MINES_COUNT)
        buttons = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Button(this) } }
        val gridLayout: GridLayout = findViewById(R.id.gridLayoutLow)

        // Crear los botones y agregarlos al GridLayout
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val button = Button(this)
                button.setOnClickListener {
                    onCellClicked(row, col)
                }
                buttons[row][col] = button

                // Configuración de LayoutParams para cada botón
                val params = GridLayout.LayoutParams().apply {
                    width = 100 // Usar ancho flexible
                    height = 100 // Usar alto flexible
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

        btnCambio.setOnClickListener {
            toggleFlagMode() // Cambia entre los modos
        }

        // Iniciar el cronómetro
        startChronometer()
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (game.isGameOver) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
            // Parar cronómetro cuando pierda
            stopChronometer()
            return
        }

        if (isFlagMode) {
            // Colocar o quitar una bandera
            toggleFlag(row, col)
        } else {
            val hitMine = game.revealCell(row, col)

            if (hitMine) {
                buttons[row][col].text = "💣" // Cambiar "M" por el ícono de mina
                Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
                // Parar cronómetro cuando pierda
                stopChronometer()
            } else {
                updateBoard()
                if (game.isWin()) {
                    Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show()
                    // Detener el cronómetro si el jugador gana
                    stopChronometer()
                }
            }
        }
    }


    private fun toggleFlag(row: Int, col: Int) {
        val cell = game.board[row][col]
        if (!cell.isRevealed) {
            cell.hasMine = !cell.hasMine // Alternar el estado de la bandera
            buttons[row][col].text = if (cell.hasMine) "🚩" else "" // Mostrar bandera
            buttons[row][col].setBackgroundColor(
                if (cell.hasMine) ContextCompat.getColor(this, R.color.green)
                else ContextCompat.getColor(this, R.color.green)
            ) // Cambiar color de fondo
        }
    }

    private fun toggleFlagMode() {
        isFlagMode = !isFlagMode
        btnCambio.text = if (isFlagMode) "Modo Juego" else "Modo Bandera" // Cambiar texto del botón
        Toast.makeText(this, if (isFlagMode) "Modo Bandera Activado" else "Modo Juego Activado", Toast.LENGTH_SHORT).show()
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
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.Background_Tabla_Game))
                    }
                    button.isEnabled = false
                }
            }
        }
    }

    // Método para reiniciar el juego
    private fun restartGame() {
        // Crear una nueva instancia de Logic para reiniciar el juego
        game = Logic(BOARD_SIZE, MINES_COUNT)

        // Limpiar y reiniciar el tablero
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                buttons[row][col].text = "" // Limpiar el texto de los botones
                buttons[row][col].isEnabled = true // Habilitar los botones nuevamente
                buttons[row][col].setBackgroundColor(ContextCompat.getColor(this, R.color.green)) // Restaurar color
            }
        }

        // Reiniciar el cronómetro
        stopChronometer() // Detener el cronómetro anterior
        timeInSeconds = 0 // Reiniciar el tiempo
        cronometroTextView.text = "00:00" // Limpiar el cronómetro
        startChronometer() // Iniciar el cronómetro de nuevo
    }

    // Función para iniciar el cronómetro
    private fun startChronometer() {
        // Solo iniciar el cronómetro si no está ya en funcionamiento
        if (!isChronometerRunning) {
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
            handler.post(runnable!!)
            isChronometerRunning = true
        }
    }

    // Función para detener el cronómetro
    private fun stopChronometer() {
        // Detener el cronómetro solo si está en funcionamiento
        if (isChronometerRunning) {
            runnable?.let { handler.removeCallbacks(it) }
            isChronometerRunning = false
        }
    }
}
