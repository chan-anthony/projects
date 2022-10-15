package hk.edu.polyu.comp.comp2021.cvfs.model;

/**
 * The class representing the document objects,
 * * This is created by newDoc and is stored in the ArrayList in Directory.java.
 */
public class Document {
    private String docName;
    private final String docType;
    private String content;
    private int size;
    // private String size;

    /**
     * REQ2 - newDoc docName docType docContent
     * @param docName - The document name
     * @param docType - The document type
     * @param content - Content of document in string.
     */
    public Document(String docName, String docType, String content) {
        final int SIZE_CONSTANT = 40;
        this.docName = docName;
        this.docType = docType;
        this.content = content;
        this.size = SIZE_CONSTANT+content.length()*2;
    }

    /**
     *
     * @return Returns the docName for methods.
     */
    public String getDocName() {
        return docName;
    }

    /**
     *
     * @return Returns the docType for methods.
     */
    public String getDocType() {
        return docType;
    }

    /**
     *
     * @return Returns the content for methods.
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @return Returns the size for methods.
     */
    public int getDocSize() {
        return size;
    }



    /**
     * Changes document name for REQ5 in Directory.java
     * @param newName - The new name
     */
    public void changeDocName (String newName) {
        this.docName = newName;
    }

}
