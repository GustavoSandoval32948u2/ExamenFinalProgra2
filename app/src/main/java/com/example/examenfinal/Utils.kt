package com.example.examenfinal

import android.content.Context
import android.util.Patterns


fun validateEmail(email: String, context: Context): Pair<Boolean, String> {
    return when {
        email.isEmpty() -> Pair(false, context.getString(R.string.validation_email_empty))
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            Pair(false, context.getString(R.string.validation_email_invalid))
        else -> Pair(true, "")
    }
}

fun validatePassword(password: String, context: Context): Pair<Boolean, String> {
    return when {
        password.isEmpty() -> Pair(false, context.getString(R.string.validation_password_empty))
        password.length < 6 -> Pair(false, context.getString(R.string.validation_password_short))
        else -> Pair(true, "")
    }
}