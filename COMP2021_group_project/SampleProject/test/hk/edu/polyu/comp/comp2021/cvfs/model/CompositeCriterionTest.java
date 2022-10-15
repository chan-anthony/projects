package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompositeCriterionTest {

    Criterion criName;
    Criterion criType;
    Criterion criSize;
    Criterion criIsDoc;

    Criterion notCriName;
    Criterion notCriSize;
    Criterion notCriIsDoc;

    Criterion binaryCriOr;
    Criterion binaryCriAnd;

    @Before
    public void prepare() {
        criName = new Criterion("aa", "name", "contains", "\"abc\"");
        criType = new Criterion("ab", "type", "equals", "\"doc\"");
        criSize = new Criterion("ac", "size", "<", 45);
        criIsDoc = new Criterion("IsDocument");

        notCriName = new CompositeCriterion("ba", "name", "contains", "\"abc\"", true);
        notCriSize = new CompositeCriterion("bb", "type", "equals", "\"doc\"", true);
        notCriIsDoc = new CompositeCriterion("bc", "size", "<", 45, true);

        binaryCriOr = new CompositeCriterion("ca", "name", "contains", "\"abc\"", criName, "||",  criType);
        binaryCriAnd = new CompositeCriterion("ca", "name", "contains", "\"abc\"", criName, "&&",  criType);
    }

    @Test
    public void isNegationTest() {
        Assert.assertTrue(notCriName.isNegation());
        Assert.assertTrue(notCriSize.isNegation());
        Assert.assertTrue(notCriIsDoc.isNegation());
    }

    @Test
    public void getCriThreeTest() {
        Assert.assertEquals("aa", binaryCriOr.getCriThree().getCriName());
        Assert.assertEquals("aa", binaryCriAnd.getCriThree().getCriName());
    }

    @Test
    public void getCriFourTest() {
        Assert.assertEquals("ab", binaryCriOr.getCriFour().getCriName());
        Assert.assertEquals("ab", binaryCriAnd.getCriFour().getCriName());
    }

    @Test
    public void getLogicOPTest() {
        Assert.assertEquals("||", binaryCriOr.getLogicOP());
        Assert.assertEquals("&&", binaryCriAnd.getLogicOP());
    }

}
