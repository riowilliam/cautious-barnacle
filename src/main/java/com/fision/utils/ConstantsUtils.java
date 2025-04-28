package com.fision.utils;

public class ConstantsUtils {
    // User desc
    public static final String USER_NOT_FOUND = "Username not found.";
    public static final String USERNAME_ALREADY_USED = "Username already used, please use another username.";
    public static final String USERNAME_HAS_CHANGED = "Username has changed. Please re login using your new username.";
    public static final String FULLNAME_HAS_CHANGED = "Full Name has changed.";
    public static final String INVALID_PASSWORD = "Password not matched.";
    public static final String PASSWORD_HAS_CHANGED = "Password has changed. Please re login using your new password.";
    public static final String PASSWORD_CANT_BE_SAME = "New Password cannot be same with your current password.";
    public static final String USER_HAS_BEEN_CREATED = "User has been created. Please contact the user to check email for the password.";
    public static final String USER_HAS_BEEN_UPDATED = "User has been updated.";

    // Response Desc
    public static final String SUCCESS = "Success";
    public static final String ERROR_SYSTEM = "System error";
    public static final String INVALID_REQUEST = "Invalid Request";
    public static final String INVALID_USERNAME_OR_PASS = "Invalid username or password";
    public static final String INVALID_EMAIL = "Invalid Email";
    public static final String EMAIL_NOT_REGISTERED = "Email not registered";

    // Separator/delimiter
    public static final String COMMA_SEPARATOR = ",";
    public static final String SEMICOLON_SEPARATOR = ";";

    public static final String SUPER_ADM = "FS-ADM";
    public static final String ADMIN = "ADMN";

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String YES = "YES";
    public static final String NO = "NO";


    // Email
    public static final String RESET_PASSWORD = "Reset Password for FiSion Apps";
    public static final String NEW_ACCOUNT_PASSWORD = "Login Password for FiSion Apps";
    public static final String EMAIL_BODY_TMPL = "Hello USER_PLACEHOLDER. Silahkan login dengan password berikut : ";

    // Data Desc
    public static final String DATA_SAVED = "Data has been saved.";
    public static final String DATA_NOT_FOUND = "Data not found.";
    public static final String PROJECT_NAME_ALREADY_USED = "Project name already used, please try another name.";
    public static final String PARTNER_NAME_ALREADY_USED = "Partner name already used, please try another name.";

    public static final String ITEM_NAME_ALREADY_USED = "Item name already used, please try another name.";
    public static final String BANK_ACCOUNT_ALREADY_USED = "Bank account already used.";
    public static final String VENDOR_NAME_ALREADY_USED = "Vendor name already used, please use another username.";
    public static final String INVOICE_NO_DUPLICATE = "Invoice no already generated and the status is approved/not approved.";

    // Items
    public static final String TOTAL_QUANTITY_SMALLER_THAN_PAID_QUANTITY = "Total Quantity smaller than total paid quantity, please make sure the new total quantity greater than paid quantity for each item.";
    public static final String TOTAL_QUANTITY_EQUALS_WITH_EXISTING = "Total Quantity are same with existing quantity, please make sure the new total quantity are different with existing quantity if you wanna change.";

    // ARInvoice
    public static final String DOC_TRACKING_ON_PROCESS = "On Process";
    public static final String DOC_TRACKING_SUBMITTED = "Submitted";

    // Payment Type
    public static final String FULLY_PAYMENT = "Fully Payment";
    public static final String PARTIALLY_PAYMENT = "Partially Payment";

    public static final String FULLY_PAID = "Fully Paid";
    public static final String PARTIALLY_PAID = "Partially Paid";

    // Cash In
    public static final String COMPLETED = "Completed";
    public static final String INCOMPLETED = "Incompleted";
    public static final String PAYMENT_TOTAL_LESS_THAN_AMOUNT = "Can not create cash in because total amount of invoice less than payment amount.";
    public static final String THERE_ARE_INCOMPLETE_PAYMENT = "Please complete the previously cash in for this invoice first.";
    public static final String PAYMENT_BANK_CODE_NULL = "Payment bank code invalid.";

    // Dashboard
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String YEARLY = "Yearly";
    public static final String ALL = "All";
    public static final String INIT_BALANCE = "INIT_BALANCE";

    // Config
    public static final String PPH_LIST = "PPH_LIST";
    public static final String FACILITY_TRANSACTION_SCHEDULER = "FACILITY_TRANSACTION_SCHEDULER";
    public static final String FACILITY_TO_CASH_OUT_SCHEDULER = "FACILITY_TO_CASH_OUT_SCHEDULER";

    // Balance Notes
    public static final String BANK_NOT_REGISTERED = "BANK NOT REGISTERED";

    // Invoice
    public static final String INVOICE_NOTES = "Pembayaran ke - PLACEHOLDER_PROGRESS atas invoice PLACEHOLDER_INVOICE";
    public static final String PLACEHOLDER_INVOICE = "PLACEHOLDER_INVOICE";
    public static final String PLACEHOLDER_PROGRESS = "PLACEHOLDER_PROGRESS";
    public static final String NO_INVOICE_PREFIX = "NO_INV-";

    // Facility
    public static final String PAYMENT = "Payment";
    public static final String FACILITY_TYPE_BG = "BG";
    public static final String FACILITY_NOT_REGISTERED = "Data Facility Type tidak terdaftar di FISION, Facility Type : ";

    // System
    public static final String SYSTEM = "System";
}
