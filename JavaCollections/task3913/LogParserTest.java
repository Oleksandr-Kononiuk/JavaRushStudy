package com.javarush.task.task39.task3913;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class LogParserTest {

    @Test
    public void testSimpleQuery() {
        String path = "D:\\test\\";
        LogParser logParser = new LogParser(Paths.get(path));
        LogParserWithStream logParserWithStream = new LogParserWithStream(Paths.get(path));

        Set<Object> expected = new HashSet<>();
        expected.add("Amigo");
        expected.add("Elly");
        expected.add("Test");
        expected.add("Diego");
        expected.add("Vasya Pupkin");
        expected.add("Eduard Petrovich Morozko");

        Set<Object> actualForLogParser = logParser.execute("get user");
        Set<Object> actualForLogParserWithStream = logParserWithStream.execute("get user");

        Assert.assertEquals(expected, actualForLogParser);
        Assert.assertEquals(expected, actualForLogParserWithStream);
    }

    @Test
    public void testFullQuery() {
        String path = "D:\\test\\";
        LogParser logParser = new LogParser(Paths.get(path));
        LogParserWithStream logParserWithStream = new LogParserWithStream(Paths.get(path));

        Set<Object> expected = new HashSet<>();
        expected.add("192.168.100.2");
        expected.add("127.0.0.1");

        Set<Object> actualForLogParser = logParser.execute("get ip for event = \"SOLVE_TASK\" " +
                "and date between \"11.12.2011 0:00:00\" and \"03.01.2015 23:59:59\"");
        Set<Object> actualForLogParserWithStream = logParserWithStream.execute("get ip for event = \"SOLVE_TASK\" " +
                "and date between \"11.12.2011 0:00:00\" and \"03.01.2015 23:59:59\"");

        Assert.assertEquals(expected, actualForLogParser);
        Assert.assertEquals(expected, actualForLogParserWithStream);
    }

    @Test
    public void testQueryWith3Fields() {
        String path = "D:\\test\\";
        LogParser logParser = new LogParser(Paths.get(path));
        LogParserWithStream logParserWithStream = new LogParserWithStream(Paths.get(path));

        Set<Object> expected = new HashSet<>();
        expected.add("127.0.0.1");

        Set<Object> actualForLogParser = logParser.execute("get ip for event = \"SOLVE_TASK\"" +
                " and status = \"OK\"" +
                " and date between \"11.12.2011 0:00:00\" and \"03.01.2015 23:59:59\"");
        Set<Object> actualForLogParserWithStream = logParserWithStream.execute("get ip for event = \"SOLVE_TASK\"" +
                " and status = \"OK\"" +
                " and date between \"11.12.2011 0:00:00\" and \"03.01.2015 23:59:59\"");

        Assert.assertEquals(expected, actualForLogParser);
        Assert.assertEquals(expected, actualForLogParserWithStream);
    }
}