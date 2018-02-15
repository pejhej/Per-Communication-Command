package RoeBot;

import Commando.CommandoRegister;
import Commando.FromArduino.Busy;
import Commando.FromArduino.CalibParam;
import Commando.FromArduino.EMC;
import Commando.FromArduino.ElevatorLimitTrigg;
import Commando.FromArduino.EncoderOutOfRange;
import Commando.FromArduino.EncoderOutOfSync;
import Commando.FromArduino.FlagPos;
import Commando.FromArduino.LinearBotLimitTrigged;
import Commando.FromArduino.LowerSafetySwitch;
import Commando.FromArduino.ReadyToRecieve;
import Commando.FromArduino.UpperSafetySwitch;
import Commando.ToArduino.Acceleration;
import Commando.ToArduino.Calibrate;
import Commando.ToArduino.Light;
import Commando.ToArduino.Move;
import Commando.ToArduino.StateRequest;
import Commando.ToArduino.Suction;
import Commando.ToArduino.Velocity;
import Communication.Communication;
import GUI.HMI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import ImageProcessing.ImageProcessing;
import roerobot.RoeRobot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Main class, initiates the project. 
 * 
 * 
 * 
 * 
 * @author KristianAndreLilleindset
 * @ version 12-02-2018
 */
public class RoeBot 
{
    // creating variables for executor and number of threads
    private final int THREADS_IN_POOL = 10;
    private ScheduledExecutorService threadPool; 
    
    // creating variables for objects
    private Communication I2CCommunication; 
    private HMI humanMachineInterface; 
    private ImageProcessing imageProcessing;
    private RoeRobot roeRobot;
    private CommandoRegister cmdReg;
    
        /**ALL THE COMMAND ADDRESSES FOR THE DIFFERENT COMMANDS **/
    /*FROM THE JAVA/Communication PROGRAM */
   private static final byte MOVE = 0x01;  
   private static final byte SUCTION = 0x02;  
   private static final byte CALIBRATE = 0x10;  
   private static final byte LIGHT = 0x11;  
   private static final byte VELOCITY = 0x20;  
   private static final byte ACCELERATION = 0x21;  
   private static final byte GRIPPERCONTROL = 0x22;  
   private static final byte STATEREQUEST = 0x30;
   /*FROM THE ARDUINO/Communication TO THE JAVA PROGRAM*/
   private static final byte BUSY = 0x50;  
   private static final byte READY_TO_RECIEVE = 0x51;  
   private static final byte EMC = 0x60;  
   private static final byte UPPER_SAFETY_SWITCH = 0x61;  
   private static final byte LOWER_SAFETY_SWITCH = 0x62;  
   private static final byte ELEV_LIMIT_TRIGG = 0x63;  
   private static final byte LINEARBOT_LMIT_TRIGG = 0x64;
   private static final byte ENCODER_OUT_OF_SYNC = 0x65;  
   private static final byte ENCODER_OUT_OF_RANGE = 0x66;
   private static final byte CALIB_PARAM = 0x70;  
   private static final byte FLAG_POS = 0x71;  
 
   
   
   
   
   
   
   
    public static void main(String[] args)
    {
        RoeBot roeBot = new RoeBot();
        roeBot.initiate();
    }

    /**
     * Creating executor, objects and starting threads
     */
    private void initiate() 
    {
        this.threadPool = Executors.newScheduledThreadPool(THREADS_IN_POOL);
        
        this.cmdReg = new CommandoRegister();
        makeCommandos(cmdReg);
        
        this.I2CCommunication = new Communication();
        
        this.humanMachineInterface = new HMI();
        
        this.imageProcessing = new ImageProcessing();
        
        this.roeRobot = new RoeRobot();
        
    }
    
    /**
     * Make all the crazy commands for RoeTheBot
     * @param cmdReg CommandRegister to put them in
     */
    private void makeCommandos(CommandoRegister cmdReg)
    {
        cmdReg.addCommando(new Move(MOVE));
        cmdReg.addCommando(new Acceleration(ACCELERATION));
        cmdReg.addCommando(new Calibrate(CALIBRATE));
        cmdReg.addCommando(new Light(LIGHT));
        cmdReg.addCommando(new Suction(SUCTION));
        cmdReg.addCommando(new Velocity(VELOCITY));
        cmdReg.addCommando(new CalibParam(CALIB_PARAM));
        cmdReg.addCommando(new EMC(EMC));
        cmdReg.addCommando(new ElevatorLimitTrigg(ELEV_LIMIT_TRIGG));
        cmdReg.addCommando(new StateRequest(STATEREQUEST));
        cmdReg.addCommando(new EncoderOutOfRange(ENCODER_OUT_OF_RANGE));
        cmdReg.addCommando(new EncoderOutOfSync(ENCODER_OUT_OF_SYNC));
        cmdReg.addCommando(new FlagPos(FLAG_POS));
        cmdReg.addCommando(new LinearBotLimitTrigged(LINEARBOT_LMIT_TRIGG));
        cmdReg.addCommando(new LowerSafetySwitch(LOWER_SAFETY_SWITCH));
        cmdReg.addCommando(new Busy(BUSY));
        cmdReg.addCommando(new ReadyToRecieve(READY_TO_RECIEVE));
        cmdReg.addCommando(new UpperSafetySwitch(UPPER_SAFETY_SWITCH));
    }
}
