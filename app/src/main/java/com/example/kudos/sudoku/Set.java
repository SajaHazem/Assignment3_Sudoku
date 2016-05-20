package com.example.kudos.sudoku;

/**
 * Created by kudos on 5/20/2016.
 */
public class Set {
    int rows = 9 ;
    int sets[][];
	/*
	 * set contain the coordination of x and y in the set so it size is [row][row*2]
	 */


    public Set() {
        sets = new int [rows][rows*2];
        int row=0, col =0, LastI=0 ;
        for (int i=0 ; i <rows ; i++){
            //determine the value of row and col
            col = (i%3) *3;
            if (i%3 == 0 && i!=0 )
                LastI = i ;
            row = LastI;

            for (int j=0,Jcounter=0 ; j<(rows*2) ; j+=2,Jcounter++){
                // Jcounter == 3 means that
                if (Jcounter == 3 ){
                    row ++;
                    Jcounter = 0;
                    col = (i%3) *3;
                }
                //fill
                sets[i][j] = row;
                sets[i][j+1] = col;
                col ++ ;
            }
        }

    }
}
