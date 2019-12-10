package task3507;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/*
ClassLoader - что это такое?

Реализуй логику метода getAllAnimals.
Аргумент метода pathToAnimals - это абсолютный путь к директории, в которой хранятся скомпилированные классы.
Путь не обязательно содержит / в конце.
НЕ все классы наследуются от интерфейса Animal.
НЕ все классы имеют публичный конструктор без параметров.
Только для классов, которые наследуются от Animal и имеют публичный конструктор без параметров, - создать по одному объекту.
Добавить созданные объекты в результирующий сет и вернуть.
Метод main не участвует в тестировании.

Требования:
1.	Размер множества возвращаемого методом getAllAnimals должен быть равен количеству классов поддерживающих интерфейс Animal и имеющих публичный конструктор без параметров (среди классов расположенных в директории переданной в качестве параметра).
2.	В множестве возвращаемом методом getAllAnimals должны присутствовать все классы поддерживающие интерфейс Animal и имеющие публичный конструктор без параметров (среди классов расположенных в директории переданной в качестве параметра).
3.	В множестве возвращаемом методом getAllAnimals НЕ должен присутствовать ни один класс не поддерживающий интерфейс Animal или не имеющий публичного конструктора без параметров (среди классов расположенных в директории переданной в качестве параметра).
4.	Метод getAllAnimals должен быть статическим.
*/
public class Solution {
    public static void main(String[] args) {
        Set<? extends Animal> allAnimals =
                getAllAnimals(Solution.class.getProtectionDomain().getCodeSource().getLocation().getPath() //path to Solution.class
                        + Solution.class.getPackage().getName().replaceAll("[.]", "/") + "/data"); //path to another packages in Solution package
        System.out.println(allAnimals);
    }

    public static Set<? extends Animal> getAllAnimals(String pathToAnimals) {
        Set<Animal> animals = new HashSet<>();
        MyClassLoader myClassLoader = new MyClassLoader();

        //get system path separator and add it in the end of the path
        String sep = System.getProperty("file.separator");
        if(!pathToAnimals.endsWith("/"))
            pathToAnimals += sep;

        try {
            //get all dirs and files
            File dir = new File(URLDecoder.decode(pathToAnimals, "UTF-8"));
            File[] files = dir.listFiles();

            //chose only file that end on .class
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    //getting class
                    Class clazz = myClassLoader.loadClass(file.getAbsolutePath());

                    //get all constructor for clazz and try to find only PUBLIC without parameters
                    Constructor[] con = clazz.getConstructors();
                    for (Constructor c : con) {
                        if (c.getModifiers() == Modifier.PUBLIC && c.getParameterCount() == 0) {

                            //checking on interface implementation
                            if (Animal.class.isAssignableFrom(clazz)) {
                                animals.add((Animal) c.newInstance());
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return animals;
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
/*
Output:
[task3507.data.Cat@1540e19d]
 */