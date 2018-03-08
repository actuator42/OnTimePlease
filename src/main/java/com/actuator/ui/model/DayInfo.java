package com.actuator.ui.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DayInfo {
    private DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private SimpleStringProperty index;
    private SimpleStringProperty day;
    private SimpleStringProperty dayType;
    private SimpleObjectProperty<LocalDateTime> getTheWork;
    private SimpleObjectProperty<LocalDateTime> leaveTheWork;
    private SimpleStringProperty status;
    private SimpleStringProperty workTime;
    private Duration workTimeDuration;

    public enum Element {
        index(0), date(1), dayType(2), getTheWork(6), status(7), leaveTheWork(9), workTime(11);
        private int order;

        Element(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        public static String getTableOrder(int num) {
            for (Element e : Element.values()) {
                if (e.ordinal() == num)
                    return e.name();
            }
            return "None";
        }
    }

    public DayInfo() {
        this.day = new SimpleStringProperty();
    }

    public DayInfo(String raw) {
        String[] split = raw.split("\t");
        this.day = new SimpleStringProperty(split[Element.date.order]);
        for (Element element : Element.values()) {
            setValue(element, split[element.order]);
        }
        calculateWorkTime();
    }

    public void setDefault() {
        for (Element element : Element.values()) {
            setValue(element, "");
        }
    }

    public void setValuesAtColumns(String columnName, String newValue) {
        if (columnName.equals("IN"))
            columnName = "getTheWork";
        else if (columnName.equals("OUT"))
            columnName = "leaveTheWork";
        switch (Element.valueOf(columnName)) {
            case date:
                day.setValue(newValue);
                break;
            case dayType:
                dayType.setValue(newValue);
                break;
            case getTheWork:
                getTheWork.setValue(dateTimeParse(newValue));
                break;
            case leaveTheWork:
                leaveTheWork.setValue(dateTimeParse(newValue));
                break;
        }
    }

    private void calculateWorkTime() {
        if (!isHolidays()) {
            workTimeDuration = Duration.between(getTheWork.get(), leaveTheWork.get());
            if (workTimeDuration.toHours() >= 8) {
                workTimeDuration = workTimeDuration.minusHours(1);
            } else if (workTimeDuration.toHours() >= 4) {
                workTimeDuration = workTimeDuration.minusMinutes(30);
            }
        } else {
            workTimeDuration = Duration.ofSeconds(0).plusHours(8);
        }
        workTime.setValue(getCalculateTime());
    }

    private String getCalculateTime() {
        String s = workTimeDuration.toHours() + ".";
        int digit = (int) (Math.log10(workTimeDuration.toMinutes() % 60) + 1);
        if (digit <= 1)
            s+="0";
        return s + workTimeDuration.toMinutes() % 60;
    }

    private void setValue(Element element, String s) {
        switch (element) {
            case index:
                this.index = new SimpleStringProperty(s);
                break;
            case dayType:
                this.dayType = new SimpleStringProperty(s);
                break;
            case getTheWork:
                if (s.length() == 0)
                    s = "00:00";
                this.getTheWork = new SimpleObjectProperty<>(dateTimeParse(s));
                break;
            case status:
                this.status = new SimpleStringProperty(s);
                break;
            case leaveTheWork:
                if (s.length() == 0)
                    s = "00:00";
                this.leaveTheWork = new SimpleObjectProperty<>(dateTimeParse(s));
                break;
            case workTime:
                this.workTime = new SimpleStringProperty(s);
                break;
        }
    }

    public LocalDateTime dateTimeParse(String s) {
        return LocalDateTime.parse(day.getValue() + " " + s, PATTERN);
    }

    public int remainTiem() {
        return getTheWork.get().getMinute();
    }

    public Duration getWorkTimeDuration() {
        if (isToday()) {
            setLeaveTheWork(LocalDateTime.now());
        }
        calculateWorkTime();
        return workTimeDuration;
    }

    public String getIndex() {
        return index.get();
    }

    public boolean isToday() {
        return getTheWork.get().getDayOfMonth() == LocalDate.now().getDayOfMonth();
    }

    public boolean isHolidays() {
        return dayType.getValue().equals("휴일");
    }

    public void setIndex(String index) {
        this.index.set(index);
    }

    public String getDay() {
        return day.get();
    }

    public void setDay(String day) {
        this.day.set(day);
    }

    public String getDayType() {
        return dayType.get();
    }

    public void setDayType(String dayType) {
        this.dayType.set(dayType);
    }

    public String getGetTheWork() {
        return getTheWork.get().getHour() + ":" + getTheWork.get().getMinute();
    }

    public void setGetTheWork(String getTheWork) {
        this.getTheWork.set(dateTimeParse(getTheWork));
    }

    public void setGetTheWork(LocalDateTime dateTime) {
        this.getTheWork.set(dateTime);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getLeaveTheWork() {
        return leaveTheWork.get().getHour() + ":" + leaveTheWork.get().getMinute();
    }

    public void setLeaveTheWork(String leaveTheWork) {
        this.leaveTheWork.set(dateTimeParse(leaveTheWork));
    }

    public void setLeaveTheWork(LocalDateTime dateTime) {
        this.leaveTheWork.set(dateTime);
    }

    public String getWorkTime() {
        return workTime.get();
    }

    public void setWorkTime(String workTime) {
        this.workTime.set(workTime);
    }
}
