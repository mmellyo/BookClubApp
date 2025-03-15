package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Common {

    private static final String PHONE_REGEX = "^0\\d{9}$"; // 10 digits, starting with 0
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";



    // Validate Date Format
    public static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as needed
        sdf.setLenient(false); // Strict validation
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Validate mail Format
    public static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    // Validate phoennmbr Format
    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.compile(PHONE_REGEX).matcher(phone).matches();
    }

    // Validate isPasswordMatching Format
    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
 }


