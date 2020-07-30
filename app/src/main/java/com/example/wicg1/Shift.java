package com.example.wicg1;

import androidx.annotation.Nullable;

import java.util.Date;

class Shift {

    public static class Hour {
        int hour;
        int minute;
        public Hour(){}
        public Hour(int hour,int minute){
            this.hour=hour;
            this.minute=minute;
        }
        public int getHour(){return hour;}

        public int getMinute(){return minute;}


        public boolean equals(Hour obj) {
            return hour==obj.hour && minute==obj.minute;
        }

        public String toString(){ return hour+":"+minute;}
    }

    public static class Date{
        int day;
        int month;
        int year;

        public Date(int day,int month,int year){
            this.day=day;
            this.month=month;
            this.year=year;
        }
        public Date(){
        }

        public int getDay(){return day;}

        public int getMonth(){return month;}
        public int getYear(){return year;}



        public boolean equals(Date obj) {
            return day==obj.day && month==obj.month && year==obj.year ;
        }

        public String toString(){return day+"/"+month+"/"+year;}
    }

    Date date;
    Hour startingHour;
    Hour endingHour;

    public Shift(){
    }

    public Shift(Date date, Hour start, Hour finish){
        this.date=date;
        this.startingHour=start;
        this.endingHour=finish;
    }

    public Date getDate(){return date;}

    public Hour getStartingHour(){return startingHour;}

    public Hour getEndingHour(){return endingHour;}


    public boolean equals(Shift obj) {
        return this.date.equals(obj.date) && this.startingHour.equals(obj.startingHour) && this.endingHour.equals(obj.endingHour);
    }

    public String toString(){return date.toString()+" "+startingHour.toString()+" "+endingHour.toString();}

}
