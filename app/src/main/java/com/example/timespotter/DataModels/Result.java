package com.example.timespotter.DataModels;

public class Result<T> {

    public static int OPERATION_SUCCESS = 0;
    public static int OPERATION_FAILURE = 1;
    private int operationStatus = OPERATION_SUCCESS;
    private T value;
    private Exception exception;

    public Result() {
    }

    public Result<T> setOperationSuccess(int operationStatus) {
        this.operationStatus = operationStatus;
        return this;
    }

    public int getStatus() {
        return operationStatus;
    }

    public T getValue() {
        return value;
    }

    public Result<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public Exception getError() {
        return exception;
    }

    public Result<T> setError(Exception e) {
        exception = e;
        return this;
    }
}
