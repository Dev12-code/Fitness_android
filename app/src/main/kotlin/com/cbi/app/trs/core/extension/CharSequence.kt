package com.cbi.app.trs.core.extension

fun CharSequence.isValidPassword(): String? {
    if (length in 8..16) {
//        if (!contains(Regex("[0-9]")))
//            return "Your password must be between 8 to 16 characters and include at least 1 uppercase, lowercase and special character."
        if (!contains(Regex("[a-z]")))
            return "Your password must be between 8 to 16 characters, include at least 1 uppercase letter, 1 lowercase letter and 1 of these special characters: @, \$, #, !, %, *, ?, &"
        if (!contains(Regex("[A-Z]")))
            return "Your password must be between 8 to 16 characters, include at least 1 uppercase letter, 1 lowercase letter and 1 of these special characters: @, \$, #, !, %, *, ?, &"
        if (!contains(Regex("[-@%[}+'!/#\$^?:;,(\")~`.=&{>]<_]")))
            return "Your password must be between 8 to 16 characters, include at least 1 uppercase letter, 1 lowercase letter and 1 of these special characters: @, \$, #, !, %, *, ?, &"
    } else {
        return "Your password must be between 8 and 16 characters."
    }
    return null
}