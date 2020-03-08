/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdo_boss_timer;

import java.awt.AWTException;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Hate
 */
public class BDO_Boss_Timer {
    /**
     * @param args the command line arguments
     * @throws java.awt.AWTException
     * @throws java.text.ParseException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws AWTException, ParseException, IOException, InterruptedException {
        try{
            if(args[0].equalsIgnoreCase("autorun")) Thread.sleep(20000);
        }catch(ArrayIndexOutOfBoundsException ex){
            System.out.println("Standart mode");
        }
        TrayMode tm = new TrayMode();
    }
}
