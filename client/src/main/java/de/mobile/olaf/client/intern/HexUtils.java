package de.mobile.olaf.client.intern;

public final class HexUtils {

    private static final char[] HEX_DIGITS = { //
        '0', '1', '2', '3', '4', '5', '6', '7', //
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' //
    };

    private static final byte[] DIGITS = { //
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, //
            -1, -1, -1, -1, -1, -1, -1, //
            10, 11, 12, 13, 14, 15, // 'A' ... 'F'
            -1, -1, -1, -1, -1, -1, -1, -1, -1, //
            -1, -1, -1, -1, -1, -1, -1, -1, -1, //
            -1, -1, -1, -1, -1, -1, -1, -1, //
            10, 11, 12, 13, 14, 15, // 'a' - 'f'
    };

    private HexUtils() {
    }

    public static String toHexString(byte b) {
        return String.format("%02X", b);
    }

    public static String toHexString(byte[] arg) {
        if (arg == null) {
            return null;
        }
        char[] cArray = new char[arg.length * 2];
        int j = 0;
        for (int i = 0; i < arg.length; i++) {
            int index = arg[i] & 0xFF;
            cArray[j++] = HEX_DIGITS[(index >> 4) & 0xF];
            cArray[j++] = HEX_DIGITS[index & 0xF];
        }
        return new String(cArray, 0, j);
    }

    public static byte[] toBytes(String hex) {
        while (hex.length() > 2 && hex.charAt(0) == '0' && hex.charAt(1) == '0') {
            hex = hex.substring(2); // trim leading zeros
        }
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        final int length = hex.length();
        boolean badHex = false;
        byte[] out = new byte[length >> 1];
        for (int i = 0, j = 0; j < length; i++) {
            int c1 = hex.charAt(j++);
            if (c1 > 'f') {
                badHex = true;
                break;
            }

            final byte d1 = DIGITS[c1 - '0'];
            if (d1 == -1) {
                badHex = true;
                break;
            }

            int c2 = hex.charAt(j++);
            if (c2 > 'f') {
                badHex = true;
                break;
            }

            final byte d2 = DIGITS[c2 - '0'];
            if (d2 == -1) {
                badHex = true;
                break;
            }

            out[i] = (byte) (d1 << 4 | d2);
        }

        if (badHex) {
            throw new NumberFormatException("Invalid hexadecimal digit: " + hex);
        }
        return out;

    }

}
