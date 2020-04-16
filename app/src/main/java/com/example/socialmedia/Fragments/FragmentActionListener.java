package com.example.socialmedia.Fragments;

import android.os.Bundle;

public interface FragmentActionListener {

    String ACTION_KEY = "action_key";
    int ACTION_VALUE_USER_SELECTED = 1;

    String KEY_SELECTED_USERID="KEY_SELECTED_USERID";

    void onActionPerformed(Bundle bundle);


}
