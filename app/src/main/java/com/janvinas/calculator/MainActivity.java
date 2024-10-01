package com.janvinas.calculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

enum Operation{
    ADDITION,
    SUBTRACTION,
    PRODUCT,
    DIVISION,
    SIN,
    COS,
    TAN,
}

public class MainActivity extends AppCompatActivity {

    EditText text;

    Operation latestOperation;
    Double firstNumber;
    Double result;

    boolean useDegrees = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        text = this.findViewById(R.id.text);
    }

    private void saveOperation(Operation operation){
        latestOperation = operation;
        try{
            if(result != null) {
                firstNumber = result;
                result = null;
            }else{
                firstNumber = Double.parseDouble(text.getText().toString());
            }
        }catch(NumberFormatException ignored){
        }

        text.getText().clear();

    }
    private void computeOperation(){

        if(latestOperation == null){
            return;
        }

        double secondNumber;
        try {
            assert firstNumber != null;
            secondNumber = Double.parseDouble(text.getText().toString());
        }catch(NumberFormatException | AssertionError e){
            text.setText("ERR");
            result = null;
            return;
        }

        switch(latestOperation){
            case ADDITION:
                result = firstNumber + secondNumber;
                break;
            case SUBTRACTION:
                result = firstNumber - secondNumber;
                break;
            case PRODUCT:
                result = firstNumber * secondNumber;
                break;
            case DIVISION:
                result = firstNumber / secondNumber;
                if(result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=oHg5SJYRHA0"));
                    startActivity(browserIntent);
                }
                break;
        }
        printNumber(result);
        latestOperation = null;
    }

    private void printNumber(double d){
        DecimalFormat df = new DecimalFormat("#.#####");
        text.setText(df.format(d));
    }

    private void drawCharacter(char c){
        result = null;
        if(c == 0){
            int length = text.length();
            if(length > 0) {
                text.getText().delete(length - 1, length);
            }
        }else{
            text.getText().append(c);
        }
    }

    private void computeInstantOperation(Operation op){
        double number;
        try{
            number = Double.parseDouble(text.getText().toString());
        }catch(NumberFormatException e){
            text.setText("ERR");
            result = null;
            return;
        }

        if(useDegrees) number *= (Math.PI / 180);
        switch(op){
            case SIN:
                result = Math.sin(number);
                break;
            case COS:
                result = Math.cos(number);
                break;
            case TAN:
                result = Math.tan(number);
                break;

        }
        printNumber(result);
    }

    public void onCharacterClickListener(View v){
        char c = ((String) v.getTag()).charAt(0);
        drawCharacter(c);
    }

    public void onClickListener(View v){
        int id = v.getId();

        if(id == R.id.button_divide){
            saveOperation(Operation.DIVISION);
        }else if(id == R.id.button_product){
            saveOperation(Operation.PRODUCT);
        }else if(id == R.id.button_add){
            saveOperation(Operation.ADDITION);
        }else if(id == R.id.button_subtract){
            saveOperation(Operation.SUBTRACTION);
        }else if(id == R.id.button_equals){
            computeOperation();
        }else if(id == R.id.button_delete){
            drawCharacter((char) 0);
        }else if(id == R.id.button_sin){
            computeInstantOperation(Operation.SIN);
        }else if(id == R.id.button_cos){
            computeInstantOperation(Operation.COS);
        }else if(id == R.id.button_tan){
            computeInstantOperation(Operation.TAN);
        }
    }

    public void onDegreesClick(View v){
        useDegrees = !useDegrees;
        ((Button) v).setText(useDegrees ? "D" : "R");
    }
}