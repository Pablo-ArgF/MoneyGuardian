package com.moneyguardian.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalFilterForInput implements InputFilter {

    private Pattern pattern;

    public DecimalFilterForInput(int numbersAfterZero) {
        this.pattern = Pattern.compile("[0-9]*+((\\.[0-9]{0," + numbersAfterZero
                                                                                + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                                                            int dstart, int dend) {
        Matcher matcher = pattern.matcher(TextUtils.concat(dest.subSequence(0, dstart),
                            source.subSequence(start, end), dest.subSequence(dend, dest.length())));
        if (!matcher.matches())
            return "";
        return null;
    }
}
