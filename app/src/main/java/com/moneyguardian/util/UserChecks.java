package com.moneyguardian.util;

import com.google.firebase.auth.FirebaseAuth;

public class UserChecks {

    private FirebaseAuth auth;

    public boolean checkUser(String userID){
        auth = FirebaseAuth.getInstance();

        return auth.getCurrentUser().getUid().equals(userID);
    }

}
