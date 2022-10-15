/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */
package hk.edu.polyu.comp.comp2021.cvfs.model;

/**
 * A subclass of criterion.
 *
 * As implied by the requirements, a criterion object is categorized into two:
 * The simple criterion (the super class) is only created by newSimpleCri
 * The composite criterion (this class) is created by newNegation and newBinaryCri.
 *
 * @param <T> The "val" parameter for the constructor can either be integer or string.
 */
public class CompositeCriterion <T> extends Criterion {
    private boolean isNegation = false;
    private Criterion criThree = null;
    private Criterion criFour = null;
    private String logicOP = null;


    /**
     * newNegation: composite criteria for IsDocument
     *
     * @param criName-  The criterion name
     * @param isNegation - The boolean flag for isNegation
     */
    public CompositeCriterion (String criName, boolean isNegation) {
        super(criName);
        this.isNegation = isNegation;
    }


    /**
     *  newNegation: composite criteria for others
     *
     * @param criName - The criterion name
     * @param attrName - The attribute name
     * @param op - The operator
     * @param val - The value
     * @param isNegation - The boolean flag for isNegation for the search and rSearch command.
     */
    public CompositeCriterion (String criName, String attrName, String op, T val, boolean isNegation) {
        super(criName, attrName, op, val);
        this.isNegation = isNegation;
    }


    /**
     * The constructor for newBinaryCri command.
     *
     * @param criName - The criterion name
     * @param attrName - The attribute name
     * @param op - The operator
     * @param val - The value
     * @param criThree - The object for criName3
     * @param logicOP - The logicOperator for the search/rSearch command
     * @param criFour - The object for criName4
     */
    public CompositeCriterion (String criName, String attrName, String op, T val, Criterion criThree, String logicOP, Criterion criFour) {
        super(criName, attrName, op, val);
        this.criThree = criThree;
        if(!logicOP.equals("&&") && !logicOP.equals("||"))
            throw new IllegalArgumentException();
        this.logicOP = logicOP;
        this.criFour = criFour;
    }

    /**
     *
     * @return Returns the isNegation flag for methods.
     */
    @Override
    public boolean isNegation() {
        return isNegation;
    }

    /**
     *
     * @return Returns the criThree object for the search/rSearch command
     */
    @Override
    public Criterion getCriThree() {
        return criThree;
    }

    /**
     *
     * @return Returns the criFour object for the search/rSearch command
     */
    @Override
    public Criterion getCriFour() {
        return criFour;
    }

    /**
     *
     * @return Returns the logicOP flag for the search/rSearch command.
     */
    @Override
    public String getLogicOP() {
        return logicOP;
    }






}
