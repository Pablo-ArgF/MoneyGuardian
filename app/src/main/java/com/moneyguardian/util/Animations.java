package com.moneyguardian.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moneyguardian.R;

import java.util.List;

public class Animations {

    private final Animation toLeft;
    private final Animation toRight;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private List<FloatingActionButton> otherButtons;
    private FloatingActionButton buttonDelete;

    public Animations(View view) {
        rotateOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.to_bottom_amim);
        toLeft = AnimationUtils.loadAnimation(view.getContext(), R.anim.to_left_anim);
        toRight = AnimationUtils.loadAnimation(view.getContext(), R.anim.to_right_anim);
    }

    public void setOnClickAnimationAndVisibility(FloatingActionButton mainButton) {
        mainButton.setOnClickListener(v -> {
            if (otherButtons.get(0).getVisibility() == View.INVISIBLE) {
                for (FloatingActionButton fab : otherButtons) {
                    fab.startAnimation(fromBottom);
                    fab.setVisibility(View.VISIBLE);
                    fab.setClickable(true);
                }
                if (buttonDelete != null && buttonDelete.getVisibility() == View.INVISIBLE) {
                    buttonDelete.startAnimation(toLeft);
                    buttonDelete.setVisibility(View.VISIBLE);
                    buttonDelete.setClickable(true);
                }
                mainButton.startAnimation(rotateOpen);
            } else {
                for (FloatingActionButton fab : otherButtons) {
                    fab.startAnimation(toBottom);
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                }
                if(buttonDelete != null) {
                    buttonDelete.startAnimation(toRight);
                    buttonDelete.setVisibility(View.INVISIBLE);
                    buttonDelete.setClickable(false);
                }
                mainButton.startAnimation(rotateClose);
            }
        });
    }

    public void setButtonDelete(FloatingActionButton buttonDelete) {
        this.buttonDelete = buttonDelete;
    }

    public void setOtherButtons(List<FloatingActionButton> newButtons) {
        this.otherButtons = newButtons;
    }

}
