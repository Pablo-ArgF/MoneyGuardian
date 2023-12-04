package com.moneyguardian.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moneyguardian.R;

import java.util.List;

public class Animations {

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    public Animations(View view) {
        rotateOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.to_bottom_amim);
    }

    public void setOnClickAnimationAndVisibility(FloatingActionButton mainButton,
                                                 List<FloatingActionButton> otherButtons) {
        mainButton.setOnClickListener(v -> {
            if (otherButtons.get(0).getVisibility() == View.INVISIBLE) {
                for (FloatingActionButton fab : otherButtons){
                    fab.startAnimation(fromBottom);
                    fab.setVisibility(View.VISIBLE);
                    fab.setClickable(true);
                }
                mainButton.startAnimation(rotateOpen);
            } else {
                for (FloatingActionButton fab : otherButtons){
                    fab.startAnimation(toBottom);
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                }
                mainButton.startAnimation(rotateClose);
            }
        });
    }

}
