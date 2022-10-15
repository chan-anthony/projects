/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */
package hk.edu.polyu.comp.comp2021.cvfs;

import hk.edu.polyu.comp.comp2021.cvfs.model.CVFS;

/**
 * This is a Javadoc
 */
public class Application {

    /**
     * Extracts the user's name from the input arguments.
     *
     * Precondition: 'args' should contain at least one element, the user's name.
     *
     * @param  args            the command-line arguments.
     */

    public static void main(String[] args){

        // Initialize and utilize the system
        CVFS cvfs = new CVFS();
        cvfs.mainRunner();
    }
}
