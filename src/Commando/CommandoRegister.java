/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commando;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class manages,holds and does alerts for all the available for the RoeProject program.
 * Also creates and holds all the specified and different command objects required
 * 
 * @author PerEspen
 */
public class CommandoRegister
{

  
   ArrayList<Commando> commands;
   
   
   public CommandoRegister()
   {
    commands = new ArrayList<Commando>();
   }
   
   /**
    * Adds a commando to the commands list
    * @param cmd Commando to add
    */
   public void addCommando(Commando cmd)
   {
    commands.add(cmd);
   }
   
   
   public Iterator getIterator()
   {
       return this.commands.iterator();
   }
   
   public Commando findCommand(Commando cmd)
   {
       int index = commands.indexOf(cmd);
       return this.commands.get(index);
   }
   
   /**
    * Find the command based on the given command address
    * @param cmdAddr Command address to look for in command register list
    * @return Returns command if found, null if not
    */
   public Commando findCommand(byte cmdAddr)
   {
       //Found flag to exit loop and set value of Command return obj
       boolean found = false;
       Iterator itr = this.getIterator();
       Commando findCmd = null;
       //Loop until found or reached end of arraylist
       while(itr.hasNext()&& !found)
       {
           findCmd = (Commando) itr.next();
            //Compares the command address to check for and the iterator element
           if(compareByte(findCmd.getCmdAddr(), cmdAddr))
               found = true;
       }
       //Set to null if command wasnt found
       if(!found)
           findCmd = null;
       
       return findCmd;
   }
   
   /**
    * Compare two bytes if they are equal or not
    * @param x One of bytes to compare
    * @param y Other byte to compare
    * @return Return true if found or false if not
    */
   private boolean compareByte(byte x, byte y)
   {
       boolean returnBool = false;
       int comp = Byte.compare(x, y);
       if(comp>0)
           returnBool = true;
       
       return returnBool;
   }
   
   
}
