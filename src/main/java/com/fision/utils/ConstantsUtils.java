package com.fision.utils;

public class ConstantsUtils {
    // User desc
    public final static String USER_NOT_FOUND = "Username not found.";
    public final static String USERNAME_ALREADY_USED = "Username already used, please use another username.";
    public final static String USERNAME_HAS_CHANGED = "Username has changed. Please re login using your new username.";
    public final static String INVALID_PASSWORD = "Password not matched.";
    public final static String PASSWORD_HAS_CHANGED = "Password has changed. Please re login using your new password.";
    public final static String PASSWORD_CANT_BE_SAME = "New Password cannot be same with your current password.";
    public final static String USER_HAS_BEEN_CREATED = "User has been created. Please contact the user to check email for the password.";
    public final static String USER_HAS_BEEN_UPDATED = "User has been updated.";

    // Response Desc
    public final static String SUCCESS = "Success";
    public final static String ERROR_SYSTEM = "System error";
    public final static String INVALID_REQUEST = "Invalid Request";
    public final static String INVALID_USERNAME_OR_PASS = "Invalid username or password";
    public final static String INVALID_EMAIL = "Invalid Email";
    public final static String EMAIL_NOT_REGISTERED = "Email not registered";

    // Separator/delimiter
    public final static String COMMA_SEPARATOR = ",";
    public final static String SEMICOLON_SEPARATOR = ";";

    public final static String SUPER_ADM = "S-ADM";
    public final static String ADMIN = "ADMN";

    public final static String ACTIVE = "ACTIVE";
    public final static String INACTIVE = "INACTIVE";
    public final static String YES = "YES";
    public final static String NO = "NO";


    // Email
    public final static String RESET_PASSWORD = "Reset Password for FiSion Apps";
    public final static String NEW_ACCOUNT_PASSWORD = "Login Password for FiSion Apps";
    public final static String EMAIL_BODY_TMPL = "Silahkan login dengan kembali dengan password berikut : ";

    // Data Desc
    public final static String DATA_SAVED = "Data has been saved.";
    public final static String DATA_NOT_FOUND = "Data not found.";
    public final static String PROJECT_NAME_ALREADY_USED = "Project name already used, please try another name.";
    public final static String ITEM_NAME_ALREADY_USED = "Item name already used, please try another name.";
    public final static String VENDOR_NAME_ALREADY_USED = "Vendor name already used, please try another name.";
    public final static String INVOICE_NO_DUPLICATE = "Invoice no already generated and the status is approved/not approved.";

    // Items
    public final static String TOTAL_QUANTITY_SMALLER_THAN_PAID_QUANTITY = "Total Quantity smaller than total paid quantity, please make sure the new total quantity greater than paid quantity for each item.";
    public final static String TOTAL_QUANTITY_EQUALS_WITH_EXISTING = "Total Quantity are same with existing quantity, please make sure the new total quantity are different with existing quantity if you wanna change.";

    // ARInvoice
    public final static String DOC_TRACKING_ON_PROCESS = "On Process";
    public final static String DOC_TRACKING_SUBMITTED = "Submitted";
}
