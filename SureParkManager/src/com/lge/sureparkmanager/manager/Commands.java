package com.lge.sureparkmanager.manager;

public final class Commands {
    public static final int CMD_REQ = 0;
    public static final int CMD_RES = 1;

    public static final int CMD_DEVICE_INFO = 0;
    public static final int CMD_SERVER_CONFIG = 1;
    public static final int CMD_HEART_BIT = 2;

    public static final int CMD_ENTRY_SENSOR = 3;
    public static final int CMD_ENTRY_SENSOR_DET = 0;
    public static final int CMD_ENTRY_SENSOR_NOTDET = 1;

    public static final int CMD_CONFIRMATION = 4;

    public static final int CMD_ENTRY_GATE = 5;
    public static final int CMD_ENTRY_GATE_OPEN = 0;
    public static final int CMD_ENTRY_GATE_CLOSE = 1;

    public static final int CMD_PARKING = 6;
    public static final int CMD_PARKING_IN = 0;
    public static final int CMD_PARKING_OUT = 1;

	public static final int CMD_EXIT_GATE = 7;
	public static final int CMD_EXIT_GATE_OPEN = 0;
    public static final int CMD_EXIT_GATE_CLOSE = 1;
}


/*
 *  cmd value   P1(string)  P2  P3
Request 0           
Response    1           
                
Device Info     0   MacAddr Parking place   
Server config   1   TBD     
HeartBeat       2   Time info       
Entry sensor    3   Det(0)/NotDet(1)        
Confirmation    4   TBD     
Entry gate      5   open(0)/close(1)        
Parking         6   In(1)/Out(0)    ParkingPlace    charging time
Exit gate       7   open(0)/close(1)        
                
                
0_5_0   request open entry gate         
1_5_0   server response         
 */