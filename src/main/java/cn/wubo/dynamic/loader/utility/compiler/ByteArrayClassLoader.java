package cn.wubo.dynamic.loader.utility.compiler;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ByteArrayClassLoader extends URLClassLoader {

    private static final Map<String, byte[]> classes = new ConcurrentHashMap<>();

    public ByteArrayClassLoader(ClassLoader parent) {
        super(new URL[0],parent);
    }

    private static class SingletonHolder {
        private static final ByteArrayClassLoader INSTANCE =
                new ByteArrayClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public static ByteArrayClassLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }

        byte[] bytes = classes.get(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }

    // 提供清理方法
    public void clear() {
        classes.clear();
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
