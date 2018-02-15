/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunicationOld;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PerEspen
 */
public class CommunicationOld2 implements Runnable
{

    //Variable holding the threadpool
    private ScheduledExecutorService threadpool;

    /**
     * *** I2C Address variables ****
     */
    //Elevator controller(-C1)
    private static final byte CONTROLLER_ADDR_C1 = 0x01; //Elevator
    private static final byte SENSORDATA_C1_ADDR = 0x10;
    private static final byte SENSORS_ATTACHED_C1 = 6;
    private static final byte SENSORDATA_S03_1 = 0b0000001;  //Optical Sensor S3.1
    private static final byte SENSORDATA_S03_2 = 0b0000010;  //Optical Sensor S3.2
    private static final byte SENSORDATA_S04_1 = 0b0000100;  //Optical Sensor S4.1
    private static final byte SENSORDATA_S04_2 = 0b0001000;  //Optical Sensor S4.2
    private static final byte SENSORDATA_S05_1 = 0b0010000;  //Optical Sensor S5.1
    private static final byte SENSORDATA_S05_2 = 0b0100000;  //Optical Sensor S5.2
    
    
    
    //Linear robot controller(-C2)
    private static final byte CONTROLLER_ADDR_C2 = 0x02; //RoeRobot
    private static final byte SENSORDATA_C2_ADDR = 0x11;
    private static final byte SENSORS_ATTACHED_C2 = 4;
    private static final byte SENSORDATA_S06_1 = 0b00000001;  //Endstop Sensor S6.1
    private static final byte SENSORDATA_S06_2 = 0b00000010;  //Endstop Sensor S6.2
    private static final byte SENSORDATA_S07_1 = 0b00000100;  //Endstop Sensor S7.1
    private static final byte SENSORDATA_S07_2 = 0b00001000;  //Endstop Sensor S7.2
    
    //AXIS REGISTER
    private static final byte X_AXIS_R_REG = 0x20; //RoeRobot
    private static final byte X_AXIS_W_REG = 0x30; //RoeRobot
    private static final byte Y_AXIS_R_REG = 0x21;  //Roerobot
    private static final byte Y_AXIS_W_REG = 0x31;   //RoeRobot
    private static final byte Z_AXIS_R_REG = 0x22;   //RoeRobot
    private static final byte Z_AXIS_W_REG = 0x32;   //RoeRobot
    
    //
    private static final byte ELEVATOR_BREAK_W_REG = 0x32;   //RoeRobot
    
    private static final byte START_SUCTION = 0x32;   //RoeRobot
    private static final byte STOP_SUCTION = 0x32;   //RoeRobot
 
    


    private byte[] stdByte;

    private static final int stdByteSize = 3;
    //Bus nr for the I2C to be connected at(**I2C-dev4**)
    private static final int I2CbusNr = 4;

    //I2C Bus
    I2CBus i2cbus;
    
    //Controllers
    I2CController c1;
    I2CController c2;
    
    public CommunicationOld2()
    {
        stdByte = new byte[4];
        initiate();
        //Creating the controllers
        c1 = new I2CController("Elevator", CONTROLLER_ADDR_C1, i2cbus);
        //c2 = new I2CController("Robot", CONTROLLER_ADDR_C2, i2cbus);
        fillSensors(c1);
        
    }
    
 
    public void run()
    {
        
        System.out.println(c1.getSensorState(SENSORDATA_S03_1));
        c1.testRun();
        
        
    }
    
    
    public void moveElevator()
    {
        
    }
    
    public void moveRobot()
    {
        
    }
    
    private void setElevatorBreak()
    {
        
    }
    /**
     * Read incomming message from the device. Incomming information(bytes) gets placed in the BUFFER parameter.
     *
     * @param deviceUsed Device to read from
     * @param address Address
     * @param buffer Buffer to save info in
     * @param registerAddr This param gets written to the
     * @return Return the created buffer
     */
    private byte[] read(I2CDevice deviceUsed, byte[] buffer, int byteSize, byte registerAddr)
    {
        
        try
        {
        deviceUsed.read(registerAddr, buffer, 0, byteSize);
        } catch (IOException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;
    }
    
    /**
     * Write to the i2c device
     *
     * @param deviceUsed
     * @param address
     * @param buffer
     */
    private void write(I2CDevice deviceUsed, byte[] writebuff)
    {
        try
        {
            //deviceUsed.write(address, buffer, 0, size);
            deviceUsed.write(writebuff);
        } catch (IOException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    private byte[] createBufferRegister(int byteSize, byte registerAddr, int valueX, int valueY)
    {
        //Create the byte
        byte[] newByte = new byte[byteSize];
        //ADD Values to the byte, registerAddrs is always one byte.
        newByte[0] = registerAddr;
        // newByte[0] = {registerAddr};
        
        if(byteSize > 1)
               newByte[1] = (byte) valueX;
        
        if(byteSize > 2)
            newByte[2] = (byte) valueY;
        
        //ByteBuffer b = ByteBuffer.allocate(byteSize);
        //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        //b.putInt(registerAddr);

        //byte[] result = b.array();
        //System.out.println(result);
       // System.out.println(Byte.toUnsignedInt(newByte[0]));
       // System.out.println(Byte.toUnsignedInt(newByte[1]));

        return newByte;
    }


    
    /**
     *  Uses the device, and sends request sensordata regarding what device is 
     *  used to request the data.
     * @param device
     * @return returns 
     */
    private void requestSensorData(I2CController controller)
    {
        
       //Check what controller is used 
       if(controller.getAddress() == CONTROLLER_ADDR_C1)
       {
           controller.requestNewSensorData(SENSORDATA_C1_ADDR);
       }
       else if(controller.getAddress() == CONTROLLER_ADDR_C2)
       {
           controller.requestNewSensorData(SENSORDATA_C2_ADDR);
       }

    }
    
    /**
     * Returns an array of all the sensors that are supposed to be attached to the controller given in the parameter.
     * @param controller Address of the controller of which to make Array for. 
     */
    private void fillSensors(I2CController controller)
    {
  
        int cnt = 0; //Counter
        //Check which controller is attached to these sensors
        if(controller.getAddress() == CONTROLLER_ADDR_C1)
        {
            //Add all the sensors
            controller.addSensor(SENSORDATA_S03_1, "Optical(S03_1)");
            controller.addSensor(SENSORDATA_S03_2, "Optical(S03_2)");
            controller.addSensor(SENSORDATA_S04_1, "Optical(S04_1)");
            controller.addSensor(SENSORDATA_S04_2, "Optical(S04_2)");
            controller.addSensor(SENSORDATA_S05_1, "Optical(S05_1)");
            controller.addSensor(SENSORDATA_S05_2, "Optical(S05_2)");
            
        }
        else if(controller.getAddress() == CONTROLLER_ADDR_C2)
        {
            //Add all the sensors
            controller.addSensor(SENSORDATA_S06_1, "Endstop(S06_1)");
            controller.addSensor(SENSORDATA_S06_2, "Endstop(S06_2)");
            controller.addSensor(SENSORDATA_S07_1, "Endstop(S07_1)");
            controller.addSensor(SENSORDATA_S07_2, "Optical(S07_2)");
        }    
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
        } catch (I2CFactory.UnsupportedBusNumberException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(CommunicationOld2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
