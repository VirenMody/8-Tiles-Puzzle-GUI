/*
Class: SearchTree
This class implements autosolve mode as specified in TilesDriver class. A hashmap is
used to determine if a board has been visited before as to avoid repeating work or
looping. A priority queue is used to implement a minheap to check the next move
(childen) boards with the best/lowest heuristic value first for efficiency as opposed
to a brute force method. If a solution is not found, returns the best possible board.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class SearchTree
{
    private HashMap             nodeHMap;
    private PriorityQueue<Node> pQueue;
    private Stack<Node>         solutionPathStack;

    private Node                bestBoardSoFar;
    private int                 minHeuristicValSoFar;
    private int                 numMoves;



    // CONSTRUCTOR
    public SearchTree()
    {
        nodeHMap = new HashMap(Constants.HMAPSIZE);
        pQueue = new PriorityQueue<Node>();
        solutionPathStack = new Stack<Node>();
        bestBoardSoFar = null;
        minHeuristicValSoFar = Integer.MAX_VALUE;
        numMoves = 0;
    }

    // @return the size of the hash map indicated number of unique board moves tried
    public int getNumMovesTried()
    {
        return nodeHMap.size();
    }

    // @returns the solution node, or the best solution board if the board is
    // imposssible to solve
    public Node autoSolve(Node rootNode)
    {
        bestBoardSoFar = rootNode;
        minHeuristicValSoFar = rootNode.getHeuristicValue();

        // Add root node to hashmap and PriorityQueue
        nodeHMap.put(rootNode.getBoard().getBoardAsString(), rootNode);
        pQueue.add(rootNode);

        // Loop through until priority queue is empty.
        // If empty, then there is no solution, return best possible solution
        while(!pQueue.isEmpty())
        {
            // Pop node board with minimum heuristic value and get all possible
            // next/potential board moves.
            Node boardNode = pQueue.poll();
            ArrayList<String> nextMoves = boardNode.getBoard().getPotentialBoardMoves();

            // Check if each next move board is unique using hash map
            // If unique, add node to tree, hash it, add to priority queue
            // If it has a heuristic value of zero, return it as the solution node

            // Potential Next Move: Up
            if(nextMoves.get(0) != null)
            {
                if(!nodeHMap.containsKey(nextMoves.get(0)))
                {
                    addNodeToTreeHashQueue(boardNode, 0, nextMoves.get(0));
                    if(boardNode.getUpChild().getHeuristicValue() == 0)
                        return boardNode.getUpChild();
                }
            }

            // Potential Next Move: Down
            if(nextMoves.get(1) != null)
            {
                if(!nodeHMap.containsKey(nextMoves.get(1)))
                {
                    addNodeToTreeHashQueue(boardNode, 1, nextMoves.get(1));
                    if(boardNode.getDownChild().getHeuristicValue() == 0)
                        return boardNode.getDownChild();
                }
            }

            // Potential Next Move: Left
            if(nextMoves.get(2) != null)
            {
                if(!nodeHMap.containsKey(nextMoves.get(2)))
                {
                    addNodeToTreeHashQueue(boardNode, 2, nextMoves.get(2));
                    if(boardNode.getLeftChild().getHeuristicValue() == 0)
                        return boardNode.getLeftChild();
                }
            }

            // Potential Next Move: Right
            if(nextMoves.get(3) != null)
            {
                if(!nodeHMap.containsKey(nextMoves.get(3)))
                {
                    addNodeToTreeHashQueue(boardNode, 3, nextMoves.get(3));
                    if(boardNode.getRightChild().getHeuristicValue() == 0)
                        return boardNode.getRightChild();
                }
            }
        }

        // Priority Queue is empty - there is no solution, return best solution possible
        return bestBoardSoFar;
    }

    // Adds a next move/child node to the Search Tree, HashMap, and Priority Queue
    // Also updates best board and heuristic value so far
    private void addNodeToTreeHashQueue(Node parent, int move, String boardAsString)
    {
        Node child = new Node(boardAsString);
        if(move == 0)
            parent.setUpChild(child);
        else if(move == 1)
            parent.setDownChild(child);
        else if(move == 2)
            parent.setLeftChild(child);
        else
            parent.setRightChild(child);
        nodeHMap.put(boardAsString, child);
        pQueue.add(child);

        if(child.getHeuristicValue() < minHeuristicValSoFar)
        {
            minHeuristicValSoFar = child.getHeuristicValue();
            bestBoardSoFar = child;
        }
    }

    // TO DO
    public Stack<Node> createSolutionPath(Node currNode)
    {
        while(currNode.getParent() != null)
        {
            solutionPathStack.push(currNode);
            currNode = currNode.getParent();
            numMoves++;
        }
        return solutionPathStack;
    }

    public int getNumMoves()
    {
        return numMoves;
    }

}

