/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */

package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing the directory files. This is created by the command newDir
 */
public class Directory {
    /**
     * The directory name
     */
    private String dirName;
    private List<Document> containedFile = new ArrayList<Document>();
    private Directory parentDir;

    /**
     * REQ3 - Constructing the directory object
     *
     * @param dirName - Get directory name
     * @param parentDir - Get the parent directory pointer.
     */
    public Directory(String dirName, Directory parentDir) {
        this.dirName = dirName;
        this.setParentDir(parentDir);

    }

    /**
     *
     * @return - Get the directory name for methods.
     */
    public String getDirName() {
        return this.dirName;
    }

    /**
     *
     * @param input - Change the dirname.
     */
    public void setDirName(String input) {this.dirName = input;}

    /**
     * An array list containing the document objects.
     */ /**
     *
     * @return - Get the containedFile arrayList for methods
     */
    public List<Document> getContainedFile() {
        return this.containedFile;
    }

    /**
     * A directory pointer pointing to this directory's parent directory.
     */ /**
     *
     * @return - Get the parent directory pointer for methods
     */
    public Directory getParentDir() {
        return this.parentDir;
    }

    /**
     * REQ2 - Create a new document object.
     *
     * @param docName - The document name
     * @param docType - The document type
     * @param docContent - The document content (string)
     * @param currentDisk - The current disk pointer to find directories
     */
    public void newDoc (String docName, String docType, String docContent, Disk currentDisk) {
        boolean isValidOperation = true;
        Document newDocObj;

        for (int i = 0; i < currentDisk.getListOfDir().size(); i++) {
            if (currentDisk.getListOfDir().get(i).getDirName().equals(docName)) {
                System.out.println("Error: A directory with the same name exists in this directory! Please choose a new name!");
                isValidOperation = false;
            }
        }

        for (int i = 0; i < this.getContainedFile().size(); i++) {
            //System.out.print("running "+this.containedFile.size());
            if (this.getContainedFile().get(i).getDocName().equals(docName)) {
                System.out.println("Error: A document with the same name exists in this directory! Please choose a new name!");
                isValidOperation = false;
            }
        }

        if (isValidOperation == true) {
            newDocObj = new Document(docName, docType, docContent);
            getContainedFile().add(newDocObj);
            System.out.println("The new document " + docName + " had been created.");
        }
    }

    /**
     * REQ4 - Delete document from array list.
     *
     *  @param fileName - The file name of the document to delete
     *  @param currentDisk - The current disk pointer to find directories
     */
    public void delete (String fileName, Disk currentDisk) {
        boolean doneFlag = false;

        for (int i = 0; i < currentDisk.getListOfDir().size(); i++) {
            if (currentDisk.getListOfDir().get(i).getParentDir() == this) {
                if (currentDisk.getListOfDir().get(i).getDirName().equals(fileName)) {
                    currentDisk.getListOfDir().remove(i);
                    doneFlag = true;
                    System.out.println("The file "+fileName+" had been deleted.");
                    break;
                }
            }
        }

        for (int i = 0; i < getContainedFile().size(); i++) {
            if (getContainedFile().get(i).getDocName().equals(fileName)) {
                getContainedFile().remove(i);
                doneFlag = true;
                System.out.println("The file "+fileName+" had been deleted.");
                break;
            }
        }
        if(!doneFlag) System.out.println("Error: No document/directory with the same name is found! ");
    }


    /**
     * REQ5 - Renaming this directory
     *
     * @param oldFileName - The target document's file name to rename
     * @param newFileName - The new name
     * @param currentDisk - The current disk pointer to find directories
     */
    public void rename (String oldFileName, String newFileName, Disk currentDisk) {
        boolean isValidOperation = true;
        boolean doneFlag = false;

        for (int i = 0; i < currentDisk.getListOfDir().size(); i++) {
            if (currentDisk.getListOfDir().get(i).getDirName().equals(newFileName)) {
                System.out.println("Error: A directory with the same name exists in this directory! Please choose a new name!");
                isValidOperation = false;
            }
        }

        for (int i = 0; i < this.getContainedFile().size(); i++) {
            //System.out.print("running "+this.containedFile.size());
            if (this.getContainedFile().get(i).getDocName().equals(newFileName)) {
                System.out.println("Error: A document with the same name exists in this directory! Please choose a new name!");
                isValidOperation = false;
            }
        }
        if (isValidOperation) {
            for (int i = 0; i < currentDisk.getListOfDir().size();i++) {
                if (currentDisk.getListOfDir().get(i).getDirName().equals(oldFileName)) {
                    currentDisk.getListOfDir().get(i).setDirName(newFileName);
                    System.out.println("The directory "+oldFileName+" had been renamed to "+newFileName);
                    doneFlag = true;
                }
            }

            for (int i = 0; i < getContainedFile().size(); i++) {
                if (getContainedFile().get(i).getDocName().equals(oldFileName)) {
                    getContainedFile().get(i).changeDocName(newFileName);
                    System.out.println("The file "+oldFileName+" had been renamed to "+newFileName);
                    doneFlag = true;
                }
            }
        }
        if(!doneFlag) System.out.println("Error: No document/directory with the same name is found! ");
    }

    //REQ7 - print everything in array list using getContainedFile(), also the name, type, size of each document, the name of directory, total number of files, total byte size of files

    /**
     * Print a list of stuff.
     *
     * @param currentDisk - The current disk object pointer from CVFS.java
     */
    public void list(Disk currentDisk) {
        int totalSize=0;
        System.out.println("\n\n\nDirectory of "+dirName+"\n");
        System.out.printf("%-10s  %-9s  %-10s ", "File Name", "File Type", "Size");
        System.out.println("\n======================================================");
        for(int i = 0; i< currentDisk.getListOfDir().size(); i++){
            if(currentDisk.getListOfDir().get(i).getParentDir() == this){
                System.out.format("%-10s  %-9s  %-10s", currentDisk.getListOfDir().get(i).getDirName(), "dir", currentDisk.getListOfDir().get(i).getSize()+ " bytes");
                System.out.println();
            }
        }
        for(int i = 0; i< getContainedFile().size(); i++){
            System.out.format("%-10s  %-9s  %-10s", getContainedFile().get(i).getDocName(), getContainedFile().get(i).getDocType(), getContainedFile().get(i).getDocSize()+" bytes");
            System.out.println();
            totalSize+= getContainedFile().get(i).getDocSize();
        }
        System.out.print("\n");
        System.out.format("%-10s  %-9s  %-10s", getContainedFile().size()+" file(s)", " ",totalSize+" bytes");
        System.out.println();

    }

    /**
     * REQ8 - print everything including child's folder documents, simlar to REQ7.
     *
     * @param currentDisk - The current disk pointer from CVFS.java
     */
    public void rList(Disk currentDisk) {
        final int SPACING = 12;
        System.out.println("\n\n\nDirectory of "+dirName+"\n");
        System.out.printf("%-"+(SPACING+(findMaxLevel(currentDisk,this)-1)*10)+"s %9s  %10s ", "File Name", "File Type", "Size");
        System.out.println();
        final int ITERATIONS = 17;
        for(int j=0; j<(findMaxLevel(currentDisk,this)*ITERATIONS); j++){
            System.out.print("=");
        }
        System.out.println();
        printContent(currentDisk,this,0,(SPACING+(findMaxLevel(currentDisk,this)-1)*9));
        System.out.println();

        System.out.format("%-10s  %-9s  %-10s", findNumOfFiles(currentDisk,this,0,0)+" file(s)         ", rDirSize(currentDisk,this,0)," bytes");
        System.out.println();
    }

    /**
     * Calculate the total file size for the REQ8 rList program.
     *
     * @param currentDisk - The current disk pointer from CVFS.java
     * @param scanDir - Temporary pointer to point the current iterated directory object.
     * @param tempSize - Temporary variable to accumulate the calculated size.
     * @return - return the file size
     */
    private int rDirSize(Disk currentDisk, Directory scanDir,int tempSize){
        int totalSize = tempSize;
        for(int i = 0; i< scanDir.getContainedFile().size(); i++){
            totalSize += scanDir.getContainedFile().get(i).getDocSize();
        }
        for(int i = 0; i< currentDisk.getListOfDir().size(); i++){
            if(scanDir == currentDisk.getListOfDir().get(i).getParentDir()){
                totalSize+=rDirSize(currentDisk, currentDisk.getListOfDir().get(i),tempSize);
            }
        }
        return totalSize;
    }

    /**
     * Find number of files of a directory recursively for REQ8.
     *
     * @param currentDisk - The pointer for current disk.
     * @param scanDir - The temporary pointer to show the current iterated directory object.
     * @param count - Accumulated count value
     * @param max - The max value to compare everytime
     * @return - Returns an integer of the number of files
     */
    private int findNumOfFiles(Disk currentDisk, Directory scanDir, int count,int max){
        int counter  = count;
        for(int i = 0; i< scanDir.getContainedFile().size(); i++){
            counter++;
        }
        for(int i = 0; i< currentDisk.getListOfDir().size(); i++){
            if(scanDir == currentDisk.getListOfDir().get(i).getParentDir()){
                int temp = findNumOfFiles(currentDisk, currentDisk.getListOfDir().get(i),counter,max);
                if(temp>max) max = temp;
                return max;
            }
        }
        return counter;
    }

    /**
     * //Find the deepest level of a directory recursively (aka deepest number of sub-folders in directory) for REQ8
     * @param currentDisk - Current disk pointer
     * @param find - The directory pointer for iteration.
     * @return - Returns the integer of the deepest level.
     */
    private int findMaxLevel(Disk currentDisk ,Directory find){
        //int count = 0;
        int bigCount = 0;
        for (int i = 0; i< currentDisk.getListOfDir().size(); i++) {
            Directory tempDir = currentDisk.getListOfDir().get(i);
            int count = 0;

            while(tempDir != null) {
                count++;
                if (tempDir == find) {
                    if (bigCount <= count) {
                        bigCount = count;
                    }
                    break;
                } else
                    tempDir = tempDir.getParentDir();
            }

        }
        return bigCount;
    }

    /**
     * Get the stats from the methods above, print the list recursively for REQ8 with identations and formatting
     * specified by methods above.
     *
     * @param currentDisk - Current disk pointer
     * @param scanDir - The temporary variable for pointing the directory object being iterated
     * @param level - The deepest number of levels/sub folders
     * @param totalSpace - The number of spaces to indent.
     */
    private void printContent(Disk currentDisk, Directory scanDir, int level,int totalSpace){
        int counter  = level;
        for(int i = 0; i< scanDir.getContainedFile().size(); i++){
            for(int j=0; j<level; j++){
                System.out.print(" ");
            }
            System.out.print(scanDir.getContainedFile().get(i).getDocName());
            for (int j = counter+ scanDir.getContainedFile().get(i).getDocName().length(); j<totalSpace+6; j++){
                System.out.print(" ");
            }
            System.out.format("%-11s  %-10s", scanDir.getContainedFile().get(i).getDocType(), scanDir.getContainedFile().get(i).getDocSize()+" bytes");
            System.out.println();

        }
        for(int i = 0; i< currentDisk.getListOfDir().size(); i++){
            if(scanDir == currentDisk.getListOfDir().get(i).getParentDir()){
                counter = counter+8;
                for(int j=0; j<level; j++){
                    System.out.print(" ");
                }
                System.out.print(currentDisk.getListOfDir().get(i).getDirName());
                for (int j = level+ currentDisk.getListOfDir().get(i).getDirName().length()+6; j<totalSpace; j++){
                    System.out.print(" ");
                }
                System.out.format("%-10s  %-11s  %-10s", " ", "dir", currentDisk.getListOfDir().get(i).getSize()+ " bytes");
                System.out.println();
                printContent(currentDisk, currentDisk.getListOfDir().get(i),counter,totalSpace);
            }
        }
    }

    /**
     * Get the total size of current directory for REQ7 and REQ8.
     * @return Return the size according to the equation in the requirements.
     */
    //Get total size of this current directory.
    public int getSize() {
        int totalSize = 0;
        final int CONSTANT_TERM = 40;
        for (int i = 0; i < getContainedFile().size(); i++) {
            totalSize = totalSize + getContainedFile().get(i).getDocSize();
        }

        totalSize = CONSTANT_TERM+totalSize;
        return totalSize;
    }

    /**
     * Change the parent dir for methods
     * @param parentDir - Reference point
     */
    public void setParentDir(Directory parentDir) {
        this.parentDir = parentDir;
    }

    /**
     * Change the containedFile array for methods
     * @param containedFile - Reference point
     */
    public void setContainedFile(List<Document> containedFile) {
        this.containedFile = containedFile;
    }

    /**
     * Change the parent directory for methods
     * @param dir - Reference point
     */
    public void changeParentDir (Directory dir) {
        this.parentDir = dir;
    }
}





