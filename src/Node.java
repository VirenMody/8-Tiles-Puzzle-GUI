/*
Class: Node
This class represents nodes of the SearchTree class. It is a container for the puzzle
board and pointers to potential next move board nodes (up, down, left, right). It
implements comparable based on heuristic value for priority queue in SearchTree class.
*/

public class Node implements Comparable<Node>
{
    private Board   board;
    private Node    parent;
    private Node    upChild;
    private Node    downChild;
    private Node    leftChild;
    private Node    rightChild;

    // Constructor
    public Node()
    {
        board = new Board();
        parent = null;
        upChild = null;
        downChild = null;
        leftChild = null;
        rightChild = null;
    }

    // Chained Constructor: Board from user input
    public Node(String boardString)
    {
        board = new Board(boardString);
        parent = null;
        upChild = null;
        downChild = null;
        leftChild = null;
        rightChild = null;
    }

    // Compare function for Priority Queue/MinHeap on Heuristic Value
    @Override
    public int compareTo(Node otherNode)
    {
        return Integer.compare(this.board.getHeuristicValue(),
                                otherNode.board.getHeuristicValue());
    }

    public Board getBoard()
    {
        return board;
    }

    public Node getParent()
    {
        return parent;
    }


    public Node getUpChild()
    {
        return upChild;
    }

    public void setUpChild(Node upChild)
    {
        this.upChild = upChild;
        upChild.parent = this;
    }

    public Node getDownChild()
    {
        return downChild;
    }

    public void setDownChild(Node downChild)
    {
        this.downChild = downChild;
        downChild.parent = this;
    }

    public Node getLeftChild()
    {
        return leftChild;
    }

    public void setLeftChild(Node leftChild)
    {
        this.leftChild = leftChild;
        leftChild.parent = this;
    }

    public Node getRightChild()
    {
        return rightChild;
    }

    public void setRightChild(Node rightChild)
    {
        this.rightChild = rightChild;
        rightChild.parent = this;
    }

    // @return heuristic value of board
    public int getHeuristicValue()
    {
        return board.getHeuristicValue();
    }


}