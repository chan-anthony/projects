/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */
package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing the disk object. This is created by the new disk.
 * Even though the disk has only one instance at all times in the program, we do this so that when a newDisk is created,
 * we can conveniently "wipe" all previous data and assign a new working directory.
 */
public class Disk {
    /**
     * Stores the current disk's total size.
     */
    private int diskSize;
    private Directory curDirectory;
    private List<Directory> ListOfDir = new ArrayList<Directory>();
    /**
     * Stores a list of criterion objects created.
     */
    private List<Criterion> ListOfCriterion = new ArrayList<Criterion>();

    /**
     * REQ1 - Reset everything and create a new disk.
     *
     * @param diskSize - Get the specified disk size to be created in the new disk object.
     */

    public Disk(int diskSize) {
        this.diskSize = diskSize;

        if (getCurDirectory() == null) {
            setCurDirectory(newDir("Root", null));;
        }
        if (getCurDirectory() != null) {


            /*
             IMPORTANT:
             We supressed the "assignment to null" checking for this assignment because this is a special case.
             In our program, the assignment of null is very important for our methods to check if the current parent directory is a ROOT DIRECTORY.
             When the parent directory pointer points to null, we deem this as the ROOT DIRECTORY.
             */

            //noinspection AssignmentToNull
            getCurDirectory().changeParentDir(null); //Make current directory as root directory.
        }
        System.out.println("A new Disk size of "+diskSize+" had been created.");
    }


    /**
     * REQ2 - Create a new directory object
     *
     * @param dirName - Get the directory name of the new directory.
     * @param parentDir - Get the parent directory (usually the working directory) of the new directory to be created.
     * @return - Returns the newly created directory object.
     */
    public Directory newDir(String dirName, Directory parentDir) {
        // Check whether it exceed the diskSize or not if user create a new dir.
        int allDirSize = 0;
        final int SIZE_CONSTANT = 40;
        for(int i = 0; i < getListOfDir().size(); i++) {
            allDirSize += getListOfDir().get(i).getSize();
        }
        if(allDirSize + SIZE_CONSTANT > diskSize)
            throw new IllegalArgumentException();

        boolean isValidOperation = true;
        Directory tempDir;

        if (curDirectory != null) {
            for (int i = 0; i < curDirectory.getContainedFile().size(); i++) {
                //System.out.print("running "+this.containedFile.size());
                if (curDirectory.getContainedFile().get(i).getDocName().equals(dirName)) {
                    System.out.println("Error: A document with the same name exists in this directory! Please choose a new name!");
                    isValidOperation = false;
                }
            }
        }

        for(int i = 0; i< getListOfDir().size(); i++){
            if (getListOfDir().get(i).getParentDir() == getCurDirectory())
                if (getListOfDir().get(i).getDirName().equals(dirName)) {
                    System.out.println("Error: A folder with the same name exists in the current directory. Please use a new name!");
                    isValidOperation = false;
                }
        }
        if (isValidOperation) {
            tempDir = new Directory(dirName, parentDir);
            getListOfDir().add(tempDir);
            if(parentDir!=null)
            System.out.println("A new Directory ("+dirName+") under ("+parentDir.getDirName()+") is created");
        }
        else {
            return null;
        }
        return tempDir;
    }

    /**
     * REQ6 - Change the current working directory.
     * @param dirName - The name of the directory to change/point to.
     */
    public void changeDir(String dirName){
        boolean isValidOperation = true;
        boolean doneFlag = false;
        if(dirName.equals("..") && (getCurDirectory().getParentDir()!=null)){
            setCurDirectory(getCurDirectory().getParentDir());
        }
        else if(dirName.equals("..") && (getCurDirectory().getParentDir()==null)){
            System.out.println("Error: This is the root directory and has no previous/root directory.");
        }
        else if (!(dirName.equals(".."))){
            for(int i = 0; i< getListOfDir().size(); i++){
                if(getListOfDir().get(i).getDirName().equals(dirName) && ((getListOfDir().get(i).getParentDir()) == getCurDirectory())){
                    setCurDirectory(getListOfDir().get(i));
                    System.out.println("Now is under the directory of: "+dirName);
                    doneFlag = true;
                    break;
                }
            }
        }
        if (!doneFlag)
            System.out.println("Invalid input - The inputted directory not found! ");

    }

    /**
     * REQ9 -
     * Construct a new criterion object, append to array list for criterions.
     * First one is for string, second one is for integer. Val types are varied to attrName and should be determined in the switch case in CVFS.
     * @param criName - The criterion name
     * @param attrName - The attribute name
     * @param op - The operator
     * @param val - The value (for string inputs required by attrName)
     */
    public void newSimpleCri(String criName, String attrName, String op, String val) {
        // Make sure criName is unique
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if(ListOfCriterion.get(i).getCriName().equals(criName))
                throw new IllegalArgumentException();
        }

        Criterion newCriterion = new Criterion(criName, attrName, op, val);
        ListOfCriterion.add(newCriterion);
    }

    /**
     * REQ9 -
     * Similar to above, but val is int. The CVFS decides the class of parameter according to the attrName.
     *
     * @param criName - As above
     * @param attrName - As above
     * @param op - As above
     * @param val - As above
     */
    public void newSimpleCri(String criName, String attrName, String op, int val) {
        // Make sure criName is unique
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if(ListOfCriterion.get(i).getCriName().equals(criName))
                throw new IllegalArgumentException();
        }

        Criterion newCriterion = new Criterion(criName, attrName, op, val);
        ListOfCriterion.add(newCriterion);
    }

    /**
     * A reserved simple criterion constructor for IsDocument only. When user constructs a new simple cri in CVFS and specified the name as IsDocument,
     * this method will be called.
     *
     * @param criName - The criName (should be IsDocument)
     */
    public void newSimpleCri(String criName) {
        // Make sure criName is unique
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if(ListOfCriterion.get(i).getCriName().equals(criName))
                throw new IllegalArgumentException();
        }

        Criterion newCriterion = new Criterion(criName);
        ListOfCriterion.add(newCriterion);
    }


    /**
     * REQ11 - Create a newNegation composite criterion
     * @param criName1 - The name of the criterion object to be assigned.
     * @param criName2 - The name of the criterion object to be negated and assigned to criName1.
     */
    public void newNegation(String criName1, String criName2){
        Criterion cri1 = null;
        Criterion cri2 = null;

        if(criName1.length() > 2){
            System.out.println("Error: Only 2 characters are allowed in the name. Please choose a new name.");
            return;
        }

        //Find cri1 and cri2
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if (ListOfCriterion.get(i).getCriName().equals(criName1)) {
                System.out.println("Error: The name already exists. Please choose a new criterion name.");
                return;
            }

            if (ListOfCriterion.get(i).getCriName().equals(criName2))
                cri2 = ListOfCriterion.get(i);

        }




        if (cri2.getAttrName().equals("name")) {
            ListOfCriterion.add(new CompositeCriterion(criName1, cri2.getAttrName(), cri2.getOp(), cri2.getVal(), true));
        }

        else if (cri2.getAttrName().equals("type")) {
            ListOfCriterion.add(new CompositeCriterion(criName1, cri2.getAttrName(), cri2.getOp(), cri2.getVal(), true));
        }



        else if (cri2.getAttrName().equals("size")) {
            String newAttrName = cri2.getAttrName();
            String newOp = "";


            int newVal = Integer.parseInt((String)(cri2.getVal()));

            System.out.println("Old Cri2: " + cri2.getOp());

            if (cri2.getOp().equals(">")) {
                newOp = "<=";
            }
            else if (cri2.getOp().equals("<")) {
                newOp = ">=";
            }
            else if (cri2.getOp().equals(">=")) {
                newOp = "<";
            }
            else if (cri2.getOp().equals("<=")) {
                newOp = ">=";
            }
            else if (cri2.getOp().equals("==")) {
                newOp = "!=";
            }
            else if (cri2.getOp().equals("!=")) {
                newOp = "==";
            }

            System.out.println("Cri2: " + newOp);


            ListOfCriterion.add(new CompositeCriterion(criName1, newAttrName, newOp, newVal, false));

        }

    }

    /**
     * REQ11 - Create a composite criterion for binary criterions.
     *
     * @param criName1 - The criterion object's name to be assigned
     * @param criName3 - One of the criterion object's name to evaluate
     * @param LogicOp - The logic operator
     * @param criName4 - One of the criterion object's name to evaluate
     */
    public void newBinaryCri(String criName1, String criName3, String LogicOp, String criName4) {
        Criterion cri1 = null;
        Criterion cri3 = null;
        Criterion cri4 = null;

        if(criName1.length() > 2){
            System.out.println("Error: Only 2 characters are allowed in the name. Please choose a new name.");
            return;
        }
        //Find cri1, cri3, and cri4
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if (ListOfCriterion.get(i).getCriName().equals(cri1)) {
                System.out.println("Error: The name already exists. Please choose a new criterion name.");
                return;
            }

            if (ListOfCriterion.get(i).getCriName().equals(cri3))
                cri3 = ListOfCriterion.get(i);
            else if (ListOfCriterion.get(i).getCriName().equals(cri4))
                cri4 = ListOfCriterion.get(i);
        }

        cri1 = new CompositeCriterion(criName1, null, null, null, cri3, LogicOp, cri4);
        ListOfCriterion.add(cri1);

    }

    /**
     * REQ12 - Print all criteria
     */
    public void printAllCriteria(){
        final int TITLE_ONE_LENGTH = 15;
        final int TITLE_TWO_LENGTH = 12;
        final int TITLE_THREE_LENGTH = 14;
        final int TITLE_FOUR_LENGTH = 10;
        Table newTable = new Table("attrName", TITLE_ONE_LENGTH, "op", TITLE_TWO_LENGTH, "val", TITLE_THREE_LENGTH, "logicOp", TITLE_FOUR_LENGTH);
        String curAttrName;
        String curOp;
        String curVal;
        String curLogicOp;
        for(int i = 0; i < ListOfCriterion.size(); i++) {
            curAttrName = ListOfCriterion.get(i).getAttrName();
            curOp = ListOfCriterion.get(i).getOp();
            if(ListOfCriterion.get(i).getVal() == null) {
                curVal = "null";
            } else {
                curVal = ListOfCriterion.get(i).getVal().toString();
            }
            if(ListOfCriterion.get(i).getClass() == CompositeCriterion.class) {
                curLogicOp = ListOfCriterion.get(i).getLogicOP();
            }
            else {
                curLogicOp = "null";
            }


            newTable.add(curAttrName, curOp, curVal, curLogicOp);
        }
        newTable.printAll();
    }

    /**
     *REQ13
     *List all files in the working directory according to the criterion. Show the total number and size of files listed too.
     *Output final result in the type Table.
     * @param targetedDri - The directory object to scan.
     * @param criName - The criterion name to be referenced for searching
     * @return - Returns a table object to output in format.
     */
    public Table search(Directory targetedDri, String criName){
        Criterion currentCri = searchForCri(criName);

        // It is composite criterion And it has CriThree and CriFour
        if(currentCri.getClass() == CompositeCriterion.class && (currentCri.getCriThree() != null || currentCri.getCriFour() != null)) {
            Criterion criThree = currentCri.getCriThree();
            Criterion criFour = currentCri.getCriFour();

            Table newTableOne = searchFilesByCri(targetedDri, criThree);
            Table newTableTwo = searchFilesByCri(targetedDri, criFour);
            List tableOneRows = newTableOne.getRows();
            List tableTwoRows = newTableTwo.getRows();
            Table finalTable = new Table("Name", 10, "Type", 10, "Size", 6);

            // If logicOp is "&&"
            if(currentCri.getLogicOP().equals("&&")) {
                for(int i = 0; i < tableOneRows.size(); i++) {
                    for(int m = 0; m < tableTwoRows.size(); m++) {
                        String tempRowFromTableOne = tableOneRows.get(i).toString();
                        String tempRowFromTableTwo = tableTwoRows.get(i).toString();
                        // If finding rows is equals, put it to finalTable
                        if(tempRowFromTableOne.equals(tempRowFromTableTwo)) {
                            finalTable.addOneRow(tempRowFromTableOne);
                        }
                    }
                }
            }
            // If logicOp is "||"
            else if (currentCri.getLogicOP().equals("||")) {
                // First put all the row from newTableOne into finalTable
                for (int i = 0; i < tableOneRows.size(); i++) {
                    String tempRowFromTableOne = tableOneRows.get(i).toString();
                    finalTable.addOneRow(tempRowFromTableOne);
                }
                // Second if finding the rows from newTableTwo != newTableOne. Add it also.
                for(int i = 0; i < tableOneRows.size(); i++) {
                    for(int m = 0; m < tableTwoRows.size(); m++) {
                        String tempRowFromTableOne = tableOneRows.get(i).toString();
                        String tempRowFromTableTwo = tableTwoRows.get(i).toString();
                        // If finding rows is not equals, put it to finalTable
                        if(!tempRowFromTableOne.equals(tempRowFromTableTwo)) {
                            finalTable.addOneRow(tempRowFromTableOne);
                        }
                    }
                }
            }
            else {
                throw new IllegalArgumentException();
            }
            return finalTable;

        }
        // It is simple criterion or It is negation criterion
        else {
            Table newTable = searchFilesByCri(targetedDri, currentCri);
            return newTable;
        }

    }

    /**
     * Finds the criterion object for REQ13
     * @param criName - Get the criName
     * @return - Returns the criterion object
     */
    private Criterion searchForCri(String criName) {
        // Find the position of our targeted criterion
        int positionOfCri = 0;
        for (int i = 0; i < ListOfCriterion.size(); i++) {
            if (ListOfCriterion.get(i).getCriName().equals(criName)) {
                positionOfCri = i;
                break;
            }
        }
        return ListOfCriterion.get(positionOfCri);
    }

    /**
     * Used by search() method.
     * @param targetedDri - The target directory to serach
     * @param currentCri - The criterion object to reference to
     * @return - Returns the table object for printing.
     */
    private Table searchFilesByCri(Directory targetedDri, Criterion currentCri) {
        // Initialise all attributes for Criterion
        String currentCriName = currentCri.getCriName();
        String currentAttrName = currentCri.getAttrName();
        String currentOp = currentCri.getOp();
        String currentVal;
        boolean IsNegation = currentCri.isNegation();
        if (currentCri.getVal() == null) {
            currentVal = "null";
        } else {
            currentVal = currentCri.getVal().toString();
        }
        final int TITLE_THREE_LENGTH = 11;
        Table newTable = new Table("Name", 10, "Type", 10, "Size", TITLE_THREE_LENGTH, "", 6);

        // Initialise all attributes for Files
        List<Document> files = targetedDri.getContainedFile();                     // files in current directory. Type:ArrayList
        String tempDocName;
        String tempDocType;
        String tempDocSize;
        String tempDirName;
        String tempDirSize;
        boolean isDocumentOrNot;

        // If criName is "IsDocument"
        if (currentCriName.equals("IsDocument")) {
            // Find the type which is  txt, java, html, and css. Then print out
            if (!IsNegation) {
                for (int i = 0; i < files.size(); i++) {
                    tempDocName = files.get(i).getDocName();
                    tempDocType = files.get(i).getDocType();
                    tempDocSize = "" + files.get(i).getDocSize();
                    isDocumentOrNot = tempDocType.equals("txt") || tempDocType.equals("java") || tempDocType.equals("html") || tempDocType.equals("css");
                    // System.out.println(tempDocName);
                    if (isDocumentOrNot) {
                        newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                    }
                }
            }
            // // Find the type which is not txt, java, html, and css. Then print out
            else {
                for (int i = 0; i < files.size(); i++) {
                    tempDocName = files.get(i).getDocName();
                    tempDocType = files.get(i).getDocType();
                    tempDocSize = "" + files.get(i).getDocSize();
                    isDocumentOrNot = tempDocType.equals("txt") || tempDocType.equals("java") || tempDocType.equals("html") || tempDocType.equals("css");
                    if (!isDocumentOrNot) {
                        newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                    }
                }
            }
        }
        // criName is not "IsDocument". Then AttrName would be name, type, size.
        else {
            String targetedString = currentVal;
            switch (currentAttrName) {
                case "name":
                    // Find the file name (including dir and doc) which contains "val". i.e. If targetedString is contained in "val" , print out.
                    if (!IsNegation) {
                        // Add Dir
                        String nameOfCurDir = targetedDri.getDirName();
                        String DirSize = "" + targetedDri.getSize();
                        if (nameOfCurDir.contains(targetedString)) {
                            newTable.add(nameOfCurDir, "Null", DirSize, "bytes");
                        }

                        // Add document
                        for (int i = 0; i < files.size(); i++) {
                            tempDocName = files.get(i).getDocName();
                            tempDocType = files.get(i).getDocType();
                            tempDocSize = "" + files.get(i).getDocSize();
                            // System.out.println(tempDocName);
                            if (tempDocName.contains(targetedString)) {
                                newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                            }
                        }
                    }
                    // Find the name which is negation of "val". i.e. If targetedString is NOT contained in "val", print out.
                    else {
                        for (int i = 0; i < files.size(); i++) {
                            tempDocName = files.get(i).getDocName();
                            tempDocType = files.get(i).getDocType();
                            tempDocSize = "" + files.get(i).getDocSize();
                            if (!tempDocName.contains(targetedString)) {
                                newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                            }
                        }
                    }
                case "type":
                    // Find the name which contains "val". i.e. If "val" == targetedString, print out.
                    if (!IsNegation) {
                        for (int i = 0; i < files.size(); i++) {
                            tempDocName = files.get(i).getDocName();
                            tempDocType = files.get(i).getDocType();
                            tempDocSize = "" + files.get(i).getDocSize();
                            // System.out.println(tempDocName);
                            if (tempDocType.equals(targetedString)) {
                                newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                            }
                        }
                    }
                    // Find the name which is negation of "val". i.e. If "val" != targetedString, print out.
                    else {
                        for (int i = 0; i < files.size(); i++) {
                            tempDocName = files.get(i).getDocName();
                            tempDocType = files.get(i).getDocType();
                            tempDocSize = "" + files.get(i).getDocSize();
                            if (!tempDocType.equals(targetedString)) {
                                newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                            }
                        }
                    }
                    break;

                case "size":
                    int fileSize;
                    int valSize = Integer.parseInt(currentVal);

                    // Add Dir
                    String nameOfCurDir = targetedDri.getDirName();
                    String fileSizeInString = "" + targetedDri.getSize();
                    fileSize = targetedDri.getSize();

                    switch (currentOp) {
                        case ">":
                            if (fileSize > valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                            break;
                        case "<":
                            if (fileSize < valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                            break;
                        case ">=":
                            if (fileSize >= valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                            break;
                        case "<=":
                            if (fileSize <= valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                            break;
                        case "==":
                            if (fileSize == valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                            break;
                        case "!=":
                            if (fileSize != valSize) {
                                newTable.add(nameOfCurDir, "Null", fileSizeInString, "bytes");
                            }
                    }

                    // Adding Doc
                    for (int i = 0; i < files.size(); i++) {
                        fileSize = files.get(i).getDocSize();              // File size in int
                        tempDocName = files.get(i).getDocName();
                        tempDocType = files.get(i).getDocType();
                        tempDocSize = "" + files.get(i).getDocSize();      // File size in String, used for adding into table

                        switch (currentOp) {
                            case ">":
                                if (fileSize > valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                                break;
                            case "<":
                                if (fileSize < valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                                break;
                            case ">=":
                                if (fileSize >= valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                                break;
                            case "<=":
                                if (fileSize <= valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                                break;
                            case "==":
                                if (fileSize == valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                                break;
                            case "!=":
                                if (fileSize != valSize) {
                                    newTable.add(tempDocName, tempDocType, tempDocSize, "bytes");
                                }
                        }
                    }
            }
        }
        return newTable;
    }

    /**
     * REQ14
     * @param targetedDri - The targeted directory to search
     * @param criName - The criterion name to reference to when searching
     * @return - Returns a table object for printing.
     */
    public Table rSearch(Directory targetedDri, String criName) {
        int numberOfDirectories = getListOfDir().size();
        final int TITLE_THREE_LENGTH = 11;
        // Table which from The kid of targetedDri directory
        Table newTable = new Table("Name", 10, "Type", 10, "Size", TITLE_THREE_LENGTH, "", 6);
        for(int i = 0; i < numberOfDirectories; i++) {
            Directory tempDir = getListOfDir().get(i);               // Directory which the loop is pointing to.
            Directory tempParDir;
            // If null, it means it is the root dir. Ignore it.
            try {
                tempParDir = tempDir.getParentDir();    // Parent of Directory which the loop is pointing to.
                // System.out.println(tempParDir.equals(targetedDri));
                if(tempParDir.equals(targetedDri)) {
                    // newTable.combine(search(tempDir, criName));
                    newTable.combine(rSearch(tempDir, criName));
                }
            } catch (NullPointerException ignored) { }
        }
        newTable.combine(search(targetedDri, criName));
        return newTable;
    }

    /**
     * Used by REQ13 and 14 - Print out the output based on the table object.
     * @param newTable - The table object to print out in a format.
     */
    public void printOutputOfSearch(Table newTable) {
        newTable.printAll();

        int totalNumberOfDocument = newTable.getRows().size();
        int totalSize = 0;
        for(int i = 0; i < totalNumberOfDocument; i++) {
            totalSize += Integer.parseInt(newTable.getAColumn(2).get(i).toString());
        }
        System.out.println("");
        System.out.printf("%d file(s) ----- %d bytes%n", totalNumberOfDocument, totalSize);
    }

    /**
     * Remembers the current directory's object. Will be changed/updated by changeDir constantly.
     * @return Returns the current directory object.
     */
    public Directory getCurDirectory() {
        return curDirectory;
    }

    /**
     * Sets the current directory
     * @param curDirectory - Reference point
     */
    public void setCurDirectory(Directory curDirectory) {
        this.curDirectory = curDirectory;
    }

    /**
     * Stores a list of directory objects created. There is no "sorting" in this array list.
     * Order is by the order of creation.
     *
     * @return Returns the list of directory.
     */
    public List<Directory> getListOfDir() {
        return ListOfDir;
    }

    /** Return List Of Criterion in type List
     *
     * @return - List Of Criterions
     */
    public List<Criterion> getListOfCriterion() {
        return ListOfCriterion;
    }

}
