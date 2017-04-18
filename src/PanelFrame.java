/*
Class: PanelFrame
This class is the front end of the 8 tiles puzzle game. This class is responsible for
displaying the GUI and calling methods from the backend to run the game.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;


public class PanelFrame extends JFrame
{
    private JPanel buttonJPanel;
    private JPanel controlButtonJPanel; // panel to hold controlButtons
    private JButton buttons[];          // array of 9 buttons
    private JButton controlButtons[];   // array of controlButtons
    private JLabel statusBar;           // label that displays event information

    private Node node;                  // Board node
    private int heuristicVal;
    private Boolean isSolved = false;   // Flag: board state is solved
    private int moveCount = 0;
    private int setTileCount = 0;       // Number of tiles set so far for "Set Board"
    // "setboard"

    private Timer autoSolveTimer;       // Timer for every move in "Auto Solve" mode

    // no-argument constructor
    public PanelFrame()
    {
        super("8 Tiles Puzzle" );
        displayAuthorInfo();

        // Returns a randomly generated board on start as long as it is not already solved
        do
        {
            node = new Node();
            heuristicVal = node.getHeuristicValue();
            isSolved = (heuristicVal == 0)? true:false;
        }while(isSolved);


        createTileButtons();
        createControlButtons();

        // Initialize the status bar
        statusBar = new JLabel();
        updateStatusBar();
        setFontSize(statusBar, 18);

        // Add the various components to the panel
        getContentPane().add(buttonJPanel, BorderLayout.NORTH ); // add to JFrame
        getContentPane().add(controlButtonJPanel, BorderLayout.CENTER ); // add to JFrame
        getContentPane().add(statusBar, BorderLayout.SOUTH );    // add to JFrame
    } // end PanelFrame constructor

    // Creates the puzzle tiles 0-9 and makes 0 an empty spot
    private void createTileButtons()
    {
        // Setup the buttons for the top of the screen
        buttons = new JButton[ 9];      // create buttons array
        buttonJPanel = new JPanel();    // set up panel
        buttonJPanel.setLayout(new GridLayout(3,3));  // 3x3 GridLayout
        //buttonJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        //Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        //buttonJPanel.setBorder(raisedEtched);

        int[] board = node.getBoard().getBoardArr();

        // Create and add numbered buttons
        for(int i = 0; i < Constants.BOARDSIZE; i++)
        {
            buttons[i] = new JButton(Integer.toString(board[i]));

            // Make 0 Tile an Empty Spot
            buttons[i].setVisible(board[i]!=0);

            //buttons[i].setPreferredSize(new Dimension(10,30));
            setFontSize(buttons[i], 20);
            // JPanel does not have a content pane, unlike JFrame
            buttonJPanel.add(buttons[i]); // add button to panel
            // Register the event handler
            buttons[i].addActionListener(new tileButtonSlideEventHandler(i));
        }
    }

    // Randomizes the puzzle tiles
    private void randomizeBoard()
    {
        // Returns a randomly generated board on start as long as it is not already solved
        do
        {
            node = new Node();
            heuristicVal = node.getHeuristicValue();
            isSolved = (heuristicVal == 0)? true:false;
        }while(isSolved);

        int[] board = node.getBoard().getBoardArr();
        // Create and add numbered buttons
        for(int i = 0; i < 9; i++) {
            buttons[i].setText(Integer.toString(board[i]));

            // Make 0 Tile an Empty Spot
            buttons[i].setVisible(board[i]!=0);
            setFontSize(buttons[i], 20);
        }
        updateStatusBar();

    }

    //***************************************************************************
    // inner class to handle button events for grid.  From GridandIcon  example
    private class tileButtonSlideEventHandler implements ActionListener
    {
        int index;   // tells what position piece is in

        //---------------------------------------------------------
        // constructor stores these values for this pieces's event handler
        public tileButtonSlideEventHandler(int theIndex) {
            this.index = theIndex;
        }

        //---------------------------------------------------------
        // handle button events
        public void actionPerformed(ActionEvent event )
        {

            if(isValidBoardToSolve())
            {
                JButton thisButton = (JButton) event.getSource();
                int emptySpotPos = node.getBoard().findPositionOfTileInBoardArray(0);

                // TO DO CHECK FOR VALID MOVE
                String tileToMove = thisButton.getText();
                int tileToMoveAsInt = Integer.parseInt((tileToMove));
                String newBoard = node.getBoard().makeMove((tileToMoveAsInt));

                if(!(newBoard.equals("")))
                {
                    node = new Node(newBoard);
                    buttons[emptySpotPos].setText(tileToMove);
                    thisButton.setText("0");
                    buttons[emptySpotPos].setVisible(true);
                    thisButton.setVisible(false);
                    moveCount++;
                }

                heuristicVal = node.getHeuristicValue();
                isSolved = (heuristicVal == 0)? true:false;
                updateStatusBar();
                if(isSolved) { PanelFrame.this.displayWinnerMessage(); }
            }
        } // end method actionPerformed
    }// end inner class tileButtonSlideEventHandler


    //--------------------------------------------------------------------------------
    // Alllows user to set the board tiles
    private void setBoard()
    {
        for(int i = 0; i < Constants.BOARDSIZE; i++)
        {
            buttons[i] = new JButton("Pick Me!");
            buttonJPanel.add(buttons[i]);
            setFontSize(buttons[i], 20);
            buttons[i].addActionListener(new setTileButtonEventHandler(i));
        }
    }

    // Handles events to set the tiles on board
    private class setTileButtonEventHandler implements ActionListener
    {
        int index;   // tells what position piece is in

        //---------------------------------------------------------
        // constructor stores these values for this pieces's event handler
        public setTileButtonEventHandler(int theIndex) {
            this.index = theIndex;
            setTileCount = 0;
        }

        //---------------------------------------------------------
        // handle button events
        public void actionPerformed(ActionEvent event )
        {
            JButton thisButton = (JButton) event.getSource();

            thisButton.setText(Integer.toString(setTileCount));
            // Make 0 Tile an Empty Spot
            thisButton.setVisible(setTileCount!=0);
            thisButton.removeActionListener(this);
            setTileCount++;

            if(setTileCount == Constants.BOARDSIZE)
            {
                String userInputBoard = "";
                // reset setTileCount to 0
                setTileCount = 0;
                for(int i = 0; i < Constants.BOARDSIZE; i++)
                {
                    buttons[i].addActionListener(new tileButtonSlideEventHandler(i));
                    userInputBoard += buttons[i].getText();
                }
                node = new Node(userInputBoard);
                isSolved = (node.getHeuristicValue() == 0)? true:false;
                updateStatusBar();

                if(isSolved)
                {
                    JOptionPane.showMessageDialog(null, "That's a No No! You must earn " +
                            "your win!! Please pick a board that doesn't automatically " +
                            "win!", "WARNING!", JOptionPane.WARNING_MESSAGE);
                    controlButtons[1].doClick();
                }
            }
        } // end method actionPerformed
    }

    //--------------------------------------------------------------------------------
    // Creates buttons for Solve, Exit, Set Board, Randomize Board
    private void createControlButtons()
    {
        // Setup the control buttons for the bottom of the screen
        controlButtons = new JButton[ 4 ]; // create controlButtons array
        controlButtonJPanel = new JPanel(); // set up panel
        // GridLayout 1 row high, controlButtons.length rows wide
        controlButtonJPanel.setLayout(new FlowLayout());

        // Create and add controlButtons
        controlButtons[0] = new JButton("Randomize");
        controlButtons[1] = new JButton("Set Board");
        controlButtons[2] = new JButton("Solve");
        controlButtons[3] = new JButton("Exit");

        for (JButton i: controlButtons)
        {
            setFontSize(i, 20);
            controlButtonJPanel.add(i); // add button to panel
            i.addActionListener(new controlButtonHandler() );
        }
    }

    //***************************************************************************
    // inner class to handle button events for grid.
    private class controlButtonHandler implements ActionListener
    {

        //---------------------------------------------------------
        public void actionPerformed(ActionEvent event )
        {
            statusBar.setText("Selected button: " + event.getActionCommand());
            JButton thisButton = (JButton) event.getSource();

            if(event.getActionCommand().equals("Exit"))
            {
                statusBar.setText("Exiting program...");
                if(!isSolved)
                {
                    JOptionPane.showMessageDialog(null, "Better luck next time...",
                            "Exiting...", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Good Bye!", "Exiting...",
                                                    JOptionPane.INFORMATION_MESSAGE);

                }
                System.exit(1);
            }
            else if (event.getActionCommand().equals(("Randomize")))
            {
                moveCount = 0;
                isSolved = false;
                randomizeBoard();
            }
            else if (event.getActionCommand().equals(("Set Board")))
            {
                moveCount = 0;
                isSolved = false;
                removeAllButtons();
                setBoard();
            }
            else if (event.getActionCommand().equals(("Solve")))
            {
                if(isValidBoardToSolve())
                {
                    isSolved = true;
                    //disableAllButtons();
                    autoSolveMode();
                    //enableAllButtons();
                    moveCount = 0;
                }
            }

        }
    }// end inner class controlButtonHandler


    //--------------------------------------------------------------------------------
    private void setFontSize(JComponent theComponent, int size)
    {
        theComponent.setFont(new Font("Arial", Font.PLAIN, size));
    }

    // Removes all buttons
    private void removeAllButtons()
    {
        for(JButton i: buttons)
        {
            buttonJPanel.remove(i);
        }
    }

    private void disableAllButtons()
    {
        for(JButton x: buttons)
        {
            x.setEnabled(false);
        }

        for(JButton x: controlButtons)
        {
            x.setEnabled(false);
        }
    }

    private void enableAllButtons()
    {
        for(JButton x: buttons)
        {
            x.setEnabled(true);
        }

        for(JButton x: controlButtons)
        {
            x.setEnabled(true);
        }
    }

    // TO DO
    private Boolean isValidBoardToSolve()
    {
        // Not valid
        // 1) Middle of setting board
        // 2) already solved
        for(JButton currButton: buttons)
        {
            if (currButton.getText().equals("Pick Me!"))
            {
                JOptionPane.showMessageDialog(null, "Cannot solve an incomplete " +
                        "board! Please complete the board first.", "WARNING!",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if(isSolved)
        {
            JOptionPane.showMessageDialog(null, "Board is already solved! Please " +
                    "click \"Randomize Board\" or \"Set Board\".", "WARNING!",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void displayWinnerMessage()
    {
        JOptionPane.showMessageDialog(null, "Congratulations! You solved the " +
                        "puzzle...\nbut do you dare try again?\nClick \"Randomize " +
                        "Board\" or \"Set Board\".", "We got a winner!",
                        JOptionPane.INFORMATION_MESSAGE);
    }

    // Automatically solves game board using SearchTree
    // @param node - current board in which user entered autosolve mode
    private void autoSolveMode()
    {
        System.out.println("Solving puzzle automatically...............");
        SearchTree st = new SearchTree();
        Node solutionNode = st.autoSolve(node);
        Stack<Node> solutionPathStack = st.createSolutionPath(solutionNode);

        ActionListener listener = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Node temp = solutionPathStack.pop();
                int[] boardArr = temp.getBoard().getBoardArr();
                moveCount++;
                heuristicVal = temp.getHeuristicValue();
                //printBoard(temp);
                for (int i = 0; i < buttons.length; i++)
                {
                    buttons[i].setText(Integer.toString(boardArr[i]));
                    buttons[i].setVisible(boardArr[i]!=0);
                }
                updateStatusBar();
                if(solutionPathStack.isEmpty()) { autoSolveTimer.stop(); }
            }
        };
        autoSolveTimer = new Timer(300, listener);
        autoSolveTimer.start();

    }

    private void updateStatusBar()
    {
        statusBar.setText("Heuristic Value: "+heuristicVal+"     Move Count: "+moveCount);
    }

    // *************************************************************************

    private void displayAuthorInfo()
    {

        JOptionPane.showMessageDialog(null, "Welcome to the 8 Tiles Puzzle Game!\n\n" +
                "Name: Viren Mody\n" +
                "Class: CS 342, Fall 2016\n" +
                "Program: #4, 8 Tiles GUI",
                "8 Tiles Puzzle", JOptionPane.INFORMATION_MESSAGE);

    }

    private void displayAutoSolveMessage()
    {
        if(heuristicVal == 0)
            JOptionPane.showMessageDialog(null, "Board automatically solved in "
                    +moveCount+" moves.", "Auto-Solve", JOptionPane.INFORMATION_MESSAGE);
        else
        {
            JOptionPane.showMessageDialog(null, "Board is unsolvable, but best board " +
                    "was found in "+moveCount+" moves with a heuristic value of " +
                    heuristicVal+".", "Auto-Solve", JOptionPane.INFORMATION_MESSAGE);
        }
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

} // end class PanelFrame

