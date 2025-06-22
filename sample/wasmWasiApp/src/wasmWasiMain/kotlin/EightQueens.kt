import kotlin.time.measureTime

/**
 * Created by benny.
 */
const val SIZE = 8

fun main() {
    measureTime {
        val board = IntArray(SIZE) { -1 } // board[row] = column where the queen is placed
        solve(board, 0)
    }.also {
        println(it)
    }
}

fun solve(board: IntArray, row: Int) {
    if (row == SIZE) {
        printBoard(board)
        return
    }

    for (col in 0 until SIZE) {
        if (isSafe(board, row, col)) {
            board[row] = col
            solve(board, row + 1)
            board[row] = -1 // backtrack
        }
    }
}

fun isSafe(board: IntArray, row: Int, col: Int): Boolean {
    for (prevRow in 0 until row) {
        val prevCol = board[prevRow]
        if (prevCol == col ||
            prevCol - prevRow == col - row ||
            prevCol + prevRow == col + row) {
            return false
        }
    }
    return true
}

fun printBoard(board: IntArray) {
//    println("Solution:")
//    for (row in 0 until SIZE) {
//        for (col in 0 until SIZE) {
//            if (board[row] == col) print("Q ") else print(". ")
//        }
//        println()
//    }
//    println()
}