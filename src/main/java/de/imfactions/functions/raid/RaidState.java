package de.imfactions.functions.raid;

public enum RaidState {

    PREPARING,
    SCOUTING,
    RAIDING,
    DONE;

    public static RaidState getStateFromString(String raidState){
        switch (raidState){
            case "PREPARING" :
                return PREPARING;
            case "SCOUTING" :
                return SCOUTING;
            case "RAIDING" :
                return RAIDING;
            case "DONE" :
                return DONE;
            default:
                return null;
        }
    }

    public static String getStringFromState(RaidState raidState){
        switch (raidState){
            case PREPARING:
                return "PREPARING";
            case SCOUTING :
                return "SCOUTING";
            case RAIDING :
                return "RAIDING";
            case DONE :
                return "DONE";
            default:
                return null;
        }
    }
}
