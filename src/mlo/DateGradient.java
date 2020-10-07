/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mlo;

import java.util.Arrays;

/**
 *
 * @author Eli
 */
public class DateGradient {
    
    String date;
    Double[] gradients = new Double[14];
    
    public DateGradient() {
        Arrays.fill(gradients, null);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double[] getGradients() {
        return gradients;
    }

    public void setGradients(Double[] gradients) {
        this.gradients = gradients;
    }
    
    public void setGradientAt(Double gradient, int index) {
        gradients[index] = gradient;
    }
}
