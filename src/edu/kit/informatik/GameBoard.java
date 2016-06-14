/*
 * COPYRIGHT (C) 2016, UNKNOWN6656
 */

package edu.kit.informatik;

import java.util.ArrayList;

/**
 * Represents a game board
 * @author Unknown6656
 * @version 1
 */
public class GameBoard
{
    /**
     * The value assigned to a player-field, if the player is not (yet) defined
     */
    public static final byte NOT_DEFINED = -0x7f;
    /* y := Row
     * x := Column
     */
    private final int size;
    private Piece[][] field; // [row][column] // [y][x]
    private byte player;
    private boolean torus;
    private Piece[] bag;
    private Piece selected = null;
    
    
    /**
     * Creates a new game board using the size of 6
     */
    public GameBoard()
    {
        this(6, false);
    }
    
    /**
     * Creates a new game board using the given size
     * @param size Game board size
     */
    public GameBoard(int size)
    {
        this(size, false);
    }
    
    /**
     * Creates a new game board using the given size
     * @param size Game board size
     * @param torus Indicates, whether the board is a torus
     */
    public GameBoard(int size, boolean torus)
    {
        super();

        this.size = size;
        this.torus = torus;
        
        reset();
    }
    
    /**
     * Returns, whether the current game board is a torus
     * @return true == torus, false == standard
     */
    public boolean isTorus()
    {
        return this.torus;
    }

    /**
     * Returns the game board's size
     * @return Game board size
     */
    public int getSize()
    {
        return this.size;
    }
    
    /**
     * Returns the current player
     * @return Player
     */
    public byte getPlayer()
    {
        return this.player;
    }
    
    /**
     * Sets the current player to the given value
     * @param player New player value 
     */
    public void setPlayer(byte player)
    {
        this.player = player;
    }
 
    /**
     * Changes the internal `player`-field to the next player
     */
    public void nextPlayer()
    {
        this.player = (byte) (this.player == 1 ? 0 : 1);
    }

    /**
     * Returns the string representation of the given row
     * @param row Row
     * @return String representation
     */
    public String printRow(int row)
    {
        StringBuilder sb = new StringBuilder();
        
        int nrow = isTorus() ? Final02.mod(row, size) : row;

        if ((nrow < 0) || (nrow >= size))
            Final02.out("Error, the row number must be a valid integer number between 0 and %d.", size - 1);
        else
            for (int x = 0; x < size; x++)
                sb.append(field[nrow][x] == null ? "#" : field[nrow][x].value())
                  .append(' ');
        
        return sb.toString().trim();
    }
    
    /**
     * Returns the string representation of the given column
     * @param col Column
     * @return String representation
     */
    public String printColumn(int col)
    {
        StringBuilder sb = new StringBuilder();

        int ncol = isTorus() ? Final02.mod(col, size) : col;

        if ((ncol < 0) || (ncol >= size))
            Final02.out("Error, the column number must be a valid integer number between 0 and %d.", size - 1);
        else
            for (int y = 0; y < size; y++)
                sb.append(field[y][ncol] == null ? "#" : field[y][ncol].value())
                  .append(' ');
        
        return sb.toString().trim();
    }

    /**
     * Returns the string representation of the complete field
     * @return String representation
     */
    public String print()
    {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < size; y++)
        {
            for (int x = 0; x < size; x++)
                sb.append(field[y][x] == null ? "#" : field[y][x].value())
                  .append(' ');
            
            sb.append('\n');
        }
        
        return sb.toString().trim();
    }
    
    /**
     * Sets the given piece on the field
     * @param piece Game piece
     * @param x Game piece X (horizontal) position
     * @param y Game piece Y (vertical) position
     * @return Action return code<br/>
     * 0 := OK<br/>
     * 1 := Outside the field / Invalid position<br/>
     * 2 := Invalid byte value / Piece already used<br/>
     * 3 := Cell already in use
     */
    public byte placePiece(byte piece, int x, int y)
    {
        int nx = x;
        int ny = y; // assignment is not allowed --> value copy
        
        if (this.isTorus()) // prevent negative values and modulo the position
        {
            nx = Final02.mod(nx, size);
            ny = Final02.mod(ny, size);
        }
        else if ((nx < 0) || (ny < 0) || (nx >= this.size) || (ny >= this.size))
            return 1;
        
        if ((piece < 0x00) || (piece > 0x0f))
            return 2;
        
        if (this.field[ny][nx] == null)
        {
            this.bag[piece].setUsed(true);
            this.field[ny][nx] = this.bag[piece];
            
            return 0;
        }
        else
            return 3;
    }
    
    /**
     * Returns the so-called game bag, which consists of all unused game pieces
     * @return Game piece bag
     */
    public ArrayList<Piece> getBag()
    {
        ArrayList<Piece> res = new ArrayList<Piece>();
        
        for (int i = 0, l = this.bag().length; i < l; i++)
            if (!this.bag[i].isUsed())
                res.add(this.bag[i]);
        
        return res;
    }

    /**
     * Returns, whether a winner exists
     * @param x The X coordinate of the just set game piece
     * @param y The Y coordinate of the just set game piece
     * @return Winner
     */
    protected boolean getWinner(int x, int y)
    {
        return getWinnerHorizontal(x, y)
            || getWinnerVertical(x, y)
            || getWinnerDiagonal1(x, y)
            || getWinnerDiagonal2(x, y);
    }

    /**
     * Resets the game board
     */
    public void reset()
    {
        this.player = NOT_DEFINED;
        this.field = new Piece[size][size];
        this.bag = new Piece[0x10];
        
        for (byte i = 0x00; i <= 0x0f; i++)
        {
            this.bag[i] = new Piece(i);
            this.bag[i].setUsed(false);
        }
    }
    
    // check horizontally for a winner
    private boolean getWinnerHorizontal(int x, int y)
    {
        byte tmp = 0x00; // byte mask
        int cnt = 0x00; // internal counter

        for (int i = -4; i <= 4; i++)
        {
            if (((x + i < 0) || (x + i >= this.size)) && !isTorus())
                continue;

            Piece p = field[y][Final02.mod(x + i, this.size)];
           
            if (p == null) // piece is null
            {
                cnt = 0x00;
                tmp = 0x00;
            }
            else if (tmp == 0x00) // not null and mask is zero
            {
                tmp = p.value();
                cnt = 0x01;
            }
            else // piece not null, mask not zero
            {
                tmp &= p.value();
                
                if (tmp == 0x00) // mask has become zero --> reset counter
                {
                    cnt = 0x01;
                    tmp = p.value();
                }
                else
                    cnt++; // increase counter
            }
            
            if (cnt >= 4)
                return true;
        }
        
        return false;
    }

    // check vertically for a winner
    private boolean getWinnerVertical(int x, int y)
    {
        byte tmp = 0x00; // byte mask
        int cnt = 0x00; // internal counter

        for (int i = 4; i <= 4; i++)
        {
            if (((y + i < 0) || (y + i >= this.size)) && !isTorus())
                continue;

            Piece p = field[Final02.mod(y + i, this.size)][x];
           
            if (p == null) // piece is null
            {
                cnt = 0x00;
                tmp = 0x00;
            }
            else if (tmp == 0x00) // not null and mask is zero
            {
                tmp = p.value();
                cnt = 0x01;
            }
            else // piece not null, mask not zero
            {
                tmp &= p.value();
                
                if (tmp == 0x00) // mask has become zero --> reset counter
                {
                    cnt = 0x01;
                    tmp = p.value();
                }
                else
                    cnt++; // increase counter
            }
            
            if (cnt >= 4)
                return true;
        }
        
        return false;
    }

    // check the first diagonal [ f(x) = -x in a Cartesian coordinate system]
    private boolean getWinnerDiagonal2(int x, int y)
    {
        byte tmp = 0x00; // byte mask
        int cnt = 0x00; // internal counter

        for (int i = -4; i <= 4; i++)
        {
            if (((y + i < 0) || (y + i >= this.size) || (x + i < 0) || (x + i >= this.size)) && !isTorus())
                continue;

            Piece p = field[Final02.mod(y + i, this.size)][Final02.mod(x + i, this.size)];
           
            if (p == null) // piece is null
            {
                cnt = 0x00;
                tmp = 0x00;
            }
            else if (tmp == 0x00) // not null and mask is zero
            {
                tmp = p.value();
                cnt = 0x01;
            }
            else // piece not null, mask not zero
            {
                tmp &= p.value();
                
                if (tmp == 0x00) // mask has become zero --> reset counter
                {
                    cnt = 0x01;
                    tmp = p.value();
                }
                else
                    cnt++; // increase counter
            }
            
            if (cnt >= 4)
                return true;
        }
        
        return false;
    }

    // check the second diagonal [ f(x) = x in a Cartesian coordinate system]
    private boolean getWinnerDiagonal1(int x, int y)
    {
        byte tmp = 0x00; // byte mask
        int cnt = 0x00; // internal counter

        for (int i = -4; i <= 4; i++)
        {
            if (((y + i < 0) || (y + i >= this.size) || (x - i < 0) || (x - i >= this.size)) && !isTorus())
                continue;

            Piece p = field[Final02.mod(y + i, this.size)][Final02.mod(x - i, this.size)];
           
            if (p == null) // piece is null
            {
                cnt = 0x00;
                tmp = 0x00;
            }
            else if (tmp == 0x00) // not null and mask is zero
            {
                tmp = p.value();
                cnt = 0x01;
            }
            else // piece not null, mask not zero
            {
                tmp &= p.value();
                
                if (tmp == 0x00) // mask has become zero --> reset counter
                {
                    cnt = 0x01;
                    tmp = p.value();
                }
                else
                    cnt++; // increase counter
            }
            
            if (cnt >= 4)
                return true;
        }
        
        return false;
    }

    
    /**
     * Returns the selected Piece
     * @return The selected Piece
     */
    public Piece getSelected()
    {
        return selected;
    }
    

    /**
     * Sets the selected Piece to the given value
     * @param selected New selected Piece
     */
    public void setSelected(Piece selected)
    {
        this.selected = selected;
    }

    /**
     * Returns the array containing all Pieces
     * @return The bag with all pieces
     */
    public Piece[] bag()
    {
        return bag;
    }

    
    /**
     * Sets the indicator, whether the current board is a torus, to the given new value 
     * @param b New torus value
     */
    public void setTorus(boolean b)
    {
        this.torus = b;
    }

}
