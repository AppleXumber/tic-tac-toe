package org.example

import java.lang.Thread.sleep
import java.util.*
import kotlin.random.Random

fun main() {
  val board = Board()
  val game = Game(board)
  val player1 = Player(State.X, game)
  val player2 = Player(State.O, game, true)

  game.startGame(player1, player2)
}

class Board {
  val squaresBoard = listOf(
    Square(1),
    Square(2),
    Square(3),
    Square(4),
    Square(5),
    Square(6),
    Square(7),
    Square(8),
    Square(9),
  )

  fun checkWins(): State {
    val winningCombinations = listOf(
      listOf(0, 1, 2), // Top line
      listOf(3, 4, 5), // Middle line
      listOf(6, 7, 8), // Bottom line
      listOf(0, 3, 6), // Left column
      listOf(1, 4, 7), // Middle column
      listOf(2, 5, 8), // Right column
      listOf(0, 4, 8), // Main diagonal
      listOf(2, 4, 6)  // Secondary diagonal
    )

    for (combinations in winningCombinations) {
      val states = combinations.map { squaresBoard[it].getState() }
      if (states.all { it == "X" }) return State.X
      if (states.all { it == "O" }) return State.O
    }
    return State.EMPTY
  }

  fun printBoard() {
    val size = 3
    for (i in 0 until size) {
      for (j in 0 until size) {
        val square = squaresBoard[i * size + j]
        val color = when (square.getState()) {
          "X" -> "\u001B[31m" // Red for X
          "O" -> "\u001B[34m" // Blue for O
          else -> "\u001B[37m" // Branco for empty
        }
        print("$color\u001B[40m[ ${square.getState()} ]\u001B[0m")
        if (j < size - 1) print(" ")
      }
      println()
    }
  }
}

class Game(val board: Board) {
  private var winner = State.EMPTY
  fun startGame(player1: Player, player2: Player) {
    val players = listOf(player1, player2)
    var currentPlayerIndex = 0
    var freeSquares: List<Int>

    do {
      val currentPlayer = players[currentPlayerIndex]
      currentPlayer.play()
      checkWin()

      freeSquares = board.squaresBoard
        .mapIndexedNotNull { index, square -> if (!square.getChanged()) index else null }
      if (freeSquares.isEmpty()) {
        println("The game has tied!")
        break
      }

      currentPlayerIndex = 1 - currentPlayerIndex
    } while (winner == State.EMPTY)

    if (winner != State.EMPTY) {
      println("The winner is the player $winner!")
    }

    board.printBoard()
  }

  private fun checkWin() {
    setWinner(board.checkWins())
  }

  private fun setWinner(state: State) {
    winner = state
  }
}

class Player(private var state: State, private var game: Game, private var isBot: Boolean = false) {
  fun play() {
    var validPlay = false
    val sc = Scanner(System.`in`)
    do {
      val coordSquare: Int
      game.board.printBoard()
      sleep(250)
      println("\nPlayer $state, which square do you want to play? (1~9)")

      if (isBot) {
        sleep(1250)
        coordSquare = getBotMove()
        println("\u001B[32m\u001B[40m${coordSquare + 1}\u001B[0m")
      } else {
        coordSquare = sc.nextInt() - 1
      }

      if (coordSquare in 0..8 && !game.board.squaresBoard[coordSquare].getChanged()) {
        game.board.squaresBoard[coordSquare].setState(state)
        validPlay = true
      } else if (coordSquare !in 0..8) {
        println("Invalid move. Please choose a number between 1 and 9.")
      } else {
        println("Square already taken. Try again.")
      }
    } while (!validPlay)
  }

  private fun getBotMove(): Int {

    val availableMoves = game.board.squaresBoard
      .mapIndexedNotNull { index, square -> if (!square.getChanged()) index else null }

    val thinkingMessages = listOf(
      "Hmm, let me think...",
      "Deciding on my next move...",
      "Analyzing the board...",
      "Which square should I choose?",
      "Considering my options...",
      "Let me evaluate my strategy...",
      "Thinking about the best move...",
      "This looks like a good spot...",
      "I need to find the best position...",
      "Strategizing my next play..."
    )

    val randomThinkingMessage = thinkingMessages[Random.nextInt(thinkingMessages.size)]
    println("\u001B[32m\u001B[40m$randomThinkingMessage\u001B[0m")
    sleep(1250)
    return availableMoves[Random.nextInt(availableMoves.size)]
  }
}

data class Square(val coord: Int) {
  private var state: State = State.EMPTY
  private var changed = false

  fun setState(state: State) {
    if (changed) return println("Square already taken")
    this.state = state
    changed = true
  }

  fun getState(): String {
    return when (state) {
      State.O -> "${State.O}"
      State.X -> "${State.X}"
      else -> "$coord"
    }
  }

  fun getChanged(): Boolean {
    return changed
  }
}

enum class State {
  X, O, EMPTY
}
