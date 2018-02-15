/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import Commando.Commando;
import Commando.ToArduino.Move;
import CommunicationOld.CommunicationOld2;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PerEspen
 */
public class Communication implements Runnable
{

    private static final int I2CbusNr = 4;
    private static final byte CONTROLLER_ADDR_ELEVATOR = 0x01;
    private static final byte CONTROLLER_ADDR_LINEARBOT = 0x02;
    //I2C Bus
    I2CBus i2cbus;
    //Controllers
    I2CDevice linearRobot;
    I2CDevice elevatorRobot;

    public void recieveCommando(Commando cmd)
    {

        if (cmd instanceof Move)

        {
            
        }
    }


    public void doMove(Move cmd)
    {
     

    }

    /**
     * Sets up the I2C bus with platform and initiates the connection
     */
    private void initiate()
    {
        try
        {
            try
            {
                PlatformManager.setPlatform(Platform.ODROID);
            } catch (PlatformAlreadyAssignedException ex)
            {
                Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
            }
            // get the I2C bus to communicate on
            i2cbus = I2CFactory.getInstance(I2CbusNr);
            elevatorRobot = i2cbus.getDevice(CONTROLLER_ADDR_ELEVATOR);
            linearRobot = i2cbus.getDevice(CONTROLLER_ADDR_LINEARBOT);
        } catch (I2CFactory.UnsupportedBusNumberException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        }

        

    }

    private void testCommando()
    {
        byte b = 0b00000001;
        byte b2 = 100;
        Commando comm = new Commando(b);
        int i = 15;
        System.out.println("Setting int value");
        comm.setIntValue(i);
        System.out.println(comm.getIntValue());
        System.out.println("Setting byte value");
        comm.setValue(b2);
        System.out.println(Byte.toString(comm.getByteValue(0)));

    }

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
