package com.example.admin.calendarioestudiante;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class cursosProfesorTest {

    private String periodo;
    private String anno;
    private int periodoI;
    private int annoI;


    @Rule
    public ActivityTestRule<cursosProfesor> testCursosProfe = new ActivityTestRule<>(cursosProfesor.class);


    @Before
    public void setUp() throws Exception {

        periodo = "2";
        anno = "2019";
        periodoI = 2;
        annoI = 2019;

    }

    @Test
    public void casoModificarCursoStringString() {

    }

    @Test
    public void casoModificarCursoStringInteger() {
    }


    @Test
    public void casoModificarCursoIntegerString() {
    }


    @Test
    public void casoModificarCursoIntegerInteger() {

    }
}