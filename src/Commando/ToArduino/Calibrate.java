/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commando.ToArduino;

import Commando.Commando;

/**
 * Command for the I2C device's to do a calibration
 * @author PerEspen
 */
public class Calibrate extends Commando
{
    
    public Calibrate(byte commandAddress)
    {
        super(commandAddress);
    }
    
}
