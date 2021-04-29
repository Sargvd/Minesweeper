package minesweeper

import kotlin.random.Random

object Board {
    private var board = Array<Array<Char>>(9) {Array(9) {'.'} }
    private var mineMap = Array<Array<Char>>(9) {Array(9) {'.'} }

    private fun incCounter(x: Int, y: Int) {
        if (x in 0..8 && y in 0..8) {
            if (mineMap[x][y] != 'X') {
                if (mineMap[x][y] == '.') mineMap[x][y] = '1'
                else mineMap[x][y] = (mineMap[x][y].toInt() + 1).toChar()
            }
        }
    }

  fun addMines(minesRequested: Int) {
      val vector = MutableList(9*9) { 0 }
      for (i in vector.indices) vector[i] = i+1

      for(i in 0 until minesRequested) {
          val coord = Random.nextInt(0, vector.size-1)
          val x = coord / 9
          val y = coord % 9
          if (mineMap[x][y] != 'X') {
              mineMap[x][y] = 'X'
              for (i in x - 1..x + 1)
                  for (j in y - 1..y + 1) incCounter(i, j)
              vector.removeAt(x)
          }
      }
    }

    fun openField(x: Int, y: Int): Boolean {
        var stepOnMine = false

        if (x !in 0..8 || y !in 0..8) return false

        if (mineMap[x][y] == 'X') {
            board[x][y] = 'X'
            stepOnMine = true
        }
        else if (mineMap[x][y] in '0'..'8') board[x][y] = mineMap[x][y]
        else if (board[x][y] != '/'){
            board[x][y] = '/'
            for (i in x-1..x+1)
                for (j in y-1..y+1) {
                    if (i == x && j == y) continue
                    openField(i, j)
                }
        }

        return stepOnMine
    }

    fun checkWin(): Boolean {
        var isWin = true
        for (i in board.indices)
            for (j in board[i].indices) {
                if (board[i][j] == '*' && mineMap[i][j] != 'X') isWin = false
                if (mineMap[i][j] == 'X' && board[i][j] != '*') isWin = false
            }
        return isWin
    }

    fun setMark(x: Int, y: Int): Boolean {
        var reRender = true
        when {
            board[x][y] in '1'..'9' -> {
                println("There is a number here!")
                reRender = false
            }
            board[x][y] == '*' -> board[x][y] = '.'
            else -> board[x][y] = '*'
        }
        return reRender
    }

    fun renderBoard() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        for (i in board.indices) println("${i+1}│${board[i].joinToString("")}│")
        println("—│—————————│")
    }
}

fun main() {
    val board = Board
    print("How many mines do you want on the field?")
    val minesRequested = readLine()!!.toInt()

    board.addMines(minesRequested)
    board.renderBoard()

    while (true) {
        print("Set/delete mine marks (x and y coordinates):")
        val (x, y, action) = readLine()!!.split(" ")
        when (action) {
            "free" -> {
                if (board.openField(y.toInt()-1, x.toInt()-1)) {
                    println("You stepped on a mine and failed!")
                    board.renderBoard()
                    break
                }
                board.renderBoard()
            }
            "mine" -> {
                if (board.setMark(y.toInt()-1, x.toInt()-1)) board.renderBoard()
            }
        }

        if (board.checkWin()) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}
