package com.actuator.ui.model;

import com.actuator.ui.controller.PreferenceController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DayInfo {
    public final static String[] HolidayStatus = {"휴일", "연차휴가", "공가", "교육(사내)", "교육(사외)", "출장(국내)", "출장(국외)"};
    private DateTimeFormatter FULL_PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm");
    private SimpleStringProperty index;
    private SimpleStringProperty day;
    private SimpleStringProperty dayType;
    private SimpleStringProperty dayTypeTime;
    private SimpleObjectProperty<LocalDateTime> getTheWork;
    private SimpleObjectProperty<LocalDateTime> leaveTheWork;
    private SimpleStringProperty status;
    private SimpleStringProperty workTime;
    private Duration workTimeDuration;
    private Duration breakTimeDuration;

    public enum Element {
        index(0), date(1), dayType(2), dayStatus(4), dayTypeTime(5), getTheWork(6), status(7), leaveTheWork(9), workTime(11);
        private int order;

        Element(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
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

    public void updateValuesAtColumns(String columnName, String newValue) {
        if (columnName.equals("Date"))
            columnName = "date";
        else if (columnName.equals("IN"))
            columnName = "getTheWork";
        else if (columnName.equals("OUT"))
            columnName = "leaveTheWork";
        else if (columnName.equals("DayType"))
            columnName = "dayType";
        else if (columnName.equals("DayTypeTime"))
            columnName = "dayTypeTime";
        switch (Element.valueOf(columnName)) {
            case date:
                day.setValue(newValue);
                break;
            case dayStatus:
            case dayType:
                if (newValue.length() > 0)
                    dayType.setValue(newValue);
                break;
            case dayTypeTime:
                dayTypeTime.setValue(newValue);
                break;
            case getTheWork:
                if (newValue.split(":")[0].length() == 1)
                    newValue = "0" + newValue;
                getTheWork.setValue(dateTimeParse(newValue));
                break;
            case leaveTheWork:
                if (newValue.split(":")[0].length() == 1)
                    newValue = "0" + newValue;
                leaveTheWork.setValue(dateTimeParse(newValue));
                break;
        }
    }

    private void calculateWorkTime() {
        if (!isHolidays()) {
            workTimeDuration = Duration.between(getTheWork.get(), leaveTheWork.get());
            breakTimeDuration = Duration.ofMinutes(0);
            if (isHalfRest())
                workTimeDuration = workTimeDuration.plusHours(4);
            if (isExistDayTimeAndEquals("외근")) {
                String raw = dayTypeTime.getValue();
                if (raw.contains("."))
                    raw = raw.replace(".", ":");
                String[] v = raw.split(":");
                workTimeDuration = workTimeDuration.plusHours(Long.parseLong(v[0]));
                workTimeDuration = workTimeDuration.plusMinutes(Long.parseLong(v[1]));
            }
            Duration temp = Duration.ofMinutes(workTimeDuration.toMinutes());
            if ((temp.toHours() - 1) >= 12) {
                breakTimeDuration = breakTimeDuration.plusHours(1);
                workTime.setValue("12.00(" + getCalculateTime() + ")");
                workTimeDuration = Duration.ofHours(12);
            } else {
                if (temp.minusMinutes(240).toMinutes() > 0) {
                    minusBreakTime(temp);
                    temp = temp.minusMinutes(240);
                    if (temp.minusMinutes(240).toMinutes() > 0) {
                        minusBreakTime(temp);
                    }
                }
                workTimeDuration = workTimeDuration.minus(breakTimeDuration);
                workTime.setValue(getCalculateTime() + getBreakTimeString());
            }
        } else {
            workTimeDuration = Duration.ofHours(8);
            workTime.setValue("8.00");
        }
    }

    private void minusBreakTime(Duration temp) {
        if (temp.minusMinutes(240).toMinutes() <= 30) {
            breakTimeDuration = breakTimeDuration.plusMinutes(temp.minusMinutes(240).toMinutes());
        } else {
            breakTimeDuration = breakTimeDuration.plusMinutes(30);
        }
    }

    private boolean isExistDayTimeAndEquals(String status) {
        if (dayType.getValue().equals(status) && dayTypeTime.getValue() != null)
            return true;
        return false;
    }

    private boolean isHalfRest() {
        return dayType.getValue().contains("반차") || dayType.getValue().contains("반일");
    }

    private String getCalculateTime() {
        String s = workTimeDuration.toHours() + ".";
        if (getDigit(workTimeDuration.toMinutes()) <= 1)
            s+="0";
        return s + workTimeDuration.toMinutes() % 60;
    }

    private String getBreakTimeString() {
        if (breakTimeDuration.toMinutes() == 0)
            return  "";
        if (breakTimeDuration.toHours() == 1)
            return "(-1.0)";
        if (getDigit(breakTimeDuration.toMinutes()) <= 1) {
            return "(-0.0" + breakTimeDuration.toMinutes() + ")";
        }
        return "(-0." + breakTimeDuration.toMinutes() + ")";
    }

    private int getDigit(long stringNum) {
        return (int) (Math.log10(stringNum % 60) + 1);
    }

    private void setValue(Element element, String s) {
        switch (element) {
            case index:
                this.index = new SimpleStringProperty(s);
                break;
            case dayStatus:
            case dayType:
                if (dayType == null)
                    this.dayType = new SimpleStringProperty(s);
                else if (s.length() > 0) {
                    dayType.setValue(s);
                }
                break;
            case dayTypeTime:
                this.dayTypeTime = new SimpleStringProperty(s);
                if (dayType.getValue().equals("외근")) {
                    dayTypeTime.setValue(s);
                }
                break;
            case getTheWork:
                if (s.length() == 0)
                    s = "00:00";
                if (s.split(":")[0].length() == 1)
                    s ="0" + s;
                this.getTheWork = new SimpleObjectProperty<>(dateTimeParse(s));
                break;
            case status:
                this.status = new SimpleStringProperty(s);
                break;
            case leaveTheWork:
                if (s.length() == 0)
                    s = "00:00";
                if (s.split(":")[0].length() == 1)
                    s ="0" + s;
                this.leaveTheWork = new SimpleObjectProperty<>(dateTimeParse(s));
                break;
            case workTime:
                this.workTime = new SimpleStringProperty(s);
                break;
        }
    }

    public static String[] getDayStatusList() {
        List<String> v = new ArrayList<>();
        v.add("평일");
        v.addAll(Arrays.asList(HolidayStatus));
        String[] r = new String[v.size()];
        v.toArray(r);
        return r;
    }

    public LocalDateTime dateTimeParse(String s) {
        return LocalDateTime.parse(day.getValue() + " " + s, FULL_PATTERN);
    }

    public int remainTiem() {
        return getTheWork.get().getMinute();
    }

    public Duration getWorkTimeDuration() {
        if (isToday() && (PreferenceController.getInstance() == null || PreferenceController.getInstance().getUseCurrentTime())) {
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
        return Arrays.asList(HolidayStatus).contains(dayType.getValue()) || dayType.getValue().contains("휴가");
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

    public SimpleStringProperty dayTypeProperty() {
        return dayType;
    }

    public String getDayType() {
        return dayType.get();
    }

    public void setDayType(String dayType) {
        this.dayType.set(dayType);
    }

    public String getGetTheWork() {
        return getTheWork.get().format(TIME_PATTERN);
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
        return leaveTheWork.get().format(TIME_PATTERN);
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

    public String getDayTypeTime() {
        return dayTypeTime.get();
    }

    public void setDayTypeTime(String dayTypeTime) {
        this.dayTypeTime.set(dayTypeTime);
    }
}
