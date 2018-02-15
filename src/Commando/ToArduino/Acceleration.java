/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commando.ToArduino;

import Commando.Commando;

/**
 * Command to update Acceleration parameter for Arduino via I2C BUS
 * @author PerEspen
 */
public class Acceleration extends Commando
{
    
    public Acceleration(byte commandAddress)
    {
        super(commandAddress);
    }
    
}
