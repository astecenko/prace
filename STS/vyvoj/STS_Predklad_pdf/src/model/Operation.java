/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author TPV
 */
public class Operation {
    
    private final String number;
    private final String workplace;
    private final String workplace2;
    private final String description;
    private final float tac;
    private final float tbc;

    public Operation(String number, String workplace,String workplace2, float tac, float tbc, String description) {
        this.number = number;
        this.workplace = workplace;
        this.workplace2 = workplace2;
        this.tac = tac;
        this.tbc = tbc;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public String getWorkplace() {
        return workplace;
    }
    public String getWorkplace2() {
        return workplace2;
    }

    public float getTac() {
        return tac;
    }

    public float getTbc() {
        return tbc;
    }

    public String getDescription() {
        return description;
    }
    
}
