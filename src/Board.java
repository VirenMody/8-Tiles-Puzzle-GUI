/*
Class: Board
This class will represent the 3X3 grid containing tiles numbered 1-8 and 0 (blank tile)
and its heuristic value (closeness to win/goal).

Heuristic value is calculated by sum all the "city-block" distances of each tile from
its end goal (i.e. if "7" is in the top left corner of the grid, its heuristic value is
2 "city-blocks" from its goal position 2 spots down).
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Board
{
    private int[]   board;
    private String  boardAsString;      // Hash key for hashmap
    private int     heuristicValue;

    // CONSTRUCTOR
    public Board()
    {
        boardAsString = buildRandomBoard();
        heuristicValue = calculateHeuristicValue();
    }

    // CHAINED CONSTRUCTOR Given user chosen board
    public Board(String userString)
    {
        buildUserGivenBoard(userString);
        boardAsString = userString;
        heuristicValue = calculateHeuristicValue();
    }

    public int[] getBoardArr()
    {
        return board;
    }

    public String getBoardAsString()
    {
        return boardAsString;
    }

    public int getHeuristicValue()
    {
        return heuristicValue;
    }

    // Creates a random string to represent the board and converts it to an integer array
    // @return string representation of board
    private String buildRandomBoard()
    {
        board = new int[Constants.BOARDSIZE];

        ArrayList<Integer> boardList = new ArrayList<>(Constants.BOARDSIZE);
        for(int x = 0; x < Constants.BOARDSIZE; x++)
            boardList.add(x);

        // Randomly shuffle array to get random board
        Collections.shuffle(boardList, new Random(System.currentTimeMillis()));

        return convertBoardArrToString(boardList);
    }

    private String convertBoardArrToString(ArrayList<Integer> boardList)
    {
        String boardAsString = "";
        // Copy board ArrayList to array and create string representation of board
        for(int x = 0; x < Constants.BOARDSIZE; x++)
        {
            board[x] = boardList.get(x);
            boardAsString = boardAsString + String.valueOf(board[x]);
        }
        return boardAsString;
    }

    // Converts user entered string representation of the board into an integer array
    // @param userBoard
    private void buildUserGivenBoard(String userBoard)
    {
        board = new int[Constants.BOARDSIZE];
        for(int x = 0; x < Constants.BOARDSIZE; x++)
        {
            board[x] = Character.getNumericValue(userBoard.charAt(x));
        }
    }


    // Calculate the heuristic value of the board by summing each tile's heuristic value
    // @return heuristic value of board
    private int calculateHeuristicValue()
    {
        int hvalue = 0;
        // Iterate through each tile and sum each tile's heuristic value
        for(int pos = 0; pos < Constants.BOARDSIZE; pos++)
        {
            hvalue += tileHeuristicValue(pos);
        }
        return hvalue;
    }

    // Calculate the specific tile's heuristic value - "city-block" distance from goal
    // @param arrPos: tile's current location in board array
    // @return tile's heuristic value
    private int tileHeuristicValue(int arrPos)
    {
        // Determine tile's position (row, col) on the board
        int currRow = Constants.arrToBoardRowMapping[arrPos];
        int currCol = Constants.arrToBoardColMapping[arrPos];

        // Determine tile's end goal (row, col) on the board
        int tile = board[arrPos];
        int goalRow = Constants.tileGoalRow[tile];
        int goalCol = Constants.tileGoalCol[tile];

        // Heuristic Value = sum of city-block distances (differences) between rows and
        // columns
        int rowCityBlockDistance, colCityBlockDistance;
        int tileHeuristicVal;

        rowCityBlockDistance = Math.abs(goalRow - currRow);
        colCityBlockDistance = Math.abs(goalCol - currCol);

        tileHeuristicVal = rowCityBlockDistance + colCityBlockDistance;
        return tileHeuristicVal;
    }

    // TO DO: SIMPLIFY WITH WRAPPER FUNCTION
    // @return ArrayList of string representations of the board of all valid moves
    //         after a move up, down, left and right. If invalid, null is added.
    public ArrayList<String> getPotentialBoardMoves()
    {
        ArrayList<String> validBoardMoves = new ArrayList<>();
        int tileToMovePos;
        int emptySpotPos = findPositionOfTileInBoardArray(0);

        // Determine which moves are valid to move into blank space(up, down, left, right)
        // If valid, then add new board to arraylist and return, else add null

        // move up (array position of tile to move is 3 more)
        tileToMovePos = emptySpotPos + 3;
        if(isValidBoardPosAndMove(emptySpotPos, tileToMovePos))
        {
            validBoardMoves.add(createNewBoardString(emptySpotPos, tileToMovePos));
        }
        else { validBoardMoves.add(null); }

        // move down (array position of tile to move is 3 less)
        tileToMovePos = emptySpotPos - 3;
        if(isValidBoardPosAndMove(emptySpotPos, tileToMovePos))
        {
            validBoardMoves.add(createNewBoardString(emptySpotPos, tileToMovePos));
        }
        else { validBoardMoves.add(null); }

        // move left (array position of tile to move is 1 more)
        tileToMovePos = emptySpotPos + 1;
        if(isValidBoardPosAndMove(emptySpotPos, tileToMovePos))
        {
            validBoardMoves.add(createNewBoardString(emptySpotPos, tileToMovePos));
        }
        else { validBoardMoves.add(null); }

        // move right(array position of tile to move is 1 less)
        tileToMovePos = emptySpotPos - 1;
        if(isValidBoardPosAndMove(emptySpotPos, tileToMovePos))
        {
            validBoardMoves.add(createNewBoardString(emptySpotPos, tileToMovePos));
        }
        else { validBoardMoves.add(null); }

        return validBoardMoves;
    }

    // @returns true if the board position is within the bounds of the board
    private Boolean isValidBoardPosAndMove(int emptySpotPos, int tileToMovePos)
    {
        // Check edge cases: next to each other in array, but not swappable
        if((emptySpotPos == 2 && tileToMovePos == 3) ||
           (emptySpotPos == 3 && tileToMovePos == 2) ||
           (emptySpotPos == 5 && tileToMovePos == 6) ||
           (emptySpotPos == 6 && tileToMovePos == 5))
            return false;

        return (tileToMovePos >= 0 && tileToMovePos < Constants.BOARDSIZE);
    }


    // @return new board string after move if valid, otherwise null
    // @param tileToMove
    public String makeMove(int tile)
    {
        int tileToMovePos = findPositionOfTileInBoardArray(tile);
        int emptySpotPos = findPositionOfTileInBoardArray(0);

        // If tileToMove is adjacent to the empty spot, then make move, return new board
        // If not a valid move, return null
        if(isValidMove(emptySpotPos, tileToMovePos))
        {
            return createNewBoardString(emptySpotPos, tileToMovePos);
        }
        return "";
    }

    // Checks to see if tileToMove is adjacent to empty spot
    // @return true if valid move
    private Boolean isValidMove(int emptySpotPos, int tileToMovePos)
    {
        // Check edge cases: next to each other in array, but not swappable
        if((emptySpotPos == 2 && tileToMovePos == 3) ||
           (emptySpotPos == 3 && tileToMovePos == 2) ||
           (emptySpotPos == 5 && tileToMovePos == 6) ||
           (emptySpotPos == 6 && tileToMovePos == 5))
            return false;

        int move = tileToMovePos - emptySpotPos;
        return (move == Constants.MOVEUP || move == Constants.MOVEDOWN ||
                move == Constants.MOVELEFT || move == Constants.MOVERIGHT);
    }

    // @returns string representation of board given a new move
    private String createNewBoardString(int emptySpotPos, int tileToMovePos)
    {
        // Convert original board string to char array for swap
        char[] originalBoard = boardAsString.toCharArray();

        // Swap blank space and tile to move
        originalBoard[emptySpotPos] = originalBoard[tileToMovePos];
        originalBoard[tileToMovePos] = '0';

        String newBoardString = new String(originalBoard);
        return newBoardString;
    }

    // @return position of given tile in board array
    public int findPositionOfTileInBoardArray(int tile)
    {
        // Iterate through array to find position of tile in board array
        for(int x = 0; x < Constants.BOARDSIZE; x++)
        {
            if(board[x] == tile)
                return x;
        }

        return -1;
    }

    // @return a string to print the 3X3 board
    public String toString()
    {
        String grid = "";
        int i = 0;

        for(int x = 0; x < Constants.numRows; x++)
        {
            grid = grid + "   ";
            for(int y = 0; y < Constants.numCols; y++, i++)
            {
                if(board[i] == 0)
                    grid = grid + "  ";
                else
                    grid = grid + board[i] + " ";
            }
            grid = grid + "\n";
        }

        return grid;
    }
}
