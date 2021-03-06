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
    private byte[] xSteps;
    private byte[] ySteps;
    private byte[] zSteps;
    private final static int byteSize = 2;

  
    
    public Calibrate(byte commandAddress)
    {
        super(commandAddress);
        xSteps = null;
        ySteps = null;
        zSteps = null;
        
    }
    
    
    
    
      public byte[] getxSteps()
    {
        return xSteps;
    }

    public void setxSteps(byte[] xSteps)
    {
        this.xSteps = xSteps;
    }

    public byte[] getySteps()
    {
        return ySteps;
    }

    public void setySteps(byte[] ySteps)
    {
        this.ySteps = ySteps;
    }

    public byte[] getzSteps()
    {
        return zSteps;
    }

    public void setzSteps(byte[] zSteps)
    {
        this.zSteps = zSteps;
    }
    
    
        public static int getByteSize()
    {
        return byteSize;
    }
    
    
    
}
