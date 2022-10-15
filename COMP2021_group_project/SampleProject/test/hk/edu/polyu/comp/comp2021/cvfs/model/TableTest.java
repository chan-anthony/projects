package hk.edu.polyu.comp.comp2021.cvfs.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TableTest {

    Table tableWithOneCol;
    Table tableWithTwoCol;
    Table tableWithTwoColTwo;
    Table tableWithThreeCol;
    Table tableWithFourCol;
    Table tableWithFiveCol;

    String tableOne;
    String tableTwo;
    String tableThree;
    String tableFour;
    String tableFive;



    @Before
    public void prepare() {
        tableOne = "A";
        tableTwo = "B";
        tableThree = "C";
        tableFour = "D";
        tableFive = "E";

        tableWithOneCol = new Table(tableOne, 5);
        tableWithTwoCol = new Table(tableOne, 5, tableTwo, 5);
        tableWithTwoColTwo = new Table(tableOne, 5, tableTwo, 5);
        tableWithThreeCol = new Table(tableOne, 5, tableTwo, 5, tableThree, 5);
        tableWithFourCol = new Table(tableOne, 5, tableTwo, 5, tableThree, 5, tableFour, 5);
        tableWithFiveCol = new Table(tableOne, 5, tableTwo, 5, tableThree, 5, tableFour, 5, tableFive, 5);
    }

    @Test
    public void getTitleTest() {
        List<String> title = new ArrayList<String>();
        title.add(tableOne);
        title.add(tableTwo);
        title.add(tableThree);
        title.add(tableFour);
        title.add(tableFive);

        Assert.assertEquals(title, tableWithFiveCol.getTitle());
    }

    @Test
    public void getARowTest() {
        tableWithThreeCol.add("AA", "BB", "CC");

        Assert.assertEquals("AA   BB   CC   ", tableWithThreeCol.getARow(0));
    }

    @Test
    public void getRowsTeat() {
        List<String> rows = new ArrayList<String>();
        rows.add("AA   BB   CC   ");
        rows.add("AAA  BBB  CCC  ");

        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithThreeCol.add("AAA", "BBB", "CCC");

        Assert.assertEquals(rows, tableWithThreeCol.getRows());
    }

    @Test
    public void  getNumberOfRowsTeat() {
        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithThreeCol.add("AAA", "BBB", "CCC");

        Assert.assertEquals(2, tableWithThreeCol.getNumberOfRows());
    }

    @Test
    public void getNumberOfAttributeTest() {
        Assert.assertEquals(1, tableWithOneCol.getNumberOfAttribute());
        Assert.assertEquals(2, tableWithTwoCol.getNumberOfAttribute());
        Assert.assertEquals(3, tableWithThreeCol.getNumberOfAttribute());
        Assert.assertEquals(4, tableWithFourCol.getNumberOfAttribute());
        Assert.assertEquals(5, tableWithFiveCol.getNumberOfAttribute());
    }

    @Test
    public void getAColumnTest() {
        List<String> firstCol = new ArrayList<String>();
        firstCol.add("AA");
        firstCol.add("AAA");
        List<String> secondCol = new ArrayList<String>();
        secondCol.add("BB");
        secondCol.add("BBB");
        List<String> thirdCol = new ArrayList<String>();
        thirdCol.add("CC");
        thirdCol.add("CCC");

        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithThreeCol.add("AAA", "BBB", "CCC");

        Assert.assertEquals(firstCol, tableWithThreeCol.getAColumn(0));
        Assert.assertEquals(secondCol, tableWithThreeCol.getAColumn(1));
        Assert.assertEquals(thirdCol, tableWithThreeCol.getAColumn(2));
    }

    @Test
    public void getColumnsTest() {
        List<String> firstCol = new ArrayList<String>();
        firstCol.add("AA");
        firstCol.add("AAA");
        List<String> secondCol = new ArrayList<String>();
        secondCol.add("BB");
        secondCol.add("BBB");
        List<String> thirdCol = new ArrayList<String>();
        thirdCol.add("CC");
        thirdCol.add("CCC");

        List[] columns = new ArrayList[3];
        columns[0] = firstCol;
        columns[1] = secondCol;
        columns[2] = thirdCol;

        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithThreeCol.add("AAA", "BBB", "CCC");

        for(int i = 0; i < 3; i++) {
            Assert.assertEquals(columns[i], tableWithThreeCol.getColumns()[i]);
        }
    }

    @Test
    public void getTotalLengthTest() {
        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithThreeCol.add("AAA", "BBB", "CCC");

        Assert.assertEquals(15, tableWithThreeCol.getTotalLength());
    }

    @Test
    public void addTest() {
        tableWithOneCol.add("AA");
        tableWithTwoCol.add("AA", "BB");
        tableWithThreeCol.add("AA", "BB", "CC");
        tableWithFourCol.add("AA", "BB", "CC", "DD");
        tableWithFiveCol.add("AA", "BB", "CC", "DD", "EE");

        String oneColRow = "AA";
        String twoColRow = "AA   BB   ";
        String threeColRow = "AA   BB   CC   ";
        String FourColRow = "AA   BB   CC   DD   ";
        String FiveColRow = "AA   BB   CC   DD   EE   ";


        Assert.assertEquals(oneColRow, tableWithOneCol.getARow(0));
        Assert.assertEquals(twoColRow, tableWithTwoCol.getARow(0));
        Assert.assertEquals(threeColRow, tableWithThreeCol.getARow(0));
        Assert.assertEquals(FourColRow, tableWithFourCol.getARow(0));
        Assert.assertEquals(FiveColRow, tableWithFiveCol.getARow(0));


    }

    @Test
    public void addOneRowTest() {
        String row = "AA   BB   ";
        tableWithThreeCol.addOneRow(row);

        Assert.assertEquals(row, tableWithThreeCol.getARow(0));
    }

    @Test
    public void combineTest() {
        tableWithTwoCol.add("AA", "BB");
        tableWithTwoColTwo.add("AAA", "BBB");

        tableWithTwoCol.combine(tableWithTwoColTwo);

        String rowOne = "AA   BB   ";
        String rowTwo = "AAA  BBB  ";

        Assert.assertEquals(rowOne, tableWithTwoCol.getARow(0));
        Assert.assertEquals(rowTwo, tableWithTwoCol.getARow(1));
    }



}
