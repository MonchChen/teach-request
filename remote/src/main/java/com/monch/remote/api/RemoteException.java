package com.monch.remote.api;

/**
 * @author 陈磊.
 */

public class RemoteException extends Exception {

    public static final int LOGIN_CODE = -10;
    public static final RemoteException LOGIN_EXCEPTION = new RemoteException(LOGIN_CODE, "Logout");

    private int code;
    private String message;

    public RemoteException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
