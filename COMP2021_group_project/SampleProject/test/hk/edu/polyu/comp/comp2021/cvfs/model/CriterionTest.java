package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CriterionTest {

    Criterion criName;
    Criterion criType;
    Criterion criSize;
    Criterion criIsDoc;


    @Before
    public void prepare() {
        criName = new Criterion("aa", "name", "contains", "\"abc\"");
        criType = new Criterion("ab", "type", "equals", "\"doc\"");
        criSize = new Criterion("ac", "size", ">", 45);
        criIsDoc = new Criterion("IsDocument");
    }

    @Test
    public void getCriNameTest() {
        Assert.assertEquals("aa", criName.getCriName());
        Assert.assertEquals("ab", criType.getCriName());
        Assert.assertEquals("IsDocument", criIsDoc.getCriName());
    }

    @Test
    public void getAttrNameTest() {
        Assert.assertEquals("name", criName.getAttrName());
        Assert.assertEquals("type", criType.getAttrName());
        Assert.assertNull(criIsDoc.getAttrName());
    }

    @Test
    public void getLogicOPTest() {
        Assert.assertNull(criName.getLogicOP());
    }

    @Test
    public void getCriThreeTest() {
        Assert.assertNull(criName.getCriThree());
    }

    @Test
    public void getCriFourTest() {
        Assert.assertNull(criName.getCriFour());
    }

    @Test
    public void isNegationTest() {
        Assert.assertFalse(criName.isNegation());
    }

}
