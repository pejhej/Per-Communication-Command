/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunicationOld;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a controller with I2C communication and connected 
 * sensors and/or actuators
 * @author PerEspen
 */
public class I2CController
{
    private static String name;
    private static byte address;
    private ArrayList<Sensor> sensors; 
    //The connected controller
    I2CDevice controller;
    
    public I2CController(String name, byte address, I2CBus bus)
    {
        this.name = name;
        this.address = address;
        sensors = new ArrayList<Sensor>();
        
        try
        {
            this.controller = bus.getDevice(address);
        } catch (IOException ex)
        {
            Logger.getLogger(I2CController.class.getName()).log(Level.SEVERE, null, ex);
            System.err.append("Exc. thrown when creating I2C Device in I2CController");
        }
    }
    
 
    public void testRun()
    {
        byte byt = 0b0000001;
        Sensor sens = getSensor(byt);
        
       System.out.println(this.getSensorState(byt));
       System.out.println("sens.getState()");
       System.out.println(sens.getState());
       System.out.println("sens.getStringAddress()");
       System.out.println(sens.getStringAddress());
       System.out.println("this.sensorValueChanged(sens, byt)");
       System.out.println(this.sensorValueChanged(sens, byt));
       sens.toggleState();
       System.out.println("sens.toggleState();");
        System.out.println("sens.getState()");
       System.out.println(sens.getState());
       System.out.println("this.sensorValueChanged(sens, byt)");
       System.out.println(this.sensorValueChanged(sens, byt));
       
       
       
    }
    
    
    private void testGetBit()
    {
        byte test = 0b1010101;
        boolean check = getBit(7, test);
        System.out.println("Testing: ");
        System.out.println(getBit(0, test));
        System.out.println(getBit(1, test));
        System.out.println(getBit(2, test));
        System.out.println(getBit(3, test));
        System.out.println(getBit(4, test));
        System.out.println(getBit(5, test));
        System.out.println(getBit(6, test)); 
    }
    
    
    /**
     * Read one byte from the given register address on the i2c device
     * @param address Register address to read from
     * @return Return one byte from the register address
     */
    public byte readRegisterByte(byte address)
    {
        byte returnByte = 0;
        try
        {
            returnByte = (byte) controller.read(address);
        } catch (IOException ex)
        {
            Logger.getLogger(I2CController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return returnByte;
    }
    
    
    /**
     * Reads from the given address with the given bytesize
     * @param registerAddr
     * @param byteSize
     * @return Returns the read byte from the device
     */
    public byte[] readRegisterBytes(byte registerAddr, int byteSize)
    {
        byte[] returnByte = null;
        try
        {
            returnByte = new byte[byteSize];
            int offset = 0;
            controller.read(registerAddr, returnByte, offset, byteSize); 
            
            
        } catch (IOException ex)
        {
            Logger.getLogger(I2CController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnByte;
    }
            
     /**
     * Device to write data to
     *
     * @param i2cdev i2c device to wrtie to
     * @param writeAddress address to write to
     * @param bufferSize buffer size
     * @return Returns NOT NULL if no errors were thrown during writing
     */
    public void writeData(int writeAddress, int bufferSize)
    {
        //Flag to check if writing was completed
        byte[] buffer = null;

        //Variable specifications for the i2c reading
        int offset = 0;
        //Does the writing to the i2c device, and sets the return statement
        try
        {
            controller.write(writeAddress, buffer, offset, bufferSize);
        } catch (IOException ex)
        {
            Logger.getLogger(I2Cdev.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

    }
    
    
      /**
     * Write to the i2c device with only one buffer
     *
     * @param deviceUsed
     * @param address
     * @param buffer
     */
    private void writeBuff(byte[] writebuff)
    {
        try
        {
            controller.write(writebuff);
        } catch (IOException ex)
        {
            Logger.getLogger(I2CController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Adds a new sensors to the list
     * @param address
     * @param type 
     */
    public void addSensor(byte address, String type)
    {
        Sensor newSensor = new Sensor(this.name, type, address);
        newSensor.setBitNr(sensors.size());
        sensors.add(newSensor);
    }
    
    /**
     * Removes a sensor from the Sensors list
     * @param address Address for sensor to be removed
     * @return Returns true if sensor was removed
     */
    public boolean removeSensor(byte address)
    {
        //Counter to check how many iterations has been done = 
        //what object to be removed from the list
        int cnt = 0;
        //Flag to check if removed or not
        boolean removed = false;
        //Get the sensor list iterator
        Iterator itr = getSensorItr();
        //Iterates through the list and checks for address on sensor
        while(itr.hasNext())
        {
            Sensor itrS = (Sensor) itr.next();
            if(itrS.getAddress() == address)
            {
                sensors.remove(cnt);
                removed = true;
            }
            
            ++cnt;
        }
        return removed;
    }
    
    /**
     * Return the sensor state, returns false also if no sensor was found
     * @param address Address for sensor to return state for
     * @return Returns state of sensor, or false if no sensor found
     */
    public boolean getSensorState(int address)
    {
        boolean state = false;

        //Get the sensor list iterator
        Iterator itr = getSensorItr();
        //Iterates through the list and checks for address on sensor
        while(itr.hasNext())
        {
            Sensor itrS = (Sensor) itr.next();
            if(itrS.getAddress() == address)
                state = itrS.getState();
        }
        return state;
    }
    
    /**
     * 
     * @param regAddress
     * @return 
     */
    public byte requestNewSensorData(byte regAddress)
    {
        //Reads byte from the i2c device
        byte sensorByte  = this.readRegisterByte(regAddress);
        //Iterator sensor
        Sensor sens;
        //Fields to keep track of sensors
        
        //Get sensor iterator
        Iterator itr = getSensorItr();
        while(itr.hasNext())
        {
            sens = (Sensor) itr.next();
            if(sensorValueChanged(sens, sensorByte))
            {
                //TODO: TOGGLE SENSORLISTENER
            } 
        }
        
        return sensorByte;
    }
    
    /**
     * Runs a check to see if sensor value has changed
     * @param sens Sensor to check against
     * @param sensorByte The sensorbyte with information incorporated
     * @return Returns true if changed and false if not!
     */
    private boolean sensorValueChanged(Sensor sens, byte sensorByte)
    {
        return (sens.getState() != getBit(sens.getBitNr(), sensorByte));
    }
    
    /**
     * Updates the value to the given sensor
     * @param sens
     * @param onOrOff
     * @param value 
     */
    private void updateSensorValue(Sensor sens, boolean onOrOff, int value)
    {
        if(onOrOff)
        {
            sens.toggleState();
        }
        else
            sens.setValue(value);
    }

    
    
    /**
     * Returns sensor iterator
     * @return Returns sensor iterator
     */
    public Iterator getSensorItr()
    {
        return sensors.iterator();
    }
    
    
     /**
     * Return the bit set in the position in byte ID. Shifts bit >> to position,
     * does AND bitwise check, returns 1 if set and 0 if not.
     * 0b1010101;
     * From right: true, false, true etc..
     * @param position Position to check bit at. Min=0, Max=7. From Right to Left
     * @param ID The byte to check bit from
     * @return Returns the bit set in the position of byte ID.
     */
    private boolean getBit(int position, byte ID)
    {
        boolean retBool = false;
        int check = ((ID >> position) & 1);
        if(check>0)
            retBool = true;
        
        return retBool;       
    }
    
    
    /**
     * Returns the address of the controller
     * @return Returns the address of the controller in b
     */
    public byte getAddress()
    {
        return this.address;
    }
    
    
    private Sensor getSensor(byte sensAddress)
    {
        boolean found = false;
        Sensor sens = null;
        Iterator itr = getSensorItr();
        while(itr.hasNext()&&!found)
        {
            sens = (Sensor) itr.next();
            if(sens.getAddress() == sensAddress)
                found = true;
        }
       
        return sens;
    }
}
