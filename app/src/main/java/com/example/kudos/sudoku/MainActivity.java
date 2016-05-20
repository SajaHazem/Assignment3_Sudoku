package com.example.kudos.sudoku;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    Button submit;
    EditText textMatrix[][];
    TextView  text ,timeElapsedView;
    int rows = 9;
    boolean Generated[][];
    int result[][];
    private int ranodm;
    private MalibuCountDownTimer countDownTimer;
    private final long startTime = 50000;
    private final long interval = 1000;
    private long timeElapsed;
    private boolean timerHasStarted = false;

    public void startGame(String Path) throws IOException, NoSuchElementException, URISyntaxException {
        AssetManager assetManager = getAssets();
        String[] files = assetManager.list("");
        InputStream is = assetManager.open("Sudoku.txt");
        InputStreamReader isr = new InputStreamReader(is);
        Scanner input = new Scanner(isr);


        //generate random .. randomly read one of the games from the file
        ranodm = (int) (Math.random() * 100) % 8;
        textMatrix = new EditText[rows][rows];
        Generated = new boolean[rows][rows];
        result = new int[rows][rows];

//         get rid of previous lines
		for (int i=0; i< (9*ranodm + ranodm -1) ; i++)
			input.nextLine();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < rows; j++) {
                // fill matrix
                int nextInt = input.nextInt();

                if (nextInt >= 10 || nextInt < 0)
                    throw new NoSuchElementException();
                int resourceId = getResources().getIdentifier("id"+i+""+(j+1), "id", getPackageName());
                textMatrix[i][j] = (EditText) findViewById(resourceId);
                if (nextInt != 0) {
                    textMatrix[i][j].setText(nextInt + "");
                    textMatrix[i][j].setTextColor(Color.RED);
                    textMatrix[i][j].setEnabled(false);
                    Generated[i][j] = true;
                    result[i][j] = -1; // from the file
                } else {
                    textMatrix[i][j].setText("");
                    result[i][j] = 0;
                }

            }
        input.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suduko_window);
        try {
            startGame("Sudoku.txt");

            text = (TextView) this.findViewById(R.id.timer);
            timeElapsedView = (TextView) this.findViewById(R.id.timeElapsed);
            countDownTimer = new MalibuCountDownTimer(startTime, interval);
            text.setText(text.getText() + String.valueOf(startTime));
            if (!timerHasStarted)
            {
                countDownTimer.start();
                timerHasStarted = true;
//                submit.setText("RESET");
            }
            submit = (Button)findViewById(R.id.button);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean win = checkifDone();

                    if (timerHasStarted && win)
                    {
                        countDownTimer.cancel();
                        timerHasStarted = false;
//                submit.setText("RESET");
                    }
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public  boolean checkifDone(){
        //check if there is any cell empty
        boolean emptyCell = false ;
        for (int i =0 ; i< rows ; i++)
            for (int j =0 ; j<rows ; j++) {
                String s = (textMatrix[i][j].getText()+"");
                if ((textMatrix[i][j].getText()+"").equals("")) {
                    emptyCell = true;
                    textMatrix[i][j].setBackgroundColor(Color.BLUE);
                }
                else
                    textMatrix[i][j].setBackgroundColor(Color.WHITE);
            }
        if (!emptyCell) {
            // all cells are filled, now check if the there is only one number in the row
            boolean rowsComplete[][] = new boolean[rows][rows];
            for (int j =0 ; j < rows; j++)
                for (int i =0 ; i <rows ; i++ ){
                    if (rowsComplete[j][Integer.parseInt(""+textMatrix[j][i].getText())-1] == false )// First occurrence
                        rowsComplete[j][Integer.parseInt(""+textMatrix[j][i].getText())-1] = true;
                    else {
                        // change background colour for the repeated number
                        for (int k=0 ; k<rows ; k++)
                            if (Integer.parseInt(textMatrix[j][i].getText()+"") == Integer.parseInt(""+textMatrix[j][k].getText())
                                    && Generated[j][k] == false)
                                textMatrix[j][k].setBackgroundColor(Color.YELLOW);
                    }
                }
            // No repeated values
            boolean allTrueRows = true;
            for (int i =0 ; i <rowsComplete.length ; i++)
                for (int j =0 ; j <rowsComplete.length ; j++)
                    if (rowsComplete[i][j] == false )// one item is repeated
                        allTrueRows = false ;

            if (!allTrueRows){
                Toast.makeText(getApplicationContext(),
                        "There is repeated values ", Toast.LENGTH_SHORT)
                        .show();

            }

            else{
                // success
                for (int j =0 ; j < rows; j++)
                    for (int i =0 ; i <rows ; i++ )
                        if (Generated[i][j] == false ) {
                            //change backGround
                            textMatrix[i][j].setBackgroundColor(Color.CYAN);
                        }
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MainActivity.this);

                // Setting Dialog Title
                alertDialog2.setTitle("You win! ");

                // Setting Dialog Message
                alertDialog2.setMessage("Go Back Home?1 ");

                // Setting Negative "NO" Btn
                alertDialog2.setNegativeButton("Home",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent i = new Intent(MainActivity.this, SudukoWindowActivity.class);
                                startActivity(i);
                            }
                        });

                // Showing Alert Dialog
                alertDialog2.show();

            }
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "There is Empty cells  ", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public class MalibuCountDownTimer extends CountDownTimer
    {

        public MalibuCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            text.setText("Time's up!");
            timeElapsedView.setText("Time Elapsed: " + String.valueOf(startTime));
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            text.setText("Time remain:" + millisUntilFinished);
            timeElapsed = startTime - millisUntilFinished;
            timeElapsedView.setText("Time Elapsed: " + String.valueOf(timeElapsed));
        }
    }

}

