/*
 * COPYRIGHT (C) 2016, UNKNOWN6656
 */

package edu.kit.informatik;

/**
 * Utility class for the final task 02
 * @author Unknown6656
 * @version 1
 */
public final class Final02
{
    /**
     * The underlying game board
     */
    private static GameBoard game;
    private static boolean finished;
    private static int round;
    
    /**
     * static default constructor
     */
    static
    {
        game = new GameBoard(6, false);
        game.setPlayer((byte) 0); // set to the first player
    }
    
    /**
     * private default constructor
     */
    private Final02()
    {
    }

    /**
     * The application's entry point
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            String arg = args[0].toLowerCase().trim();
            
            if (arg.equals("torus"))
                game.setTorus(true);
            else if (!arg.equals("standard"))
            {
                out("Error, Expected `torus` or `standard` as first argument.");
                
                System.exit(1);
                return;
            }

            mainLoop();
        }
       
        out("Error, At least one argument expected.");
        
        System.exit(1);
    }

    // the application's main loop
    private static void mainLoop()
    {
        round = 0;
        finished = false;
        game.setPlayer((byte) 1);
        
        while (true)
        {
            String command = Terminal.readLine().trim();
            String arguments = command.contains(" ") ? command.substring(command.indexOf(' ')).trim() : "";
            
            if (command.contains(" "))
                command = command.substring(0, command.indexOf(' ') + 1).trim();
            
            try
            {
                switch (command.toLowerCase())
                {
                    case "select":
                        internalSELECTCommand(arguments);
                        
                        break;
                    case "place":
                        internalPLACECommand(arguments);
                        
                        break;
                    case "bag":
                        StringBuilder sb = new StringBuilder();

                        for (Piece p : game.getBag())
                            sb.append(p.value())
                              .append(" ");
                        
                        out(sb.toString().trim());
                        
                        break;
                    case "rowprint":
                        if (checkArgumentsCount(arguments, 1))
                            out(game.printRow(Integer.parseInt(arguments)));
                        
                        break;
                    case "colprint":
                        if (checkArgumentsCount(arguments, 1))
                            out(game.printColumn(Integer.parseInt(arguments)));

                        break;
                    case "print": // only for debugging
                        out(game.print());
                        
                        break;
                    case "exit":
                    case "quit":
                        System.exit(0);
                        
                        return;
                    default:
                        out("Error, The command '%s' is unknown or not registered.", command);
                }
            }
            catch (NumberFormatException ex)
            {
                out("Error, At least one argument/command could not be parsed as an integer value.");
            }
            catch (NullPointerException ex)
            {
                out("Error, Some internal error occured. Pray to god/jesus or retry the operation an other time.");
            }
            catch (StackOverflowError ex)
            {
                out("Error, Some internal error occured. Pray to god/jesus or retry the operation an other time.");
            }
        }
    }

    // Fired, when the command `SELECT` is used
    private static void internalSELECTCommand(String arguments)
    {
        if (finished)
            out("Error, Invalid operation `select`: The game has already been finished.");
        else if (checkArgumentsCount(arguments, 1))
            if (game.getSelected() == null)
            {
                byte num = Byte.parseByte(arguments);
                
                if ((num < 0x00) || (num > 0x0f))
                    out("Error, The game piece number must be a positive integer value between "
                      + "0 and 15 [incl.]");
                else if (!game.bag()[num].isUsed())
                {
                    game.setSelected(game.bag()[num]);
                    game.getSelected().setUsed(true);
                    
                    out("OK");
                }
                else
                    out("Error, The game piece number %d has already been used.", num);
            }
            else
                out("Error, The `select`-command is invalid, as the selection is currently locked.");
    }

    // Fired, when the command `PLACE` is used
    private static void internalPLACECommand(String arguments)
    {
        if (finished)
            out("Error, Invalid operation `place`: The game has already been finished.");
        else if (checkArgumentsCount(arguments, 2))
            if (game.getSelected() != null)
            {
                String[] coord = arguments.split("\\;");
                int x = Integer.parseInt(coord[1]);
                int y = Integer.parseInt(coord[0]);
                
                switch (game.placePiece(game.getSelected().value(), x, y))
                {
                    case 1: out("Error, Invalid coordinates (%d|%d): The field might is outside "
                              + "the game board dimensions.", x, y);
                        break;
                    case 2: out("Error, The piece in question is invalid.");
                        break;
                    case 3: out("Error, Invalid coordinates (%d|%d): The field is already in use.", x, y);
                        break;
                    default:
                        if (game.getWinner(x, y))
                        {
                            out("P%d wins\n%d", game.getPlayer() + 1, round);
                            
                            finished = true;
                        }
                        else if (game.getBag().size() > 0)
                        {
                            game.setSelected(null);
                            game.nextPlayer();
                            
                            round++;
                            
                            out("OK");
                        }
                        else
                            out("draw");
                }
            }
            else
                out("Error, The `place`-command is invalid, as no piece is currently selected.");
    }
    
    // checks, whether the given argument string contains enough arguments
    private static boolean checkArgumentsCount(String arg, int count)
    {
        if ((count == 1) && (arg.length() > 0))
            return true;
        else if (arg.split("\\;").length < count)
        {
            out("Error, Not enough arguments: At least %d arguments separated by a semicolon are required.", count);
            return false;
        }
        
        return true;
    }
    
    /**
     * redirects the output stream
     * @param msg String to be written into the output stream
     */
    protected static void out(String msg)
    {
        out("%s", msg);
    }

    /**
     * redirects the output stream and uses a format string
     * @param msg Format string to be written into the output stream
     * @param args Format parameters
     */
    protected static void out(String msg, Object... args)
    {
        Terminal.printLine(String.format(msg, args));
    }

    /**
     * Overrides the standard Java `%`-operator, as `%` does return the remainder in Java - not the modulus :(
     * @param num Number 
     * @param div Divisor
     * @return Modulus
     */
    protected static int mod(int num, int div)
    {
        if (num >= 0)
            return num % div;
        else
            return div - ((-num) % div);
    }
}
