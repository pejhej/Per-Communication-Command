/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import Commando.Commando;
import Commando.CommandoRegister;
import Commando.FromArduino.ReadyToRecieve;
import Commando.ToArduino.Acceleration;
import Commando.ToArduino.Calibrate;
import Commando.ToArduino.Move;
import Commando.ToArduino.StateRequest;
import Commando.ToArduino.Suction;
import Commando.ToArduino.Velocity;
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
 * This communication class holds the respectively i2c devices used for the i2c
 * communication. Sending and recieving via i2c should be done via this class.
 * It works as an information relay. Also updating and checking commandos.
 * Recieve commando and send the appropriate data to the respective controllers.
 *
 * @author PerEspen
 */
public class Communication implements Runnable
{

    //i2c-dev bus used
    private static final int I2CbusNr = 4;
    private static final byte CONTROLLER_ADDR_ELEVATOR = 0x01;
    private static final byte CONTROLLER_ADDR_LINEARBOT = 0x02;

    //Commando reg
    CommandoRegister cmdReg;

    //I2C Bus
    I2CBus i2cbus;
    //Controllers
    I2CDevice linearRobot;
    I2CDevice elevatorRobot;

    public void recieveCommando(Commando cmd)
    {
        /**
         * COMMANDS TO ARDUINO*
         */
        //Check for move command
        if (cmd instanceof Move)
        {
            doMove(cmd);
        } //Check for acceleration command
        else if (cmd instanceof Acceleration)
        {
            //Do the X-Y movement first and send to the controller
            Acceleration cmdAccl = (Acceleration) cmd;
            if (!(cmdAccl.getElevatorAcclParam().equals(null)))
            {
                this.writeByteToAddr(linearRobot, cmdAccl.getElevatorAcclParam(), cmd.getCmdAddr());
            }
            if (!(cmdAccl.getLinearRobotAcclParam().equals(null)))
            {
                this.writeByteToAddr(linearRobot, cmdAccl.getLinearRobotAcclParam(), cmd.getCmdAddr());
            }
        } //Check for calibrate command and do the tasks   
        else if (cmd instanceof Calibrate)
        {
            doCalibrate(cmd);
        } //Check for suction command
        else if (cmd instanceof Suction)
        {
            //Do the X-Y movement first and send to the controller
            Suction cmdSuction = (Suction) cmd;
            this.writeBytes(linearRobot, cmd.getValue());
            this.writeBytes(elevatorRobot, cmd.getValue());
        } //Check for velocity command
        else if (cmd instanceof Velocity)
        {
            //Do the X-Y movement first and send to the controller
            Suction cmdSuction = (Suction) cmd;
            this.writeBytes(linearRobot, cmd.getValue());
            this.writeBytes(elevatorRobot, cmd.getValue());
        }

        /**
         * COMMANDS "FROM" ARDUINO*
         */
        //Check for move command
        if (cmd instanceof StateRequest)
        {   
            //Storing of bytes
            byte[] returnByteLinear = null;
            byte[] returnByteElevator = null;
            StateRequest cmdStqry = (StateRequest) cmd;
            //Check if StateRequest is for elevator robot
            if (cmdStqry.forElevatorRobot())
                returnByteElevator = readByteFromAddr(elevatorRobot, cmd.getCmdAddr(), 1);
            
            //Check if StateRequest is for linear robot
            if (cmdStqry.forLinearRobot())
                returnByteLinear = readByteFromAddr(linearRobot, cmd.getCmdAddr(), 1);

      
            //Find the retrievend command and preform the State Update
            updateState(cmdReg.findCommand(linearRobot , returnByteElevator[0]));
            updateState(cmdReg.findCommand(elevatorRobot, returnByteLinear[0]));
            
            //Reset the state request
            ((StateRequest) cmd).reset();
        }

    }

    /**
     * Do the move command as specified
     *
     * @param cmd The command with attached values etc
     */
    public void doMove(Commando cmd)
    {
        //Do the X-Y movement first and send to the controller
        Move cmdMove = (Move) cmd;
        //Combine the xyByte from the cmd move
        byte[] xyByte = new byte[cmdMove.getxValue().length + cmdMove.getyValue().length];
        System.arraycopy(cmdMove.getxValue(), 0, xyByte, 0, cmdMove.getxValue().length);
        System.arraycopy(cmdMove.getyValue(), 0, xyByte, cmdMove.getxValue().length, cmdMove.getxValue().length);
        //Write the respective x-y-z values to the respective controllers
        writeByteToAddr(linearRobot, xyByte, cmd.getCmdAddr());
        writeByteToAddr(elevatorRobot, cmdMove.getzValue(), cmd.getCmdAddr());
    }

    public void doCalibrate(Commando cmd)
    {
        //Do the X-Y movement first and send to the controller
        Calibrate cmdCali = (Calibrate) cmd;
        int returnByteSize = 4;
        //Send the command register address and get the byte in return
        byte[] linearByteCalib = readByteFromAddr(linearRobot, cmd.getCmdAddr(), returnByteSize);
        byte[] elevatorByteCalib = readByteFromAddr(elevatorRobot, cmd.getCmdAddr(), returnByteSize / 2);
        //To divide the return byte from the linear robot
        byte[] xSteps = new byte[returnByteSize / 2];
        byte[] ySteps = new byte[returnByteSize / 2];

        //Copy and set the appropriate return byte to the command object
        System.arraycopy(linearByteCalib, 0, xSteps, 0, returnByteSize / 2);
        System.arraycopy(linearByteCalib, returnByteSize / 2, ySteps, 0, returnByteSize / 2);
        //Set the values
        cmdCali.setxSteps(xSteps);
        cmdCali.setySteps(ySteps);

        cmdCali.setzSteps(elevatorByteCalib);

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
                Logger.getLogger(CommunicationOld2.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            // get the I2C bus to communicate on
            i2cbus = I2CFactory.getInstance(I2CbusNr);
            elevatorRobot = i2cbus.getDevice(CONTROLLER_ADDR_ELEVATOR);
            linearRobot = i2cbus.getDevice(CONTROLLER_ADDR_LINEARBOT);

        } catch (I2CFactory.UnsupportedBusNumberException ex)
        {
            Logger.getLogger(CommunicationOld2.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex)
        {
            Logger.getLogger(CommunicationOld2.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Read the incomming message from the device
     *
     * @param device The device to read from
     * @return Return the incomming byte from the
     */
    private byte readByte(I2CDevice device)
    {
        return readByte(device);
    }

    /**
     * Read from the given register address and buffer the answer in the byte[]
     *
     * @param device Device to read from
     * @param address The register address specified
     * @param byteSize Size of the buffer byte
     * @return Returns a read buffer from the given i2cdevice with given
     * bytesize
     */
    private byte[] readByteFromAddr(I2CDevice device, byte address, int byteSize)
    {
        byte[] returnByte = new byte[byteSize];
        int offset = 0;
        try
        {
            device.read(address, returnByte, offset, byteSize);

        } catch (IOException ex)
        {
            Logger.getLogger(Communication.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return returnByte;
    }

    /**
     * Write a byte to the given i2c device in the param, does not carry a
     * register address to be read first
     *
     * @param device The device to wrtie byte to
     * @param sendByte The byte to be sent
     */
    private void writeByte(I2CDevice device, byte sendByte)
    {
        try
        {
            device.write(sendByte);

        } catch (IOException ex)
        {
            Logger.getLogger(Communication.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.out.println("Communication.Communication.writeByte(): WRITE GAVE IO-EXCEPTION");
        }
    }

    /**
     * Write a byte[] to the given i2c device in the param, does not carry a
     * register address to be read first
     *
     * @param device The device to wrtie byte to
     * @param sendByte The byte[] to be sent
     */
    private void writeBytes(I2CDevice device, byte[] sendByte)
    {
        try
        {
            device.write(sendByte);

        } catch (IOException ex)
        {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Communication.Communication.writeByte(): WRITE GAVE IO-EXCEPTION");
        }
    }
    /**
     * Write byte[] to the specified device with the specified cmd.
     * 
     * @param device The I2CDevice to write to
     * @param sendByte The byte[] to send to respective i2c device
     * @param sendAddress The register address for the sent byte[]
     */
    private void writeByteToAddr(I2CDevice device, byte[] sendByte, byte sendAddress)
    {
        try
        {
            device.write(sendAddress, sendByte);

        } catch (IOException ex)
        {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Communication.Communication.writeByteToAddr(): WRITE GAVE IO-EXCEPTION");
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

    /**
     * Returns true if the state of the device is equal to ReadyToRecieve
     * command
     *
     * @param linearRobot The i2c device to check state of
     * @return Returns true if the state of the device is equal to
     * ReadyToRecieve command
     */
    private boolean readyState(I2CDevice device)
    {
        boolean ready = false;
        //Read byte from the device
        byte stateByte = readByte(device);
        //Find the commando
        Commando findCmd = cmdReg.findCommand(stateByte);
        if (findCmd instanceof ReadyToRecieve)
        {
            ready = true;
        }

        return ready;
    }

    
    /**
     * Set the State true for the respective device in param
     * @param device Device to set state for
     * @param cmd The state
     */
    private void updateState(I2CDevice device, Commando cmd)
    {
        if(device.equals(linearRobot))
            cmd.setLinearRobot(true);
        
        if(device.equals(elevatorRobot))
            cmd.setElevatorRobot(true);
    }

    /*
    
    WAITING FOR READY TO RECIEVE
        //Keep sending coordinates until they give OK recieved message back
        while (!linearBotOk || !elevatorBotOk)
        {
            //Check the linear and elevator bot are ok
            if (!linearBotOk)
            {
                linearBotOk = readyState(linearRobot);
            }
            if (!elevatorBotOk)
            {
                elevatorBotOk = readyState(elevatorRobot);
            }
        }

        //Send the X-Y Movement
        if (linearBotOk)
        {
            writeByteToAddr(linearRobot, xyByte, cmd.getCmdAddr());
        }

        ///Send the Z movement
        if (elevatorBotOk)

        {
            writeByteToAddr(elevatorRobot, cmdMove.getzValue(), cmd.getCmdAddr());
        }

    
    
    
    
     */
}
