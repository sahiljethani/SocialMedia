package com.example.socialmedia.Fragments;

import android.os.Bundle;

public interface FragmentActionListener {

    String ACTION_KEY = "action_key";
    int ACTION_VALUE_USER_SELECTED = 1;
    int ACTION_VALUE_FOLLOW_ACTIVITY_SELECTED=2;

    String KEY_SELECTED_USERID="KEY_SELECTED_USERID";
    String KEY_SELECTED_ACTION="KEY_SELECTED_ACTION";


    void onActionPerformed(Bundle bundle);


}
