package com.example.timespotter.Utils;

import com.example.timespotter.Enums.EventType;

public class Result {
    public static final boolean SUCCESS = true;
    public static final boolean FAILURE = false;
    public EventType event;
    public Result() {}
    public Object result;
    public String errMsg;
    public boolean operationStatus;
}
