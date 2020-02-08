package com.javarush.task.task39.task3913;

import com.javarush.task.task39.task3913.query.QLQuery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogParserWithStream implements QLQuery {
    private Path logDir;
    private String datePattern = "dd.MM.yyyy HH:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern, Locale.ENGLISH);

    public LogParserWithStream(Path logDir) {
        this.logDir = logDir;
    }

    /**
     * Searching some fields on log file and return <tt>Set<? extends Object></tt>
     * unique values for query. Using Stream API for searching necessary log lines.
     * Query can be written in three ways.
     *
     * @param   query can be written in three different ways.<p>
     *      get Field1<p>
     *      get Field1 for Field2 = "Value1" and date between "After" and "Before"<p>
     *      get Field1 for Field2 = "Value1" and Field3 = "Value2" and date between "After" and "Before"<p>
     *
     *  Where:<p>
     *  <tt>Field1</tt> - type for <tt>HashSet<<tt>Field1</tt>></tt>.<p>
     *  <tt>Field2</tt> - type for <tt>Value1</tt>.<p>
     *  <tt>Field3</tt> - type for <tt>Value2</tt>.<p>
     *  <tt>Value1</tt> - value which using for searching <tt>Field1</tt>.<p>
     *  <tt>Value2</tt> - value which using for searching <tt>Field1</tt>.<p>
     *  <tt>After</tt> - date (not including) after which necessary looking <tt>Filed1</tt>. If field is <code>null</code>, search will be limited only <tt>Before</tt><p>
     *  <tt>Before</tt> - date (not including) before which necessary looking <tt>Filed1</tt>. If field is <code>null</code>, search will be limited only <tt>After</tt>.
     *                If <tt>After</tt> and <tt>Before</tt> is <code>null</code>, search will be all log lines.
     * @return <tt>Set<? extends Object></tt> - Set of unique values <tt>Field1</tt> type.
     */
    @Override
    public Set<Object> execute(String query) {
        String[] queryLength = query.split(" ");

        if (queryLength.length == 2) {
            return getSimpleQuery(query);
        } else if (queryLength.length > 2){
            return getFullQuery(query);
        }
        return null;
    }
/*
************************************************************************************************************************
All private methods
************************************************************************************************************************
 */
    //return all Fields
    private Set<Object> getSimpleQuery(String simpleQuery) {
        switch(simpleQuery) {
            case "get ip"       : return findFieldAndAdd("ip", getAllLogLinesWithStreamAPI(null, null));
            case "get user"     : return findFieldAndAdd("user", getAllLogLinesWithStreamAPI(null, null));
            case "get date"     : return findFieldAndAdd("date", getAllLogLinesWithStreamAPI(null, null));
            case "get event"    : return findFieldAndAdd("event", getAllLogLinesWithStreamAPI(null, null));
            case "get status"   : return findFieldAndAdd("status", getAllLogLinesWithStreamAPI(null, null));
        }
        return new HashSet<>();
    }

    //parse query and return all Field1s
    private Set<Object> getFullQuery(String query) {
        Set<Object> fields = new HashSet<>();
        String queryPattern = "get (?<field1>\\w+) for (?<field2>\\w+) = " +
                "\"(?<value1>.+?)\"" +
                "( and (?<field3>\\w+) = \"(?<value2>.+?)\")?" +
                " and date between " +
                "\"(?<after>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\" and " +
                "\"(?<before>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\"";
        Pattern pattern = Pattern.compile(queryPattern);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String field1 = matcher.group("field1"),    //type for Set
                    field2 = matcher.group("field2"),   //type for value1
                    field3 = matcher.group("field3"),   //type for value2
                    value1 = matcher.group("value1"),
                    value2 = matcher.group("value2"),
                    after = matcher.group("after"),     //date after
                    before = matcher.group("before");   //date before

            List<String[]> allLogLinesBetweenDates = getAllLogLinesWithStreamAPI(getDate(after), getDate(before));
            List<String[]> firstFilter;
            List<String[]> secondFilter;

            if (field3 == null && value2 == null) {
                //filter lines which contains value1 and field2
                firstFilter = filter(allLogLinesBetweenDates, field2, value1);
                //get from firstFilter unique field1
                fields = findFieldAndAdd(field1, firstFilter);
            } else {
                //filter lines which contains value1 and field2
                firstFilter = filter(allLogLinesBetweenDates, field2, value1);
                //filter again firstFilter by value2 and field3
                secondFilter = filter(firstFilter, field3, value2);
                //get from secondFilter unique field1
                fields = findFieldAndAdd(field1, secondFilter);
            }
        }
        return fields;
    }

    //return filtered all log lines by field and value
    private List<String[]> filter(List<String[]> lines, String field, String value) {
        List<String[]> list = new ArrayList<>();
        for (String[] line : lines) {
            switch (field) {
                case "ip"     : if (value.equals(line[0])) list.add(line);         break;
                case "user"   : if (value.equals(line[1])) list.add(line);         break;
                case "date"   : if (value.equals(line[2])) list.add(line);         break;
                case "event"  : if (value.equals(line[3].split(" ")[0])) list.add(line); break;
                case "status" : if (value.equals(line[4])) list.add(line);         break;
            }
        }
        return list;
    }

    //search field on list and after return unique Set of founded fields
    private Set<Object> findFieldAndAdd(String returnType, List<String[]> list) {
        Set<Object> fields = getSetByReturnType(returnType);
        for (String[] line : list) {
            switch (returnType) {
                case "ip"     : fields.add(line[0]);                               break;
                case "user"   : fields.add(line[1]);                               break;
                case "date"   : fields.add(getDate(line[2]));                      break;
                case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                case "status" : fields.add(getStatus(line[4]));                    break;
            }
        }
        return fields;
    }

    //return parameterized HashSet by returnType
    private Set getSetByReturnType(String returnType) {
        switch (returnType) {
            case "ip"       : return new HashSet<String>();
            case "user"     : return new HashSet<String>();
            case "date"     : return new HashSet<Date>();
            case "event"    : return new HashSet<Event>();
            case "status"   : return new HashSet<Status>();
        }
        return new HashSet<>();
    }

    //return specific Event by eventStr
    private Event getEvent(String eventStr) {
        switch (eventStr) {
            case "DONE_TASK": return Event.DONE_TASK;
            case "SOLVE_TASK": return Event.SOLVE_TASK;
            case "WRITE_MESSAGE": return Event.WRITE_MESSAGE;
            case "DOWNLOAD_PLUGIN": return Event.DOWNLOAD_PLUGIN;
            case "LOGIN": return Event.LOGIN;
        }
        return null;
    }

    //return specific Status by statusStr
    private Status getStatus(String statusStr) {
        switch (statusStr) {
            case "OK": return Status.OK;
            case "ERROR": return Status.ERROR;
            case "FAILED": return Status.FAILED;
        }
        return null;
    }

    //parse String representation of date to Date
    private Date getDate(String string) {
        try {
            return simpleDateFormat.parse(string);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

    //checking current Date in period after-before
    private boolean checkDate(String current, Date after, Date before) {
        Date date = getDate(current);

        if (date != null) {
            if (after != null && before == null && date.after(after)) {
                return true;
            } else if (after == null && before != null && date.before(before)) {
                return true;
            } else if (after == null && before == null) {
                return true;
            } else {
                if (date.before(before) && date.after(after)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Using Stream API get and return from log files all log lines which between after-before Dates.
    private List<String[]> getAllLogLinesWithStreamAPI(Date after, Date before) {
        List<String[]> allDateLogLines = new ArrayList<>();

        Pattern shortPattern = Pattern.compile("(?<anybefore>\\.*)\\s(?<date>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\\s(?<anyafter>\\.*)");

        try {
            Files.list(logDir).filter(f -> f.toString().endsWith(".log"))
                    .collect(Collectors.toList()).forEach(x -> {
                        try {
                            allDateLogLines.addAll(Files.readAllLines(x).stream()
                                    .filter(l -> {
                                        Matcher matcher = shortPattern.matcher(l);
                                        if (matcher.find())
                                            return checkDate(matcher.group("date"), after, before);
                                        return false;
                                    })
                                    .map(l -> l.split("\t"))
                                    .collect(Collectors.toList()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allDateLogLines;
    }
}