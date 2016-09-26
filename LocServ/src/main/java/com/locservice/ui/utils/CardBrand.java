package com.locservice.ui.utils;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public enum  CardBrand {

    Unknown(Pattern.compile("^unknown$")),
    Visa(Pattern.compile("^4[0-9]{6,}$")),
    // MasterCard numbers start with the numbers 51 through 55, and 2221 through 2720.
    MasterCard(Pattern.compile("^5[1-5][0-9]{5,}$|^(222[1-9]|2[3-6][0-9][0-9]|27[0-1][0-9]|2720)[0-9]{12}$")),
    AmericanExpress(Pattern.compile("^3[47][0-9]{5,}$")),
    // Diners Club card numbers begin with 300 through 305, 36 or 38.
    DinersClub(Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$")),
    Discover(Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{3,}$")),
    JCB(Pattern.compile("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$")), ;

    public static CardBrand from(final String accountNumber)
    {
        if (isBlank(accountNumber))
        {
            return Unknown;
        }
        for (final CardBrand cardBrand: values())
        {
            if (cardBrand.pattern.matcher(accountNumber).matches())
            {
                return cardBrand;
            }
        }
        return Unknown;
    }

    private final Pattern pattern;

    private CardBrand(final Pattern pattern) {
        this.pattern = pattern;
    }
}
