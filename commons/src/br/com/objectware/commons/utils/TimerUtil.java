/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.util.concurrent.TimeUnit;

/**
 * Some useful methods for time manipulation/visualization
 * 
 * @author Luciano M. Christofoletti
 * @since 10/Jun/2015
 */
public class TimerUtil {
    
    /**
     * Suspend program execution for the given milliseconds interval.
     * @param millis the interval
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // nothing to do ;P
        }
    }
    
    /**
     * Return a text string showing the given interval in seconds or milliseconds.
     * @param interval
     * @return text string
     */
    public static String getIntervalString(long interval) {
        if(interval < 1000) {
            return String.format("%d milliseconds", interval);
        } else {
            return String.format("%d seconds", TimeUnit.MILLISECONDS.toSeconds(interval));
        }
    }
}
