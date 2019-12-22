package com.javarush.task.task36.task3606;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/* 
Осваиваем ClassLoader и Reflection

Аргументом для класса Solution является абсолютный путь к пакету.
Имя пакета может содержать File.separator.
В этом пакете кроме скомпилированных классов (.class) могут находиться и другие файлы (например: .java).
Известно, что каждый класс имеет конструктор без параметров и реализует интерфейс HiddenClass.
Считай все классы с файловой системы, создай фабрику - реализуй метод getHiddenClassObjectByKey.
Примечание: в пакете может быть только один класс, простое имя которого начинается с String key без учета регистра.

Требования:

1. Реализуй метод scanFileSystem, он должен добавлять в поле hiddenClasses найденные классы.
2. Реализуй метод getHiddenClassObjectByKey, он должен создавать объект класса согласно условию задачи.
3. Метод main не изменяй.
4. Метод getHiddenClassObjectByKey не должен кидать исключений.
*/

public class Solution {
    private List<Class> hiddenClasses = new ArrayList<>();
    private String packageName;

    public Solution(String packageName) {
        this.packageName = packageName;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Solution solution = new Solution(Solution.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "com/javarush/task/task36/task3606/data/second");
        solution.scanFileSystem();
        System.out.println(solution.getHiddenClassObjectByKey("secondhiddenclassimpl"));
        System.out.println(solution.getHiddenClassObjectByKey("firsthiddenclassimpl"));
        System.out.println(solution.getHiddenClassObjectByKey("packa"));
    }

    public void scanFileSystem() throws ClassNotFoundException {
        MyClassLoader myClassLoader = new MyClassLoader();
        try {

            //get all files
            File dir = new File(URLDecoder.decode(packageName, "UTF-8"));
            File[] files = dir.listFiles();

            //filter
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    Class clazz = myClassLoader.loadClass(file.getAbsolutePath());
                    Constructor[] constructors = clazz.getDeclaredConstructors();

                    for (Constructor cons : constructors) {
                        if (cons.getParameterCount() == 0 && HiddenClass.class.isAssignableFrom(clazz)) {
                            hiddenClasses.add(clazz);
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public HiddenClass getHiddenClassObjectByKey(String key) {
        HiddenClass classByKey = null;

        //using key looking up class and create instance
        try {
            for (Class c : hiddenClasses) {
                if (c.getSimpleName().toLowerCase().equals(key.toLowerCase()) | c.getSimpleName().toLowerCase().startsWith(key.toLowerCase())) {
                    Constructor constructor = c.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    classByKey = (HiddenClass)constructor.newInstance();
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }
        return classByKey;
    }

    private static class MyClassLoader extends ClassLoader {

        //trying to make class from bytes massive
        @Override
        public Class<?> findClass(String name) {
            byte[] bytes = new byte[0];
            try {
                Path file = Paths.get(name);
                bytes = Files.readAllBytes(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return defineClass(null, bytes, 0, bytes.length);
        }
    }
}

