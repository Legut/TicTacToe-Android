package com.example.tictactoeandroid;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tictactoeandroid.auth.LoginActivity;

public class UnitTests {
    @Test
    public void dataIsCorrect_isCorrect() {
        String email = "test@test.pl";
        String password = "testjeden";
        assertTrue(LoginActivity.dataIsCorrectStatic(email, password));
    }

    @Test
    public void dataIsCorrect_emailEmpty_isCorrect() {
        String email = "";
        String password = "testjeden";
        assertFalse(LoginActivity.dataIsCorrectStatic(email, password));
    }

    @Test
    public void dataIsCorrect_passwordEmpty_isCorrect() {
        String email = "test@test.pl";
        String password = "";
        assertFalse(LoginActivity.dataIsCorrectStatic(email, password));
    }
}