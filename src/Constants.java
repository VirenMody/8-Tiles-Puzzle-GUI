// TO DO


import static java.lang.Math.sqrt;

public final class Constants
{
    public static int BOARDSIZE = 9;
    public static int numRows = (int) sqrt(Constants.BOARDSIZE);
    public static int numCols = (int) sqrt(Constants.BOARDSIZE);


    // Maps board array's current position to its row and column position on the board
    public static int[] arrToBoardRowMapping = {0, 0, 0, 1, 1, 1, 2, 2, 2};
    public static int[] arrToBoardColMapping = {0, 1, 2, 0, 1, 2, 0, 1, 2};

    // Given a tile number (index), these arrays will give tile's end goal row and column
    public static int[] tileGoalRow = {2, 0, 0, 0, 1, 1, 1, 2, 2};
    public static int[] tileGoalCol = {2, 0, 1, 2, 0, 1, 2, 0, 1};

    // TO DO
    public static int MOVEUP = (int) sqrt(Constants.BOARDSIZE) * -1;
    public static int MOVEDOWN = (int) sqrt(Constants.BOARDSIZE);
    public static int MOVELEFT = -1;
    public static int MOVERIGHT = 1;

    // HashMap initial size
    public static int HMAPSIZE = 200000;


    private Constants()
    {}
}
