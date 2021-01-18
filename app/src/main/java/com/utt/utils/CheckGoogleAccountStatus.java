package com.utt.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class CheckGoogleAccountStatus {
    public static boolean  getcheckDataAccount(Context context){
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(context);
        if (gg!=null){
            return true;
        }else{
            return false;
        }
    }
}