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
    private byte[] linearRobotAcclParam;
    private byte[] elevatorAcclParam;

   
    public Acceleration(byte commandAddress)
    {
        super(commandAddress);
        linearRobotAcclParam = null;
        elevatorAcclParam = null;
    }
    
    
     public byte[] getLinearRobotAcclParam()
    {
        return linearRobotAcclParam;
    }

    public void setLinearRobotAcclParam(byte[] linearRobotAcclParam)
    {
        this.linearRobotAcclParam = linearRobotAcclParam;
    }

    public byte[] getElevatorAcclParam()
    {
        return elevatorAcclParam;
    }

    public void setElevatorAcclParam(byte[] elevatorAcclParam)
    {
        this.elevatorAcclParam = elevatorAcclParam;
    }
}
