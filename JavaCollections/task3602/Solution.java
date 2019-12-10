package task3602;

import java.lang.reflect.*;
import java.util.Collections;

/* 
Найти класс по описанию

Условие
Описание класса:
1. Реализует интерфейс List;
2. Является приватным статическим классом внутри популярного утилитного класса;
3. Доступ по индексу запрещен - кидается исключение IndexOutOfBoundsException.
Используя рефлекшн (метод getDeclaredClasses), верни подходящий тип в методе getExpectedClass.


Требования:
1. Метод getExpectedClass должен использовать метод getDeclaredClasses подходящего утилитного класса.
2. Метод getExpectedClass должен вернуть правильный тип.
3. Метод main должен вызывать метод getExpectedClass.
4. Метод main должен вывести полученный класс на экран.

Алгоритм:

1) Импорт в виде Collections (он изначально есть) намекает нам где искать.
2) Получаем (getDeclaredClasses) у Collections и обходим все классы у него.
3) Для каждого класса проверяем:
3.1)	Имплементирует ли этот класс или его родитель (getSuperclass) интерфейс List (getInterfaces). Тут сразу можно рефлексией взять (forName()) List по полному имени.
3.2)	Является ли этот класс приватным (Modifier.isPrivate(clazz.getModifiers())).
3.3)	Является ли этот класс статическим (Modifier.isStatic(clazz.getModifiers())).
3.4)	Получаем метод get (getDeclaredMethod("get", int.class)) и устанавливаем ему доступность setAccessible(true).
3.5)	Аналогично с конструктор (getDeclaredConstructor) и (setAccessible(true)).
3.6)	Выполняем метод (get.invoke()) с созданным новым инстансом через конструктор (newInstance()) и любым параметром int
4) Если отловили InvocationTargetException и в getCause() содержится (contains) "IndexOutOfBoundsException" – это наш класс, его и возвращаем. Остальные catch можно оставить пустыми.
*/
public class Solution {
    public static void main(String[] args) {
        System.out.println(getExpectedClass());
    }

    public static Class getExpectedClass() {
        Class<?>[] classes = Collections.class.getDeclaredClasses();
        Class<?> list = null;
        try {
            list = Class.forName("java.util.List");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Class c : classes) {
            if (list.isAssignableFrom(c) && list.isAssignableFrom(c.getSuperclass())) {
                if (Modifier.isPrivate(c.getModifiers()) & Modifier.isStatic(c.getModifiers())) {
                    try {
                        Method get = c.getDeclaredMethod("get", int.class);
                        Constructor constructor = c.getDeclaredConstructor();

                        get.setAccessible(true);
                        constructor.setAccessible(true);

                        get.invoke(constructor.newInstance(), 0);
                    } catch (InvocationTargetException e) {
                        if (e.getCause().toString().contains("IndexOutOfBoundsException"))
                            return c;
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {

                    }
                }
            }
        }
        return null;
    }
}
