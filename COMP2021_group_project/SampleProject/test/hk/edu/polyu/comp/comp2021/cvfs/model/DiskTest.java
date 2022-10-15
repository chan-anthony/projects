package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiskTest {

    Disk newDisk;
    Directory dirRoot;

    @Before
    public void prepare() {
        newDisk = new Disk(500);
        dirRoot = new Directory("Root", null);
    }

    @Test
    public void testConstructor() {

    }

    @Test
    public void newDirTest() {
        newDisk.newDir("Root", null);
        Assert.assertEquals(dirRoot.getDirName(), newDisk.getListOfDir().get(0).getDirName());
        Assert.assertEquals(dirRoot.getParentDir(), newDisk.getListOfDir().get(0).getParentDir());
        Assert.assertEquals(newDisk.getCurDirectory().getDirName(), dirRoot.getDirName());
    }

    @Test
    public void changeDirTest() {
        newDisk.newDir("Root", null);
        newDisk.newDir("bb", newDisk.getListOfDir().get(0));

        newDisk.changeDir("bb");
        Assert.assertEquals(newDisk.getCurDirectory().getDirName(), "bb");
    }

    @Test
    public void newSimpleCriTestName() {
        newDisk.newSimpleCri("cr", "name", "contains", "abc");

        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getCriName(), "cr");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getAttrName(), "name");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getOp(), "contains");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getVal(), "abc");
    }

    @Test
    public void newSimpleCriTestType() {
        newDisk.newSimpleCri("cr", "type", "equals", "txt");

        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getCriName(), "cr");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getAttrName(), "type");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getOp(), "equals");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getVal(), "txt");
    }

    @Test
    public void newSimpleCriTestSize() {
        newDisk.newSimpleCri("cr", "size", ">", 25);

        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getCriName(), "cr");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getAttrName(), "size");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getOp(), ">");
        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getVal(), 25);
    }

    @Test
    public void newSimpleCriTestIsDocument() {
        newDisk.newSimpleCri("IsDocument");

        Assert.assertEquals(newDisk.getListOfCriterion().get(0).getCriName(), "IsDocument");
        Assert.assertNull(newDisk.getListOfCriterion().get(0).getAttrName());
        Assert.assertNull(newDisk.getListOfCriterion().get(0).getOp());
        Assert.assertNull(newDisk.getListOfCriterion().get(0).getVal());
    }

    @Test
    public void searchTestName() {
        // Add new Dir
        newDisk.newDir("Root", null);
        newDisk.newDir("bb", newDisk.getListOfDir().get(0));

    }

    @Test
    public void testNewNegation() {
        //Create criterions
        newDisk.newSimpleCri("aa", "size", "<", "4");
        newDisk.newNegation("bb", "aa");

        for (int i = 0; i < newDisk.getListOfCriterion().size(); i++) {
            if (newDisk.getListOfCriterion().get(i).getCriName().equals("bb")) {
                Assert.assertEquals(newDisk.getListOfCriterion().get(i).getOp(), ">=");
                break;
            }
        }
    }

    @Test
    public void searchTestSimCri() {
        newDisk.newSimpleCri("aa", "name", "contains", "abc");
        Directory targetedDir = newDisk.getListOfDir().get(0);
        targetedDir.newDoc("abc", "doc", "ggggg", newDisk);

        Table tableAfterSearch = newDisk.search(targetedDir, "aa");

        Assert.assertEquals(1, tableAfterSearch.getNumberOfRows());
    }

    @Test
    public void searchTestNegation() {
        newDisk.newSimpleCri("ab", "type", "equals", "txt");
        newDisk.newNegation("aa", "ab");

        Directory targetedDir = newDisk.getListOfDir().get(0);
        targetedDir.newDoc("abc", "doc", "ggggg", newDisk);

        Table tableAfterSearch = newDisk.search(targetedDir, "aa");

        Assert.assertEquals(1, tableAfterSearch.getNumberOfRows());
    }

    @Test
    public void searchTestComOr() {
        newDisk.newSimpleCri("aa", "name", "contains", "abc");
        newDisk.newSimpleCri("ab", "type", "equals", "txt");
        newDisk.newSimpleCri("ca", "name", "contains", "abc");
        newDisk.newBinaryCri("ca", "aa", "||",  "ab");

        Directory targetedDir = newDisk.getListOfDir().get(0);
        targetedDir.newDoc("abc", "doc", "ggggg", newDisk);

        Table tableAfterSearch = newDisk.search(targetedDir, "ca");

        Assert.assertEquals(1, tableAfterSearch.getNumberOfRows());
    }

    @Test
    public void searchTestComAnd() {
        newDisk.newSimpleCri("aa", "name", "contains", "abc");
        newDisk.newSimpleCri("ab", "type", "equals", "txt");
        newDisk.newSimpleCri("ca", "name", "contains", "abcd");
        newDisk.newBinaryCri("ca", "aa", "&&",  "ab");

        Directory targetedDir = newDisk.getListOfDir().get(0);
        targetedDir.newDoc("abc", "doc", "ggggg", newDisk);

        Table tableAfterSearch = newDisk.search(targetedDir, "ca");

        Assert.assertEquals(0, tableAfterSearch.getNumberOfRows());
    }

    @Test
    public void rsearchTest() {
        // Root Dir
        newDisk.newSimpleCri("aa", "name", "contains", "abc");
        newDisk.getListOfDir().get(0).newDoc("abc", "doc", "ggggg", newDisk);

        // Second Dir
        newDisk.newDir("dirTwo", newDisk.getListOfDir().get(0));
        newDisk.getListOfDir().get(1).newDoc("abcd", "doc", "ggggcccg", newDisk);

        Table tableAfterSearch = newDisk.rSearch(newDisk.getListOfDir().get(0), "aa");


        Assert.assertEquals(2, tableAfterSearch.getNumberOfRows());
    }

}
