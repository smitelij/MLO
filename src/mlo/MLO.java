/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mlo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eli
 */
public class MLO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String inputFilename;
        String outputFilename;
        
        if (args[0] == null) {
            inputFilename = "C:\\Users\\Eli\\Downloads\\water-temp.csv";
        } else {
            inputFilename = args[0];
        }
        
        if (args[1] == null) {
            outputFilename = "C:\\Users\\Eli\\Downloads\\gradients.csv";
        } else {
            outputFilename = args[1];
        }
        
        List<String> mixDates = new ArrayList<>();
        List<DateGradient> gradients = new ArrayList<>();
        
        List<Double> validDiffs = new ArrayList<>();
        
        int validLines = 0;
        int invalidLines = 0;
        
        //int mixed = 0;
        //int stratified = 0;
        
        // Read in data
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(inputFilename));
            StringBuilder sb = new StringBuilder();
            
            String line = br.readLine();
            
            while(line != null) {
                line = br.readLine();

                String[] outcomes = line.split(",");
                
                DateGradient dateGradient = new DateGradient();
                
                dateGradient.setDate(outcomes[0]);

                try {
                   Double oneM = null;
                   Double twoM = null;
                   Double tenM = null;
                   Double elevenM = null;

                    if (outcomes.length > 1) {
                        if (outcomes[1].length() > 0) {
                            oneM = Double.parseDouble(outcomes[1]);
                        }
                        if (outcomes[2].length() > 0) {
                            twoM = Double.parseDouble(outcomes[2]);
                        }
                        if (outcomes.length > 7 && outcomes[7].length() > 0) {
                            tenM = Double.parseDouble(outcomes[7]);
                        }
                        if (outcomes.length > 8 && outcomes[8].length() > 0) {
                            elevenM = Double.parseDouble(outcomes[8]);
                        }
                    }
                    
                    //Use four meter depth if one or two is missing
                    if ((oneM == null) ^ (twoM == null)) {
                        if (outcomes[3].length() > 0 ){
                            if (oneM == null) {
                                oneM = Double.parseDouble(outcomes[3]);
                            }
                            if (twoM == null) {
                                twoM = Double.parseDouble(outcomes[3]);
                            }
                        }
                    }
                    
                    if (oneM != null && twoM != null && tenM != null && elevenM != null) {
                        validLines++;
                        
                        Double topAvg = (oneM + twoM) / 2;
                        Double bottomAvg = (tenM + elevenM) / 2;
                        Double diff = topAvg - bottomAvg;
                        
                        validDiffs.add(diff);
                        
                        //Make sure we have at least 3 hours of past records
                        if (validDiffs.size() > 12) {

                            
                            //Start at 3 hours
                            int start = 12;
                            
                            //And go up to 17 hours
                            while (start < 68 && (start < validDiffs.size())) {
                                
                                int priorIndex = validDiffs.size() - start;
                                Double priorMeasurement = validDiffs.get(priorIndex);
                                
                                double hoursElapsed = start / 4.0;
                                double gradient = ((priorMeasurement - diff) / hoursElapsed);
                                
                                if (start % 4 == 0) {
                                    int hourIndex = (start / 4) - 3;
                                    dateGradient.setGradientAt(gradient, hourIndex);
                                }
                            
                                if (gradient > 1.5) {
                                    mixDates.add(outcomes[0]);
                                    start = 100;
                                }
                                start++;
                            }

                            

                        }

                    } else {
                        validDiffs = new ArrayList<>();
                        invalidLines++;
                    }

                } catch (Exception e) {
                    invalidLines++;
                    System.out.println("Unknown exception... ");
                    System.out.println(line);
                    e.printStackTrace();
                }
                
                gradients.add(dateGradient);
                
            }
            
        } catch (Exception e) {
        }
        System.out.println("   TOTAL VALID: " + validLines);
        System.out.println("   TOTAL INVALID: " + invalidLines);
        
        //System.out.println(" TOTAL MIXED: " + mixed);
        //System.out.println(" TOTAL STRATIFIED: " + stratified);
        
        System.out.println(" TOTAL MIX DATES: " + mixDates.size());
        
        try {
            /*
            FileWriter myWriter = new FileWriter("C:\\Users\\Eli\\Downloads\\mixdates.txt");
            
            for (String date : mixDates) {
                myWriter.write(date + "\n");
            }*/
            
            FileWriter myWriter = new FileWriter(outputFilename);
            
            for (DateGradient dateGradient : gradients) {
                
                String dateGradString = dateGradient.getDate();
                for (int j = 0; j < 14; j++) {
                    if (dateGradient.getGradients()[j] != null) {
                        dateGradString = dateGradString + "," + dateGradient.getGradients()[j];
                    } else {
                        dateGradString = dateGradString + ",";
                    }
                }
                myWriter.write(dateGradString + "\n");
            }
            
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
    
}
