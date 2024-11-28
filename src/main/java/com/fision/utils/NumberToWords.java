package com.fision.utils;

import java.math.BigDecimal;
/**
 * @author LordDev
 */

public class NumberToWords {

    private static final String[] UNITS = {
            "", "Satu", "Dua", "Tiga", "Empat", "Lima",
            "Enam", "Tujuh", "Delapan", "Sembilan"
    };

    private static final String[] TENS = {
            "", "Sepuluh", "Dua Puluh", "Tiga Puluh", "Empat Puluh",
            "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh"
    };

    private static final String[] TEENS = {
            "Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas",
            "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas"
    };

    public static String convertToWords(BigDecimal number) {
        if (number == null) {
            throw new IllegalArgumentException("Angka tidak boleh null");
        }

        long nilai = number.longValue(); // Konversi BigDecimal ke long
        if (nilai == 0) {
            return "Nol Rupiah";
        }

        return convert(nilai).trim() + " Rupiah";
    }

    private static String convert(long number) {
        if (number < 10) {
            return UNITS[(int) number];
        } else if (number < 20) {
            return TEENS[(int) (number - 10)];
        } else if (number < 100) {
            return TENS[(int) (number / 10)] + " " + UNITS[(int) (number % 10)];
        } else if (number < 1000) {
            return UNITS[(int) (number / 100)] + " Ratus " + convert(number % 100);
        } else if (number < 1000000) {
            return convert(number / 1000) + " Ribu " + convert(number % 1000);
        } else if (number < 1000000000) {
            return convert(number / 1000000) + " Juta " + convert(number % 1000000);
        }
        return "";
    }
}

