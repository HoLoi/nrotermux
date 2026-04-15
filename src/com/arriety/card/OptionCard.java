package com.arriety.card;

/**
 *
 * @author Dev Duy Be B Beo Bé Beo Be Beo B Beo Be Beo Béo Beo Be Beo 
 */
public class OptionCard {
    public int id; 
    public int param;
    public byte active;
    
    public OptionCard(int i , int p , byte a){
        id = i;
        param = p;
        active = a;
    }
    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "id" + n + ":" + n + id + n + ","
                + n + "active" + n + ":" + n + active + n + ","
                + n + "param" + n + ":" + n + param + n
                + "}";
    }
}
