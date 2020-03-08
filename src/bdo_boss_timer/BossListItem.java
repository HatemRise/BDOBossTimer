/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdo_boss_timer;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Hate
 */
public class BossListItem {
    private String name;
    private int hour;
    private int minute;
    private int day;

    public BossListItem() {
    }
    
    public BossListItem(String name, String day) {
        this.name = name.split(" ")[1];
        this.hour = 0;
        plusHour(Integer.parseInt(name.split(" ")[0].split(":")[0]));
        this.minute = 0;
        plusMinute(Integer.parseInt(name.split(" ")[0].split(":")[1]));
        setDay(day);
    }

    public BossListItem(String name, int hour, int minute, String day) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        setDay(day);
    }

    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getDay() {
        switch (day){
            case 1: 
                return "Пн";
            case 2: 
                return "Вт";
            case 3: 
                return "Ср";
            case 4: 
                return "Чт";
            case 5: 
                return "Пт";
            case 6: 
                return "Сб";
            case 7: 
                return "Вс";
            default:
                day -= 7;
                return getDay();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHour(int hour) {
        while(hour >= 24){
            hour -= 24;
            plusDay(1);
        }
        this.hour = hour;
    }

    public void setMinute(int minute) {
        while(minute >= 60){
            minute -= 60;
            plusHour(1);
        }
        this.minute = minute;
    }

    public void setDay(String day) {
        switch (day){
            case "Пн": 
                this.day = 1;
                break;
            case "Вт": 
                this.day = 2;
                break;
            case "Ср": 
                this.day = 3;
                break;
            case "Чт": 
                this.day = 4;
                break;
            case "Пт": 
                this.day = 5;
                break;
            case "Сб": 
                this.day = 6;
                break;
            case "Вс": 
                this.day = 7;
                break;
            default:
                System.out.println("Некорректный день");
        }
    }
    
    public void plusHour(int hour){
        this.hour += hour;
        while(this.hour >= 24){
            this.hour -= 24;
            plusDay(1);
        }
        while(this.hour < 0){
            minusDay(1);
            this.hour += 24;
        }
    }
    
    public void plusMinute(int minute){
        this.minute += minute;
        while(this.minute >= 60){
            this.minute -= 60;
            plusHour(1);
        }
    }
    
    public void plusDay(int day){
        this.day += day;
        while(this.day > 7){
            this.day -= 7;
        }
    }
    
    public void minusDay(int day){
        this.day -= day;
        while(this.day < 1){
            this.day += 7;
        }
    }
    
    public void minusHour(int hour){
        this.hour -= hour;
        while(hour > this.hour){
            minusDay(1);
            this.hour += 24;
        }
    }
    
    public boolean isAfter (LocalDateTime time){
        if(time.getDayOfWeek().getValue() < day){
            if(day - time.getDayOfWeek().getValue() != 6 && day - time.getDayOfWeek().getValue() != 5){
                return true;
            }
        }else if(time.getDayOfWeek().getValue() == day){
            if(time.getHour() < hour){
                return true;
            }else if(time.getHour() == hour){
                if(time.getMinute() < minute){
                    return true;
                }
            }
        }else if(time.getDayOfWeek().getValue() > day && day == 1 && time.getDayOfWeek().getValue() == 7){
            return true;
        }
        
        return false;
    }
    
    public long getTimeToMilli(){
        long time;
        time = minute * 60 * 1000;
        time += hour * 60 * 60 * 1000;
        return time;
    }
    
    public long getDeltaTimeToMilli(int m){
        long time, minute1 = 0, minute2 = 0;
        LocalDateTime now = LocalDateTime.now();
        System.out.println(day - now.getDayOfWeek().getValue());
        if(day - now.getDayOfWeek().getValue() > 0){
            minute1 = 24 * 60;
        }
        if(day - now.getDayOfWeek().getValue() < 0){
            minute1 = 24 * 60;
        }
        minute1 += hour * 60;
        minute1 += minute;
        minute2 += now.getHour() * 60;
        minute2 += now.getMinute();
        time = minute1 - minute2 - m;
        time *= 60;
        time *= 1000;
        if(time < 0){
            return 0;
        }
        return time;
    }
    
    public String getTime(){
        String time = String.valueOf(hour) + ":" + String.valueOf(minute);
        return time;
    }
    
    public String timeForNext(){
        LocalTime now = LocalTime.now();
        int hour = this.hour, minute = this.minute;
        if(now.getHour() > this.hour){
            hour += 24;
        }
        if(now.getMinute() > this.minute){
            hour--;
            minute += 60;
        }
        hour -= now.getHour();
        minute -= now.getMinute();
        if(minute < 10){
            return String.valueOf(hour) + ":0" + String.valueOf(minute);
        }
        if(hour < 10){
            return "0" + String.valueOf(hour) + ":" + String.valueOf(minute);
        }
        if(hour < 10 && minute < 10){
            return "0" + String.valueOf(hour) + ":0" + String.valueOf(minute);
        }
        return String.valueOf(hour) + ":" + String.valueOf(minute);
    }
}
