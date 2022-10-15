/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */
package hk.edu.polyu.comp.comp2021.cvfs.model;

/**
 * The class for criterion objects that is used for the search/rSearch command show show
 * search results according to user-defined criterias in command newSimpleCriteria.
 *
 * Criterion classes are categorised into two in this problem - Simple Criterions and Composite Criterions.
 * Simple Criterions (this class) are created by command newSimpleCri. The other class is a subclass of this class
 * (in CompositeCrterion.java).
 *
 * @param <T> - The constructor of this class allows integer and string "val" parameters, according to the specified attrName.
 */
public class Criterion<T> {
    private String criName;
    private String attrName;
    private String op;
    private T val;


    /**
     * REQ9 - Creating a criterion object
     * *
     * @param criName - The criterion name
     * @param attrName - The attribute name
     * @param op - The operator
     * @param val - The value - can either be integer or string according to attrName.
     */
    public Criterion(String criName, String attrName, String op, T val) {
        this.criName = criName;
        this.attrName = attrName;
        this.op = op;
        this.val = val;
    }



     /** REQ10 -  a constructor for Criterion name: IsDocument
      *
      * @param criName - The criterion name
      */
    public Criterion(String criName) {
        if(!criName.equals("IsDocument"))
            throw new IllegalArgumentException();
        this.criName = criName;
        // this.attrName = null;
        //this.op = null;
        // this.val = null;
    }

    /**
     *
     * @return Returns the criName for methods
     */
    public String getCriName() {
        return criName;
    }

    /**
     *
     * @return Returns the attrName for methods
     */
    public String getAttrName() {
        return attrName;
    }

    /**
     *
     *
     * @return Returns the operator for methods
     */
    public String getOp() {
        return op;
    }

    //Methods for composite criterion:

    /**
     *
     * @return Returns the value for methods
     */
    public T getVal() {
        return val;
    }

    /**
     *
     * @return Reference point
     */
    public String getLogicOP() {
        return null;}


    /**
     *
     * @return Reference Point
     */
    public Criterion getCriThree() {
        return null;
    }

    /**
     *
     * @return Reference point
     */
    public Criterion getCriFour() {
        return null;
    }

    /**
     *
     * @return Reference point
     */
    public boolean isNegation() {
        return false;
    }



}
