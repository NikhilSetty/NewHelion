package com.teachmate.teachmate.DBClasses;

public class Schema {
    public static final String CREATE_TABLE_Profile = "create table if not exists " + DbTableStrings.TABLE_NAME_USER_MODEL +
            "( _id integer primary key autoincrement, "
            + DbTableStrings.SERVERUSERID + " string, "
            + DbTableStrings.FNAME + " string, "
            + DbTableStrings.EMAILID + " string, "
            + DbTableStrings.ADDRESS1 + " string, "
            + DbTableStrings.PINCODE1 + " string, "
            + DbTableStrings.ADDRESS2 + " string, "
            + DbTableStrings.PINCODE2 + " string) ";

    public static final String CREATE_TABLE_REQUESTS = "create table if not exists " + DbTableStrings.TABLE_NAME_REQUESTS +
            "( _id integer primary key autoincrement, "
            + DbTableStrings.REQUEST_ID + " string, "
            + DbTableStrings.REQUEST_EUSER_ID + " string, "
            + DbTableStrings.REQUEST_USERNAME + " string, "
            + DbTableStrings.REQUEST_STRING + " string, "
            + DbTableStrings.REQUEST_TIME + " string, "
            + DbTableStrings.REQUEST_USER_PROFESSION + " string, "
            + DbTableStrings.REQUEST_USER_PROFILE_PHOTO_SERVER_PATH + " string, "
            + DbTableStrings.REQUEST_YEAR + " int, "
            + DbTableStrings.REQUEST_DAY_OF_THE_YEAR + " int) ";


    public static final String CREATE_TABLE_DEVICE_INFO = "create table if not exists " + DbTableStrings.TABLE_NAME_DEVICE_INFO +
            "( _id integer primary key autoincrement, "
            + DbTableStrings.KEY + " string, "
            + DbTableStrings.VALUE+ " string) ";
}


