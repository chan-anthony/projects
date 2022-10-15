package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

    Document newDoc;

    @Before
    public void prepare() {
        newDoc = new Document("polyU", "txt", "abc");
    }

    @Test
    public void getDocNameTest() {
        Assert.assertEquals("polyU", newDoc.getDocName());
    }

    @Test
    public void getDocTypeTest() {
        Assert.assertEquals("txt", newDoc.getDocType());
    }

    @Test
    public void getContent() {
        Assert.assertEquals("abc", newDoc.getContent());
    }

    @Test
    public void getDocSizeTest() {
        Assert.assertEquals(46, newDoc.getDocSize());
    }

    @Test
    public void changeDocNameTest() {
        newDoc.changeDocName("CityU");
        Assert.assertEquals("CityU", newDoc.getDocName());
    }

}
