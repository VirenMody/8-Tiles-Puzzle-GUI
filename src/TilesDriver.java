/*
Name: Viren Mody
Class: CS 342, Fall 2016
Professor Dale Reed
System: Windows 10, IntelliJ IDE

Program: #4, 8 Tiles GUI
Due: 11/21/2016 11:59 PM

Program Description:
This program simulates the puzzle 8 tiles using a GUI in which
the goal is to arrange 8 tiles, numbered 1-8, in numerical order
on a 3X3 grid with only one open spot in which to move tiles to.

User options:
- Play on a randomly generated grid or personally chosen grid
- User play or have the computer solve the puzzle

**************************************************

Class: TilesDriver
This class is the driver for the 8 Tiles Puzzle. It is the
graphical user interface for prompting the user, game play,
and output to the screen.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class TilesDriver extends JFrame
{

    private int moveCount = 0;

    public static void main(String[] args)
    {
        TilesDriver driver = new TilesDriver();
        //driver.startGame();  // CONSOLE VERSION - LEFT INTENTIONALLY


        // Set the look and feel to be cross-platform, so that setBackground() works
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // EventQueue.invokeLater is to allow updates of the GUI during autosolve method
        EventQueue.invokeLater(
                new Runnable() {
                    public void run(){
                        PanelFrame panelFrame = new PanelFrame();
                        panelFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                        panelFrame.pack();
                        //panelFrame.setSize( 400, 300 ); // set frame size
                        panelFrame.setLocationRelativeTo(null);
                        panelFrame.setVisible( true ); // display frame
                    }
                }
        );

    }


    // **********************************************************************************
    //  THE FOLLOWING FUNCTIONS HAVE BEEN KEPT INTENTIONALLY FOR THE CONSOLE VERSION
    // **********************************************************************************

    private void startGame()
    {
        printGameIntro();

        // Returns a randomly generated board or user input board
        Node node = promptUserForGameBoard();

        System.out.print("\nInitial board is:");
        printBoard(node);

        // Loop to simulate game play, if user enters 's', solve automatically and exit
        while(node.getHeuristicValue() != 0)
        {
            int tileToMove = getNextTileToMove();
            if(tileToMove == -1) // Invalid Tile - print error and reprompt user
                ;
            else if(tileToMove == 0) // MODE: Quit
            {
                System.out.println("Quitting game......");
                break;
            }
            else if(tileToMove == 999) // MODE: AutoSolve
            {
                autoSolveMode(node);
            }
            else // MODE: User game play
            {
                // Try user's next move, if invalid print error, otherwise update board
                String newBoard = node.getBoard().makeMove(tileToMove);
                if(newBoard == null)
                {
                    System.out.println("*** Invalid move. Please retry.");
                    continue;
                }

                node = new Node(newBoard);
                moveCount++;
                printBoard(node);
            }
        }
        System.out.println("Done.");
    }

    // Automatically solves game board using SearchTree
    // @param node - current board in which user entered autosolve mode
    private void autoSolveMode(Node node)
    {
        System.out.println("Solving puzzle automatically...............");
        SearchTree st = new SearchTree();
        Node solutionNode = st.autoSolve(node);

        // If heuristic value is 0, print solution path and exit
        // otherwise, board is impossible to solve - print best solution possible and exit
        if(solutionNode.getHeuristicValue() == 0)
        {
            recursivePrintSolution(solutionNode);
            System.out.println("Done.");
        }
        else
        {
            // Set moveCount to size of hashmap to show all possibilities have been tried
            moveCount = st.getNumMovesTried();
            printImpossibleSolution(solutionNode);
        }
        System.exit(0);
    }

    // Recursively traces to root node and prints solution path produced by autosolve
    // @param currNode is solution leaf
    private void recursivePrintSolution(Node currNode)
    {
        Node parent = currNode.getParent();
        if(parent != null)
        {
            // recursive call to traverse up solution path parent by parent
            recursivePrintSolution(parent);
        }

        // If statement is to prevent reprinting of root node board
        if(parent != null)
        {
            moveCount++;
            printBoard(currNode);
        }
    }

    // Prints out best possible solution board when board is impossible to solve
    // @param best solution board possible
    private void printImpossibleSolution(Node solutionNode)
    {
        System.out.println("\n");
        System.out.println("All "+moveCount+" moves have been " +
                "tried.");
        System.out.println("That puzzle is impossible to solve. Best board " +
                "found was:");
        System.out.print(solutionNode.getBoard());
        System.out.println("Heuristic value: "+
                solutionNode.getHeuristicValue()+"\n");
        System.out.println("Exiting program.");
    }

    // Gets next tile to move (or autosolve mode) from user
    // @return next tile to move: error: -1, quit: 0, autosolve: 999, else tile to move
    private int getNextTileToMove()
    {
        String response;
        int tileToMove;
        System.out.print("Piece to move: ");

        Scanner sc = new Scanner(System.in);
        response = sc.next();
        if(response.equals("s") || response.equals("S"))
            return 999;

        tileToMove = Integer.parseInt(response);
        if(tileToMove < 0 || tileToMove >= Constants.BOARDSIZE)
        {
            System.out.println("ERROR: Tile is not " +
                                "between 1 and "+(Constants.BOARDSIZE-1));
            return -1;
        }

        return tileToMove;
    }

    // Prints move count, board, and heuristic value
    // @param node
    private void printBoard(Node node)
    {
        System.out.println();
        System.out.println(moveCount+".");
        System.out.print(node.getBoard());
        System.out.println("Heuristic Value: "+node.getHeuristicValue());
    }

    // Prompt user to play a random board or user-chosen
    // @return node of board (randomly generated or from user input)
    private Node promptUserForGameBoard()
    {
        // Option 1: Randomly generated puzzle board
        // Option 2: User input puzzle board
        return (getGameOption() == 1)? new Node(): new Node(getBoardFromUserInput());

        /*
        if(getGameOption() == 1)
        {
            return new Node();
        }
        else // getGameOption == 2
        {
            return new Node(getBoardFromUserInput());
        }
        */
    }

    // @return string board from user input
    private String getBoardFromUserInput()
    {
        System.out.print("Some boards such as 728045163 are impossible.\n" +
                "Others such as 245386107 are possible.\n" +
                "Enter a string of 9 digits (including 0) for the board --> ");
        Scanner sc = new Scanner(System.in);
        String userBoard = sc.next();
        return userBoard;
    }


    // @return int game option chosen (Random board: 1, From user input: 2)
    private int getGameOption()
    {

        int option = 0;
        while(option == 0)
        {
            Scanner sc = new Scanner(System.in);
            System.out.print("Choose a game option: \n" +
                    "  1. Start playing \n" +
                    "  2. Set the starting configuration\n" +
                    "Enter your choice --> ");

            if(sc.hasNextInt())
            {
                option = sc.nextInt();
                if (option == 1) return 1;
                else if (option == 2) return 2;
                else option = 0;
            }
        }
        return 0;
    }

    private void printGameIntro()
    {
        System.out.println("Welcome to the 8-tiles puzzle. \n" +
                "Place the tiles in ascending numerical order.  For each  \n" +
                "move enter the piece to be moved into the blank square, \n" +
                "or 0 to exit the program.\n");
    }

    private void printAuthorInfo()
    {
        System.out.println("Author: Viren Mody\n" +
                "Class: CS 342, Fall 2016 \n" +
                "Program: #4, 8 Tiles GUI. \n\n");
    }
}
