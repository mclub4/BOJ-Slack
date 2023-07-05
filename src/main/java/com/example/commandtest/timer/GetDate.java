package com.example.commandtest.timer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * 현재 시간이 메시지 보내는 시간인지, 한번 보낸적이 있는지 체크하는 Class
 */

public class GetDate {
    public static LocalDate TODAY;
    public static LocalDateTime NOW;
    public static LocalDateTime START;
    public static LocalDateTime PM12;
    public static LocalDateTime STANDARD;

    private static boolean[] cycleCheck = {false, false, false, false};

    public void setTime(){
        TODAY = LocalDate.now();
        START = LocalDateTime.of(TODAY, LocalTime.of(0,0));
        PM12 = LocalDateTime.of(TODAY, LocalTime.of(12,0));
        STANDARD = LocalDateTime.of(TODAY.plusDays(1L), LocalTime.of(1,0));
    }

    public boolean checkDate(){
        NOW = LocalDateTime.now();
        int hour = NOW.getHour();
        int minute = NOW.getMinute();
        if(NOW.isAfter(PM12) && NOW.isBefore(STANDARD)){
            int key = setKey(hour, minute);
            if(!cycleCheck[key]) {
                Arrays.fill(cycleCheck, false);
                cycleCheck[key] = true;
                return true;
            }
        }
        return false;
    }

    public int setKey(int hour, int minute){
        if((12<= hour && hour<19) || (hour == 19 && minute<30)) return 0;
        else if((20<= hour && hour<23) || (hour == 19 && minute>=30)) return 1;
        else if(23<= hour) return 2;
        else if(hour == 0)  return 3;
        else return -1;
    }

    public String test(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
        String nowString = now.format(dateTimeFormatter);
        return nowString;
    }
}
