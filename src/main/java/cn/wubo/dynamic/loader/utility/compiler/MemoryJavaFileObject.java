package cn.wubo.dynamic.loader.utility.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class MemoryJavaFileObject extends SimpleJavaFileObject{

    private final String javaSourceCode;

    public MemoryJavaFileObject(String name, String javaSourceCode) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
        this.javaSourceCode = javaSourceCode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return javaSourceCode;
    }
}

