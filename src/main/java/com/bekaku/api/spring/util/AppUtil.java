package com.bekaku.api.spring.util;

import com.bekaku.api.spring.enumtype.EmojiType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.vo.IpAddress;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {
    public static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";
    // strict regex
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";

    public static String genHashPassword(String passwordToHash, String salt) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static String getUserAgent(String userAgent) {
        String user = userAgent.toLowerCase();

        String os = "";
        String browser = "";

        if (userAgent.toLowerCase().contains("windows")) {
            os = "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            os = "Mac";
        } else if (userAgent.toLowerCase().contains("x11")) {
            os = "Unix";
        } else if (userAgent.toLowerCase().contains("android")) {
            os = "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            os = "IPhone";
        } else {
            os = "UnKnown, More-Info: " + userAgent;
        }
        if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera"))
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if (user.contains("opr"))
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6")) || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3"))) {
            browser = "Netscape-?";
        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else {
            browser = "UnKnown, More-Info: " + userAgent;
        }

        return os + " " + browser;
    }

    public static IpAddress getIpaddress() {

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage(), ""));
        }
        if (inetAddress != null) {
            return new IpAddress(inetAddress.getHostAddress(), inetAddress.getHostName());
        }
        return null;
    }

    public static IpAddress getIpaddress(HttpServletRequest request) {
        String ip = request.getHeader(ConstantData.X_REAL_IP);
        if (!isEmpty(ip)) {
            return new IpAddress(ip, null);
        }
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage(), ""));
        }
        if (inetAddress != null) {
            return new IpAddress(inetAddress.getHostAddress(), inetAddress.getHostName());
        }
        return null;
    }

    public static String getSimpleClassName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static String capitalizeFirstLetter(String str, boolean isLower) {
        if (isLower) {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String upperLowerCaseString(String str, boolean isLower) {
        if (isLower) {
            return str.toLowerCase();
        }
        return str.toUpperCase();
    }

    public static String camelToSnake(String str) {

        // Empty String
        StringBuilder result = new StringBuilder();

        // Append first character(in lower case)
        // to result string
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        // Tarverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result.append(ch);
            }
        }

        // return the result
        return result.toString();
    }

    public static double calculatePercentage(double obtained, double total) {
        if (obtained == 0 || total == 0) {
            return 0;
        }
        return (obtained * 100) / total;
    }

    public static double calculatePercentage(BigDecimal obtained, BigDecimal totalAmount) {
        BigDecimal zero = BigDecimal.valueOf(0);
        if (obtained.compareTo(zero) == 0 || totalAmount.compareTo(zero) == 0) {
            return 0;
        }
        BigDecimal percentageResult = obtained.multiply(new BigDecimal("100")).divide(totalAmount, 2, RoundingMode.HALF_UP);
        return percentageResult.doubleValue();
    }

    public static double round(double n, int decimals) {
        return Math.floor(n * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static float roundFloat(float n, int decimals) {
        return (float) (Math.floor(n * Math.pow(10, decimals)) / Math.pow(10, decimals));
    }

    public static double roundDouble(double obtained, int scale, RoundingMode mode) {
        if (scale > 0) {
            BigDecimal bd = new BigDecimal(obtained).setScale(scale, mode);
            return bd.doubleValue();
        }
        return obtained;
    }

    public static BigDecimal roundBigDecimal(BigDecimal obtained, int scale, RoundingMode mode) {
        if (scale > 0 && obtained.compareTo(BigDecimal.ZERO) > 0) {
            return obtained.setScale(scale, mode);
        }
        return obtained;
    }

    public static double calculatePercentageReverse(double percentage, double total) {
        if (percentage == 0 || total == 0) {
            return 0;
        }
        return (percentage / 100) * total;
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static boolean isEmailValid(String emailAddress) {
        return EmailValidator.getInstance().isValid(emailAddress);
    }

    public static String getEmojiTypeText(EmojiType emojiId) {
        String msg = "reaction.love";
        switch (emojiId) {
            case CARE -> msg = "reaction.care";
            case FIGHTING -> msg = "reaction.fighting";
            case LAUGH -> msg = "reaction.haha";
            case SAD -> msg = "reaction.sad";
            case WOW -> msg = "reaction.wow";
        }
        return msg;
    }

    public static String substring(int beginIndex, int endIndex, String value) {
        if (beginIndex < 0) {
            return value;
        }
        if (endIndex > value.length()) {
            return value;
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            return value;
        }
        return ((beginIndex == 0) && (endIndex == value.length())) ? value : new String(value.toCharArray(), beginIndex, subLen);
    }

    public static double calculatePercentRank(double[] values, double targetValue) {
        Arrays.sort(values);
        int rank = Arrays.binarySearch(values, targetValue);
        if (rank < 0) {
            rank = -(rank + 1);
        }
        return (double) rank / values.length * 100;
    }

    public static boolean isEmpty(Object o) {
        return ObjectUtils.isEmpty(o);
    }

    public static String getCookieByName(Cookie[] cookies, String targetCookieName) {
        String value = null;
        if (cookies != null) {
            // Iterate through the cookies and find the one with the specified name
            for (Cookie cookie : cookies) {
                if (targetCookieName.equals(cookie.getName())) {
                    // Found the desired cookie
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public static List<String> getCookieRefreshJwtTokenAll(Cookie[] cookies, String targetCookieName) {
        return Arrays.stream(cookies)
                .filter(c -> c.getName().startsWith(targetCookieName))
                .map(Cookie::getValue)
                .toList();
    }

    public static String getCookieByName(HttpServletRequest request, String targetCookieName) {
        return getCookieByName(request.getCookies(), targetCookieName);
    }

    public static Optional<String> readCookie(Cookie[] cookies, String key) {
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> key.equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();
    }

    public static int getCookieMaxAgeDays(int days) {
        return days * 24 * 60 * 60;
    }

    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Appending random digits (0-9)
        }
        return sb.toString();
    }

    public static boolean validatePasswordStrong(String password) {
        // Initialize variables
        int strength = 0;
        StringBuilder tips = new StringBuilder();

        // Check password length
        if (password.length() < 8) {
            tips.append("Make the password longer. ");
        } else {
            strength += 1;
        }

        // Check for mixed case
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            strength += 1;
        } else {
            tips.append("Use both lowercase and uppercase letters. ");
        }

        // Check for numbers
        if (password.matches(".*\\d.*")) {
            strength += 1;
        } else {
            tips.append("Include at least one number. ");
        }

        // Check for special characters
        Pattern pattern = Pattern.compile(".*[^a-zA-Z\\d].*");
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            strength += 1;
        } else {
            tips.append("Include at least one special character. ");
        }

        // Return results
        if (strength < 3) {
            // return "Easy to guess. " + tips.toString();
            return false;
        } else {
            // return "Medium difficulty or above. " + tips.toString();
            return true;
        }
    }

    public static boolean isValidSortOrder(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String direction = order.getDirection().toString().toLowerCase();
                if (!direction.equals("asc") && !direction.equals("desc")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNumber(String str) {
        return str.matches("-?\\d+");
    }

    public static boolean isNonNegativeNumber(String str) {
        return str.matches("\\d+");
    }

    public static int maxRunningNoByLength(int length) {
        StringBuilder maxRunningString = new StringBuilder();
        int maxRunning;
        for (int i = 0; i < length; i++) {
            maxRunningString.append("9");
        }
        maxRunning = Integer.parseInt(maxRunningString.toString());
        return maxRunning;
    }

    public static String runningNoPrependPrefix(int maxLength, int runningNo) {
        int runningLength = String.valueOf(runningNo).length();
        StringBuilder runningNoPrefix = new StringBuilder();
        if (runningLength < maxLength) {
            int prefixLength = maxLength - runningLength;
            for (int i = 0; i < prefixLength; i++) {
                runningNoPrefix.append("0");
            }
        }
        return runningNoPrefix.toString() + runningNo;
    }

    public static long[] getLongListFromGroupConcatString(String groupConcat, String separator) {
        if (isEmpty(groupConcat)) {
            return new long[0];
        }
        String[] parts = groupConcat.split(separator);
        long[] result = new long[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Long.parseLong(parts[i]);
        }
        return result;
    }

    public static boolean findStringInString(String text, String searchText) {
        return text.contains(searchText);
    }
}
