package com.example.buscaminas

import android.graphics.Typeface
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
import com.example.buscaminas.R.id.gridLayoutLow

class LowActivity : AppCompatActivity() {

    private val BOARD_SIZE_COLUMNS = 8  // Definir el tamaño del tablero columnas
    private val BOARD_SIZE_ROWS = 8  // Definir el tamaño del tablero filas
    private val MINES_COUNT = 10 // Definir la cantidad de minas del tablero

    // Variables para el juego y la UI
    private lateinit var game: Logic
    private lateinit var btnReset: Button
    private lateinit var btnCambio: Button // Botón para cambiar entre modo
    private lateinit var buttons: Array<Array<Button>> // Array de botones para el tablero
    private var timeInSeconds = 0 // Tiempo transcurrido en segundos
    private lateinit var cronometroTextView: TextView
    private lateinit var handler: Handler
    private var runnable: Runnable? = null // Runnable para el cronómetro
    private var isChronometerRunning = false // Variable para controlar el estado del cronómetro
    private var isFlagMode = false // Estado del modo de bandera


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low)

        // Inicializar las vistas
        btnReset = findViewById(R.id.btnReset)
        btnCambio = findViewById(R.id.btnCambio)
        cronometroTextView = findViewById(R.id.cronometro)
        handler = Handler(Looper.getMainLooper()) // Inicializar el Handler para el cronómetro

        // Inicializar el juego y el tablero
        game = Logic(BOARD_SIZE_COLUMNS, BOARD_SIZE_ROWS, MINES_COUNT)
        buttons = Array(BOARD_SIZE_COLUMNS) { Array(BOARD_SIZE_ROWS) { Button(this) } }
        val gridLayout: GridLayout = findViewById(gridLayoutLow)

        // Crear los botones y agregarlos al GridLayout
        for (row in 0 until BOARD_SIZE_COLUMNS) {
            for (col in 0 until BOARD_SIZE_ROWS) {
                createCellButton(row, col, gridLayout) // Crear botón para cada celda
            }
        }

        // Configurar botón de reinicio
        btnReset.setOnClickListener { restartGame() }

        // Establecer texto inicial y color del botón de cambio
        isFlagMode = false // Iniciar en modo revelar
        btnCambio.text = "👁️ Modo Revelar" // Texto inicial
        btnCambio.setBackgroundColor(ContextCompat.getColor(this, R.color.green)) // Color inicial

        // Configurar el listener para el botón de cambio de modo
        btnCambio.setOnClickListener {
            isFlagMode = !isFlagMode // Alternar el estado del modo de bandera
            if (isFlagMode) {
                btnCambio.text = "🚩 Modo Bandera" // Cambiar texto cuando se activa el modo bandera
                btnCambio.setBackgroundColor(ContextCompat.getColor(this, R.color.color_2)) // Cambiar color a amarillo
            } else {
                btnCambio.text = "👁️ Modo Revelar" // Cambiar texto cuando se vuelve al modo revelar
                btnCambio.setBackgroundColor(ContextCompat.getColor(this, R.color.green)) // Cambiar color a verde
            }
         //   Toast.makeText(this, if (isFlagMode) "Modo Bandera Activado" else "Modo Revelar Activado", Toast.LENGTH_SHORT).show()
        }

        // Iniciar el cronómetro
        startChronometer()
    }

    // Método para crear un botón para cada celda
    private fun createCellButton(row: Int, col: Int, gridLayout: GridLayout) {
        val button = Button(this).apply {
            setOnClickListener {
                onCellClicked(row, col) // Manejar clic en la celda
            }
            buttons[row][col] = this // Almacenar el botón en el array
            setBackgroundColor(ContextCompat.getColor(this@LowActivity, R.color.green)) // Color inicial
        }

        // Configuración de LayoutParams para cada botón
        val params = GridLayout.LayoutParams().apply {
            width = 100 // Usar ancho fijo
            height = 100 // Usar alto fijo
            setMargins(5, 5, 5, 5) // Márgenes entre botones
            rowSpec = GridLayout.spec(row, 1f) // Especificar la fila
            columnSpec = GridLayout.spec(col, 1f) // Especificar la columna
        }
        button.layoutParams = params // Asignar los parámetros de layout al botón
        gridLayout.addView(button) // Agregar el botón al GridLayout
    }

    // Método para manejar el clic en una celda
    private fun onCellClicked(row: Int, col: Int) {
        if (game.isGameOver) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show() // Mensaje de Game Over
            stopChronometer() // Detener el cronómetro
            return
        }

        // Comprobar si el modo de bandera está activo
        if (isFlagMode) {
            val cell = game.board[row][col] // Obtener la celda correspondiente
            if (!cell.isRevealed) { // Solo marcar si la celda no ha sido revelada
                cell.isFlagged = !cell.isFlagged // Alternar el estado de la bandera
                buttons[row][col].text = if (cell.isFlagged) "🚩" else "" // Muestra la bandera o quita el texto

            }
            return // Salir del método
        }

        val cell = game.board[row][col] // Obtener la celda correspondiente
        if (cell.isFlagged) {
            Toast.makeText(this, "Esta celda está marcada con una bandera", Toast.LENGTH_SHORT).show() // Mensaje si la celda tiene bandera
            return // Salir del método
        }

        // Revelar la celda
        val hitMine = game.revealCell(row, col)
        if (hitMine) {
            buttons[row][col].text = "💣"
            revealAllMines() // Revelar todas las minas
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            stopChronometer() // Detener el cronómetro
        } else {
            updateBoard() // Actualizar el estado del tablero
            if (game.isWin()) {
                Toast.makeText(this, "¡Ganaste!", Toast.LENGTH_SHORT).show()
                stopChronometer()
            }
        }
    }

    // Método para revelar todas las minas en el tablero
    private fun revealAllMines() {
        for (row in 0 until BOARD_SIZE_COLUMNS) {
            for (col in 0 until BOARD_SIZE_ROWS) {
                val cell = game.board[row][col]
                if (cell.hasMine) {
                    buttons[row][col].text = "💣" // Mostrar todas las minas
                    buttons[row][col].setBackgroundColor(ContextCompat.getColor(this, R.color.color_3)) // Cambiar color a rojo para minas
                }
            }
        }
    }


    // Método para verificar si el jugador ha ganado
    private fun checkWinCondition() {
        var allCellsRevealed = true // Variable para verificar si todas las celdas sin mina han sido reveladas
        var allFlagsCorrect = true // Variable para verificar si todas las banderas están en las posiciones correctas

        for (row in 0 until BOARD_SIZE_COLUMNS) {
            for (col in 0 until BOARD_SIZE_ROWS) {
                val cell = game.board[row][col]

                // Verificar si hay celdas que deberían estar reveladas pero no lo están
                if (!cell.hasMine && !cell.isRevealed) {
                    allCellsRevealed = false
                }

                // Verificar si las banderas están en las posiciones correctas
                if (cell.isFlagged != cell.hasMine) {
                    allFlagsCorrect = false
                }
            }
        }

        // Comprobar si se cumplen ambas condiciones para ganar
        if (allCellsRevealed && allFlagsCorrect) {
            Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show() // Mensaje de victoria
            stopChronometer() // Detener el cronómetro
        }
    }



    // Método para actualizar el tablero
    private fun updateBoard() {
        for (row in 0 until BOARD_SIZE_COLUMNS) {
            for (col in 0 until BOARD_SIZE_ROWS) {
                val cell = game.board[row][col]
                val button = buttons[row][col]

                if (cell.isRevealed) {
                    if (cell.hasMine) {
                        button.text = "💣"
                    } else {
                        button.text = if (cell.adjacentMines > 0) cell.adjacentMines.toString() else ""
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.Background_Tabla_Game))

                        // Cambiar el color del texto basado en el número de minas adyacentes
                        when (cell.adjacentMines) {
                            1 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_3))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            2 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_4))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            3 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_5))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            4 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_8))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            5 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_7))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            6 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.Background_Interfaze))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            7 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_0))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                            8 -> {
                                button.setTextColor(ContextCompat.getColor(this, R.color.color_2))
                                button.setTypeface(null, Typeface.BOLD) // Texto en negrita
                            }
                        }
                    }
                    button.isEnabled = false
                }
            }
        }
    }



    // Método para reiniciar el juego
    private fun restartGame() {
        // Crear una nueva instancia de Logic para reiniciar el juego
        game = Logic(BOARD_SIZE_COLUMNS, BOARD_SIZE_ROWS, MINES_COUNT)

        // Limpiar y reiniciar el tablero
        for (row in 0 until BOARD_SIZE_COLUMNS) {
            for (col in 0 until BOARD_SIZE_ROWS) {
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
                    timeInSeconds++ // Incrementar el tiempo
                    val minutes = timeInSeconds / 60 // Calcular minutos
                    val seconds = timeInSeconds % 60 // Calcular segundos

                    // Actualizar el TextView con el tiempo formateado
                    cronometroTextView.text = String.format("%02d:%02d", minutes, seconds)

                    // Repetir la acción cada segundo
                    handler.postDelayed(this, 1000) // Programar el siguiente incremento
                }
            }
            handler.post(runnable!!) // Iniciar el runnable
            isChronometerRunning = true // Marcar cronómetro como en funcionamiento
        }
    }

    // Función para detener el cronómetro
    private fun stopChronometer() {
        // Detener el cronómetro solo si está en funcionamiento
        if (isChronometerRunning) {
            runnable?.let { handler.removeCallbacks(it) } // Eliminar callbacks del runnable
            isChronometerRunning = false // Marcar cronómetro como detenido
        }
    }
}
