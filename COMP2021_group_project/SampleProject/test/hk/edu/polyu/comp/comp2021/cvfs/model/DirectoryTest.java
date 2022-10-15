package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectoryTest {
    Disk newDisk;

    Directory dirRoot;
    Document docOne;
    Document docTwo;

    String deleteString;

    @Before
    public void prepare() {
        newDisk = new Disk(500);

        dirRoot = new Directory("Root", null);
        docOne = new Document("PolyU", "txt", "abc");
        docTwo = new Document("CityU", "html", "def");

        deleteString = "PolyU";
    }

    @Test
    public void getDirNameTest() {
        Assert.assertEquals("Root", dirRoot.getDirName());
    }

    @Test
    public void getContainedFileTest() {
        dirRoot.newDoc("PolyU", "txt", "abc", newDisk);
        dirRoot.newDoc("CityU", "html", "def", newDisk);

        Assert.assertEquals("PolyU", dirRoot.getContainedFile().get(0).getDocName());
        Assert.assertEquals("CityU", dirRoot.getContainedFile().get(1).getDocName());
    }

    @Test
    public void newDocTest() {
        dirRoot.newDoc("PolyU", "txt", "abc", newDisk);
        Assert.assertEquals("PolyU", dirRoot.getContainedFile().get(0).getDocName());
        Assert.assertEquals("txt", dirRoot.getContainedFile().get(0).getDocType());
        Assert.assertEquals("abc", dirRoot.getContainedFile().get(0).getContent());
    }

    @Test
    public void deleteTest() {
        dirRoot.newDoc("PolyU", "txt", "abc", newDisk);
        dirRoot.newDoc("CityU", "html", "def", newDisk);

        dirRoot.delete("PolyU", newDisk);

        boolean sameDoc = false;
        for(int i = 0; i < dirRoot.getContainedFile().size(); i++) {
            if(dirRoot.getContainedFile().get(i).getDocName().equals("PolyU")) {
                sameDoc = true;
                break;
            }
        }
        Assert.assertFalse(sameDoc);
    }

    @Test
    public void renameTest() {
        dirRoot.newDoc("PolyU", "txt", "abc", newDisk);

        dirRoot.rename("PolyU", "cityU", newDisk);

        Assert.assertEquals("cityU", dirRoot.getContainedFile().get(0).getDocName());
    }

    @Test
    public void getSizeTest() {
        dirRoot.newDoc("PolyU", "txt", "abc", newDisk);
        dirRoot.newDoc("CityU", "html", "def", newDisk);

        Assert.assertEquals(132, dirRoot.getSize());
    }
}
