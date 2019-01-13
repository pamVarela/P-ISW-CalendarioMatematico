package com.example.admin.calendarioestudiante.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class noPresionarBotonPadre extends android.support.v7.widget.AppCompatImageButton {

    public noPresionarBotonPadre(Context context) {
        super(context);
    }

    public noPresionarBotonPadre(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public noPresionarBotonPadre(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
}
