package com.javarush.task.task39.task3913;

import com.javarush.task.task39.task3913.query.*;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogParser implements QLQuery {
    private Path logDir;
    private List<File> logFiles;
    private String datePattern = "dd.MM.yyyy HH:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern, Locale.ENGLISH);

    public LogParser(Path logDir) {
        this.logDir = logDir;
        this.logFiles = getAllLogFiles(this.logDir.toString());
    }

    @Override
    public Set<Object> execute(String query) {//"get event for date = "30.01.2014 12:56:22″"
        String[] queryLength = query.split(" ");

        if (queryLength.length == 2) {
            return getSimpleQuery(query);
        } else if (queryLength.length > 2){
            return getFullQuery(query);
        }
        return null;
    }

    //TODO overload execute method for query type ##get field1 for field2 = "value1" and field3 = "value2" and date between "after" and "before"##
    //TODO add docs
    //TODO add Unit tests
    //TODO Create LogParser class which has been have the same implementation, but using Stream API
/*
************************************************************************************************************************
All private methods
************************************************************************************************************************
 */
    private Set<Object> getSimpleQuery(String simpleQuery) {
        switch(simpleQuery) {
            case "get ip"       : return new HashSet<>(getAllUniqueField("ip"));
            case "get user"     : return new HashSet<>(getAllUniqueField("user"));
            case "get date"     : return new HashSet<>(getAllUniqueField("date"));
            case "get event"    : return new HashSet<>(getAllUniqueField("event"));
            case "get status"   : return new HashSet<>(getAllUniqueField("status"));
        }
        return new HashSet<>();
    }

    private Set<Object> getAllUniqueField(String field) {
        Set<Object> fields = getSetWithReturnType(field);
        List<String[]> allLogLinesBetweenDates = getLinesFromLogFile(null, null);

        for (String[] line : allLogLinesBetweenDates){
            switch (field) {
                case "ip"     : fields.add(line[0]);                               break;
                case "user"   : fields.add(line[1]);                               break;
                case "date"   : fields.add(getDate(line[2]));                      break;
                case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                case "status" : fields.add(getStatus(line[4]));                    break;
            }
        }
        return fields;
    }

    // Example: get ip for user = "Eduard Petrovich Morozko" and date between "11.12.2013 0:00:00" and "03.01.2014 23:59:59".
    // get field1 for field2 = "value1" and date between "after" and "before"
    private Set<Object> getFullQuery(String query) {
        Set<Object> set = new HashSet<>();
        String queryPattern = "get (?<field1>\\w+) for (?<field2>\\w+) = \"(?<value1>.+?)\"( and date between " +
                "\"(?<after>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\" and " +
                "\"(?<before>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\")?";
        Pattern pattern = Pattern.compile(queryPattern);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String field1 = matcher.group("field1"),    //type for Set
                    field2 = matcher.group("field2"),   //type for value1
                    value1 = matcher.group("value1"),
                    after = matcher.group("after"),     //date after
                    before = matcher.group("before");   //date before

            switch (field2) {
                case "ip"     : set = getFieldByValue(field1, value1, null, null, null, null, getDate(after), getDate(before));         break;
                case "user"   : set = getFieldByValue(field1, null, value1, null, null, null, getDate(after), getDate(before));           break;
                case "date"   : set = getFieldByValue(field1, null, null, getDate(value1), null, null, getDate(after), getDate(before));  break;
                case "event"  : set = getFieldByValue(field1, null, null, null, getEvent(value1), null, getDate(after), getDate(before));  break;
                case "status" : set = getFieldByValue(field1, null, null, null, null, getStatus(value1), getDate(after), getDate(before)); break;
            }
        }
        return set;
    }

    private Set<Object> getFieldByValue(String returnType, String IP, String user, Date date, Event event, Status status, Date after, Date before) {
        Set<Object> fields = getSetWithReturnType(returnType);
        List<String[]> allLogLinesBetweenDates = getLinesFromLogFile(after, before);

        for (String[] line : allLogLinesBetweenDates){
            if (line[0].equals(IP)){
                switch (returnType) {
                    case "ip"     : fields.add(line[0]);                               break;
                    case "user"   : fields.add(line[1]);                               break;
                    case "date"   : fields.add(getDate(line[2]));                      break;
                    case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                    case "status" : fields.add(getStatus(line[4]));                    break;
                }
            } else if (line[1].equals(user)){
                switch (returnType) {
                    case "ip"     : fields.add(line[0]);                               break;
                    case "user"   : fields.add(line[1]);                               break;
                    case "date"   : fields.add(getDate(line[2]));                      break;
                    case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                    case "status" : fields.add(getStatus(line[4]));                    break;
                }
            } else if (Objects.equals(getDate(line[2]), date)){
                switch (returnType) {
                    case "ip"     : fields.add(line[0]);                               break;
                    case "user"   : fields.add(line[1]);                               break;
                    case "date"   : fields.add(getDate(line[2]));                      break;
                    case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                    case "status" : fields.add(getStatus(line[4]));                    break;
                }
            } else if (Objects.equals(getEvent(line[3].split(" ")[0]), event)) {
                switch (returnType) {
                    case "ip"     : fields.add(line[0]);                               break;
                    case "user"   : fields.add(line[1]);                               break;
                    case "date"   : fields.add(getDate(line[2]));                      break;
                    case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                    case "status" : fields.add(getStatus(line[4]));                    break;
                }
            } else if (Objects.equals(getStatus(line[4]), status)){
                switch (returnType) {
                    case "ip"     : fields.add(line[0]);                               break;
                    case "user"   : fields.add(line[1]);                               break;
                    case "date"   : fields.add(getDate(line[2]));                      break;
                    case "event"  : fields.add(getEvent(line[3].split(" ")[0])); break;
                    case "status" : fields.add(getStatus(line[4]));                    break;
                }
            }
        }
        return fields;
    }

    private Set getSetWithReturnType(String returnType) {
        switch (returnType) {
            case "ip"       : return new HashSet<String>();
            case "user"     : return new HashSet<String>();
            case "date"     : return new HashSet<Date>();
            case "event"    : return new HashSet<Event>();
            case "status"   : return new HashSet<Status>();
        }
        return new HashSet<>();
    }

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

    private Status getStatus(String statusStr) {
        switch (statusStr) {
            case "OK": return Status.OK;
            case "ERROR": return Status.ERROR;
            case "FAILED": return Status.FAILED;
        }
        return null;
    }

    private Date getDate(String string) {
        try {
            return simpleDateFormat.parse(string);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

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

    private List<File> getAllLogFiles(String packageName) {
        File dir = null;
        List<File> list = new ArrayList<>();
        try {
            dir = new File(URLDecoder.decode(packageName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File[] files = dir != null ? dir.listFiles() : new File[0];
        if (files.length != 0) {
            for (File f : files) {
                if (f.getAbsolutePath().toLowerCase().endsWith(".log")) {
                    list.add(f);
                }
            }
        }
        return list;
    }

    private List<String[]> getLinesFromLogFile(Date after, Date before) {
        List<String[]> lines = new ArrayList<>();

        for (File log : logFiles){
            try (BufferedReader br = new BufferedReader(new FileReader(log))) {
                String s;
                while ((s = br.readLine()) != null) {
                    String[] line = s.split("\t");
                    if (checkDate(line[2], after, before)) {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    private List<String> getAllLogLinesWithStreamAPI(Date after, Date before) {
        List<String> allDateLogLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<IP>\\d+\\.\\d+\\.\\d+\\.\\d+)\\s" +
                "(?<user>[a-zA-Z ]+)\\s" +
                "(?<date>\\d+\\.\\d+\\.\\d+ \\d+:\\d+:\\d+)\\s" +
                "(?<event>[A-Z_]+)\\s" +
                "(?<taskNumber>\\d+)\\s" +
                "(?<status>[A-Z_]+)");
        try {
            Files.list(logDir).filter(f -> f.toString().endsWith(".log"))
                    .collect(Collectors.toList())
                    .forEach(x -> {
                        try {
                            allDateLogLines.addAll(Files.readAllLines(x).stream()
                                    .filter(l -> checkDate(pattern.matcher(l).group("date"), after, before))
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