/*
 * COPYRIGHT (C) 2016, UNKNOWN6656
 */

package edu.kit.informatik;

/**
 * Represents a game piece/token
 * @author Unknown6656
 * @version 1
 */
public class Piece
{
    /*         inner  -----+--- 0:=empty -- 1:=massive
     *         size   ----+|--- 0:=small -- 1:=large
     *         shape  ---+||--- 0:=cubic -- 1:=round
     *         colour --+|||--- 0:=black -- 1:=white
     *                  ||||
     * BYTE VALUE: 0000.XXXX
     */
    private byte val;
    private boolean used;
    
    
    /**
     * Creates a new game piece using the given properties
     * @param val Game piece properties
     */
    public Piece(byte val)
    {
        super();
        
        this.val = (byte) (val & 0x0f);
    }
    
    /**
     * Returns the underlying byte value, which represents the piece's properties
     * @return Byte value
     */
    public byte value()
    {
        return this.val;
    }

    /**
     * Sets the underlying byte value, which represents the piece's properties, to the given value
     * @param val New byte value
     */
    public void value(byte val)
    {
        this.val = (byte) (val & 0x0f);
    }

    /**
     * Returns, whether the current piece is white
     * @return Boolean colour flag
     */
    public boolean isWhite()
    {
        return (this.val & 0x08) != 0x00; 
    }

    /**
     * Returns, whether the current piece is round
     * @return Boolean shape flag
     */
    public boolean isRound()
    {
        return (this.val & 0x04) != 0x00;
    }

    /**
     * Returns, whether the current piece is large
     * @return Boolean size flag
     */
    public boolean isLarge()
    {
        return (this.val & 0x02) != 0x00;
    }

    /**
     * Returns, whether the current piece is massive
     * @return Boolean  flag
     */
    public boolean isMassive()
    {
        return (this.val & 0x01) != 0x00; 
    }

    
    /**
     * Returns, whether the Piece has been used or not
     * @return Used
     */
    public boolean isUsed()
    {
        return used;
    }

    
    /**
     * Sets, whether the Piece has been used or not
     * @param used New used value
     */
    public void setUsed(boolean used)
    {
        this.used = used;
    }
}
