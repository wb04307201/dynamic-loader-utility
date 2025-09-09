package cn.wubo.loader.util.class_loader;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MemoryClassFileObject extends SimpleJavaFileObject{

    private final String name;

	public MemoryClassFileObject(String name) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.CLASS.extension),
                Kind.CLASS);
        this.name = name;
	}

    @Override
    public OutputStream openOutputStream() {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                ByteArrayClassLoader.getInstance().addClass(name, this.toByteArray());
            }
        };
    }
}

