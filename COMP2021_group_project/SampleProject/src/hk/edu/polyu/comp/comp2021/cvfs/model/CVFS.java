/*
COMP2021 Group Project: In-Memory Virtual File System
Group 15
 */
package hk.edu.polyu.comp.comp2021.cvfs.model;


import java.util.Scanner;
import java.util.ArrayList;

/**
 * The main CVFS program. This class consists of two methods
 * The mainRunner() takes the user input, check for parameter errors, and then call the respective methods to generate outputs.
 * The trim() method is an auxillary method for mainRunner() to trim the userInput to separate into different elements, such as the command and the parameters strings.
 */
public class CVFS {
    /**
     * A list of acceptable docTypes specified in the requirements.
     */
    private String[] AllDocType = {"txt", "java", "html","css"};

    private Disk currentDisk;

    /**
     *
     */
    public void mainRunner() {
        {
            boolean isValidInput = false;

            while (!isValidInput) {
                isValidInput = true;
                System.out.println("Please enter a command: ");

                Scanner scanner = new Scanner(System.in);
                String userInput = "";

                System.out.println(userInput);
                userInput = scanner.nextLine();
                // System.out.println("Before trim" + userInput);

                /*
                userInput = userInput.replaceAll("\\s+", " ");
                System.out.println("After trim" + userInput);
                */



                String[] splittedInput = customTrim(userInput).toArray(new String[0]);
                // for (String s : splittedInput) {
                    // System.out.println(s);
                // }


                //Find the command by checking the first space of the input

        /*
        String commandVariable = "";
        commandVariable = userInput;
        for (int i = 0; i < userInput.length(); i++) {
            System.out.println(userInput);
            if (userInput.charAt(i) == ' ') {
                commandVariable = userInput.substring(0, i);
                break;
            }
        }



        System.out.println("The command variable is: " + commandVariable);
*/


                //Have 14 different if statement/switch case, scan for different parameter according to each commandVariable's respective parameter



                switch (splittedInput[0]) {
                    case "newDisk":
                        //Check if have enough parameter
                        int newDiskParamaterNo = 1;
                        if (splittedInput.length > newDiskParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }
                        //Check the types of parameter is valid
                        try {
                            int diskSize = Integer.parseInt(splittedInput[1]);
                        } catch (Exception e) {
                            isValidInput = false;
                            System.out.println("Invalid input - parameter is not integer.");
                            break;
                        }
                        int diskSize = Integer.parseInt(splittedInput[1]);
                        //Run a newDiskMethod
                        setCurrentDisk(new Disk(diskSize));
                        break;

                    case "newDoc":
                        //Check if have correct number of parameters
                        int newDocParamaterNo = 3;
                        if (splittedInput.length > newDocParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < newDocParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }

                        //check if the docName is valid
                        //
                        if (splittedInput[1].length() < 10) {
                            for (int i = 0; i < splittedInput[1].length(); i++) {
                                final int CHAR_LOWR_CASE_LOWR_LIMIT = 48;
                                final int CHAR_LOWR_CASE_UPPR_LIMIT = 57;
                                if (!(Character.isLetter(splittedInput[1].charAt(i)) || (splittedInput[1].charAt(i) >= CHAR_LOWR_CASE_LOWR_LIMIT && splittedInput[1].charAt(i) <= CHAR_LOWR_CASE_UPPR_LIMIT))) {
                                    isValidInput = false;
                                    System.out.println("Invalid input - Only digits and English letters are allowed in file names");
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Invalid input - The docName is too long. It should be at most 10 characters.");
                            isValidInput = false;
                            break;
                        }
                        //check if docType is valid
                        boolean breakFlag = false;
                        for (int i = 0; i < 4; i++) {
                            if (splittedInput[2].equals(AllDocType[i])) break;
                            if (i == 3) {
                                breakFlag = true;
                                System.out.println("Invalid input - docType is invalid.");
                                isValidInput = false;
                                break;
                            }
                        }
                        if (breakFlag) break;

                        //Run a newDocMethod
                        getCurrentDisk().getCurDirectory().newDoc(splittedInput[1], splittedInput[2], splittedInput[3], currentDisk);
                        break;

                    case "newDir":
                        int newDirParamaterNo = 1;
                        if (splittedInput.length > newDirParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < newDirParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a newDirMethod
                        try {
                            System.out.println("now is in the case of newDir ");
                            getCurrentDisk().newDir(splittedInput[1], getCurrentDisk().getCurDirectory());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid input - The Disk size is not big enough");
                        }
                        break;

                    case "delete":
                        int deleteParamaterNo = 1;
                        if (splittedInput.length > deleteParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < deleteParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a deleteMethod
                        getCurrentDisk().getCurDirectory().delete(splittedInput[1], currentDisk);
                        break;

                    case "rename":
                        int renameParamaterNo = 2;
                        if (splittedInput.length > renameParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < renameParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a renameMethod
                        getCurrentDisk().getCurDirectory().rename(splittedInput[1], splittedInput[2], currentDisk);
                        break;

                    // REQ6
                    case "changeDir":
                        int changeDirParamaterNo = 1;
                        if (splittedInput.length > changeDirParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < changeDirParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }

                        //Run a changeDirMethod
                        getCurrentDisk().changeDir(splittedInput[1]);
                        break;

                    case "list":
                        int listParamaterNo = 0;
                        if (splittedInput.length > listParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < listParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a listMethod
                        getCurrentDisk().getCurDirectory().list(getCurrentDisk());
                        break;

                    case "rList":
                        int rlistParamaterNo = 0;
                        if (splittedInput.length > rlistParamaterNo + 1) {
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        } else if (splittedInput.length < rlistParamaterNo + 1) {
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a rListMethod
                        getCurrentDisk().getCurDirectory().rList(getCurrentDisk());
                        break;

                    case "newSimpleCri":

                        // Check the number of Paramaters: 1 Paramater for Isdocument, 4 Paramaters otherwise
                        if(splittedInput.length == 2 && splittedInput[1].equals("IsDocument")) {
                            //Run a newSimpleCriMethod
                            getCurrentDisk().newSimpleCri(splittedInput[1]);
                        } else {
                            int newSimpleCriParamaterNo = 4;
                            if (splittedInput.length > newSimpleCriParamaterNo + 1) {
                                System.out.println("Invalid input - too many parameter");
                                isValidInput = false;
                                break;
                            } else if (splittedInput.length < newSimpleCriParamaterNo + 1) {
                                System.out.println("Invalid input - too few parameter");
                                isValidInput = false;
                                break;
                            }
                            // Validation check for Paramaters:
                            // criName: Only two letters
                            if (splittedInput[1].length() != 2) {
                                System.out.println("Invalid input - criName is not two letters");
                                break;
                            }
                            // criName: Only english letters. i.e. A to Z and a to z.
                            for (int i = 0; i < splittedInput[1].length(); i++) {
                                char currentChar = splittedInput[1].charAt(i);
                                final int CHAR_UPPR_CASE_LOWR_LIMIT = 65;
                                final int CHAR_UPPR_CASE_UPPR_LIMIT = 90;
                                final int CHAR_LOWR_CASE_LOWR_LIMIT = 97;
                                final int CHAR_LOWR_CASE_UPPR_LIMIT = 122;
                                if (currentChar < CHAR_UPPR_CASE_LOWR_LIMIT || currentChar > CHAR_UPPR_CASE_UPPR_LIMIT && currentChar < CHAR_LOWR_CASE_LOWR_LIMIT || currentChar > CHAR_LOWR_CASE_UPPR_LIMIT) {
                                    System.out.println("Invalid input - criName is not two letters");
                                    break;
                                }
                            }

                            //attrName:  either name, type, or size
                            if (!splittedInput[2].equals("name") && !splittedInput[2].equals("type") && !splittedInput[2].equals("size")) {
                                System.out.println("Invalid input - Incorrect attrName");
                                isValidInput = false;
                                break;
                            }
                            // op:
                            // Case 1: attrName = name
                            if(splittedInput[2].equals("name")){
                                // op must be "contains"
                                if (!splittedInput[3].equals("contains")) {
                                    System.out.println("Invalid input - Incorrect op. \"contains\" must be obtained if attrName = name");
                                    isValidInput = false;
                                    break;
                                }
                                // val must be a string and in double quote:
                                // Reject input if there is no two double quote after "op"
                                int locationOFLastSpaceBar = userInput.indexOf(splittedInput[3]) + splittedInput[3].length() - 1;
                                int firstIndexOfVal = locationOFLastSpaceBar + 1;
                                int lastIndexOfval = userInput.length() - 1;
                                if(userInput.charAt(firstIndexOfVal) != '"' && userInput.charAt(lastIndexOfval) != '"') {
                                    System.out.println("Invalid input - Incorrect val. val must be a string and in double quote");
                                    isValidInput = false;
                                    break;
                                }
                            }
                            // Case 2: attrName = type
                            if (splittedInput[2].equals("type")) {
                                // op must be "equals"
                                if (!splittedInput[3].equals("equals")) {
                                    System.out.println("Invalid input - Incorrect op. \"equals\" must be obtained if attrName = type");
                                    isValidInput = false;
                                    break;
                                }
                                // val must be a string and in double quote:
                                // Reject input if there is no two double quote after "op"
                                int locationOFLastSpaceBar = userInput.indexOf(splittedInput[3]) + splittedInput[3].length() - 1;
                                int firstIndexOfVal = locationOFLastSpaceBar + 1;
                                int lastIndexOfval = userInput.length() - 1;
                                if(userInput.charAt(firstIndexOfVal) != '"' && userInput.charAt(lastIndexOfval) != '"') {
                                    System.out.println("Invalid input - Incorrect val. val must be a string and in double quote");
                                    isValidInput = false;
                                    break;
                                }
                            }
                            // Case 3: attrName = size
                            if (splittedInput[2].equals("size")) {
                                if (!splittedInput[3].equals(">") && !splittedInput[3].equals("<") && !splittedInput[3].equals(">=") && !splittedInput[3].equals("<=") && !splittedInput[3].equals("==") && !splittedInput[3].equals("!=")) {
                                    System.out.println("Invalid input - Incorrect op. \">\", \"<\", \">=\", \"<=\", \"==\", \"!=\" must be obtained if attrName = size");
                                    isValidInput = false;
                                    break;
                                }

                                // val must be int
                                try {
                                    int x = Integer.parseInt(splittedInput[4]);
                                } catch (Exception e) {
                                    isValidInput = false;
                                    System.out.println("Invalid input - val is not integer.");
                                    break;
                                }
                            }
                            //Run a newSimpleCriMethod
                            getCurrentDisk().newSimpleCri(splittedInput[1], splittedInput[2], splittedInput[3], splittedInput[4]);
                        }
                        System.out.println("newSimpleCri has run successfully");
                        break;


                    /*
                    case "isDocument":
                        int isDocumentParamaterNo = 0;
                        if(splittedInput.length > isDocumentParamaterNo+1){
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }else if(splittedInput.length < isDocumentParamaterNo+1){
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a isDocumentMethod
                        System.out.println("now is in the case of isDocument ");
                        break;
                    */
                    case "newNegation":
                        int newNegationParamaterNo = 2;
                        if(splittedInput.length > newNegationParamaterNo+1){
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }else if(splittedInput.length < newNegationParamaterNo+1){
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a newNegationMethod
                        System.out.println("now is in the case of newNegation ");

                        getCurrentDisk().newNegation(splittedInput[1], splittedInput[2]);

                        break;

                    case "newBinaryCri":
                        int newBinaryCriParamaterNo = 4;
                        if(splittedInput.length > newBinaryCriParamaterNo+1){
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }else if(splittedInput.length < newBinaryCriParamaterNo+1){
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a newBinaryCriMethod
                        System.out.println("now is in the case of newBinaryCri ");
                        getCurrentDisk().newBinaryCri(splittedInput[1], splittedInput[2], splittedInput[3], splittedInput[4]);
                        break;

                    case "printAllCriteria":
                        int printAllCriteriaParamaterNo = 0;
                        if(splittedInput.length != printAllCriteriaParamaterNo+1){
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a printAllCriteriaMethod
                        System.out.println("now is in the case of printAllCriteria ");
                        getCurrentDisk().printAllCriteria();
                        break;

                    case "search":
                        int searchParamaterNo = 1;
                        if(splittedInput.length != searchParamaterNo+1){
                            System.out.println("Invalid input - Number of parameter is wrong");
                            isValidInput = false;
                            break;
                        }
                        //Run a searchMethod
                        System.out.println("now is in the case of search ");
                        try {
                            Directory currentDir = getCurrentDisk().getCurDirectory();
                            Table newTable = getCurrentDisk().search(currentDir, splittedInput[1]);
                            getCurrentDisk().printOutputOfSearch(newTable);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid input - IllegalArgument");
                            isValidInput = false;
                        }
                        break;

                    case "rSearch":
                        int rsearchParamaterNo = 1;
                        if(splittedInput.length > rsearchParamaterNo+1){
                            System.out.println("Invalid input - too many parameter");
                            isValidInput = false;
                            break;
                        }else if(splittedInput.length < rsearchParamaterNo+1){
                            System.out.println("Invalid input - too few parameter");
                            isValidInput = false;
                            break;
                        }
                        //Run a researchMethod
                        System.out.println("now is in the case of research ");
                        try {
                            Directory currentDir = getCurrentDisk().getCurDirectory();
                            Table newTable = getCurrentDisk().rSearch(currentDir, splittedInput[1]);    // Search all files in the kids of currentDir

                            // print out
                            getCurrentDisk().printOutputOfSearch(newTable);

                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid input - IllegalArgument");
                            isValidInput = false;
                        }

                        break;

                    case "exit":
                        System.out.println("Program has exited!");
                        return;


                    default:
                        System.out.println("Invalid input");
                        isValidInput = false;
                        break;

                }
                isValidInput = false;
            }
        }

    }

    /**
     * The custom trim method to separate the input into respective elements (e.g commands, parameters)
     * When double quotes are detected, words in double quotes will not be trimmed.
     *
     * @param Input - Get the input of the input
     * @return - Return an array list of the separated inputs.
     */
    public static ArrayList<String> customTrim(String Input) {

        ArrayList<String> CustomsplittedInput = new ArrayList<String>();
        Input = Input+' ';
        int startIndex = 0;
        int endIndex = 0;
        boolean byPass = false;
        boolean isDoubleQuote = false;
        for(int i=0; i<Input.length(); i++){
            if(Input.charAt(i) == '"' && !byPass) {
                byPass = true;
                isDoubleQuote = true;
            }
            else if(Input.charAt(i) == '"' && byPass)
                byPass = false;
            else if(Input.charAt(i) == ' ' && !byPass) {
                endIndex = i;

                if (isDoubleQuote) {
                    endIndex = endIndex - 1;
                    startIndex = startIndex + 1;
                }

                CustomsplittedInput.add(Input.substring(startIndex,endIndex));

                if (isDoubleQuote) {
                    endIndex = endIndex + 1;
                    startIndex = startIndex - 1;
                    isDoubleQuote = false;
                }
                //startIndex += endIndex;
                startIndex = endIndex + 1;
            }
        }
        //System.out.println(CustomsplittedInput);
        return CustomsplittedInput;
    }

    /**
     * An object for the program to keep track the current disk created. Initially this is empty upon first boot of the program.
     * @return - Returns the current disk pointer.
     */
    public Disk getCurrentDisk() {
        return currentDisk;
    }

    /**
     * Changes the currentDisk for methods.
     *
     * @param currentDisk - The current disk object pointer to change to
     */
    public void setCurrentDisk(Disk currentDisk) {
        this.currentDisk = currentDisk;
    }
}
