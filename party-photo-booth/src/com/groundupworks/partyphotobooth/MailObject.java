package com.groundupworks.partyphotobooth;

import com.google.android.gms.drive.internal.m;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by User on 13.09.2016.
 */
public class MailObject {

    private String hostAddress = "";

    private String hostPassword = "";

    private String userAddress = "";

    private String filePath = "";

    private String subject = "";

    private String message = "";

    public String id = "1000000";

    public MailObject() {

    }

    public MailObject(String hostAddress, String hostPassword, String userAddress, String filePath, String subject, String message) {
        this.hostAddress = hostAddress;
        this.hostPassword = hostPassword;
        this.userAddress = userAddress;
        this.filePath = filePath;
        this.subject = subject;
        this.message = message;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public String getHostPassword() {
        return this.hostPassword;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getUserAddress() {
        return this.userAddress;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getMessage() {
        return this.message;
    }

}
