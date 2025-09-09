package cn.wubo.loader.util.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

public class MemoryFileManager<T extends JavaFileManager> extends ForwardingJavaFileManager<T> {

    protected MemoryFileManager(T fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String name,
                                               JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new MemoryClassFileObject(name);
        }
        return super.getJavaFileForOutput(location, name, kind, sibling);
    }
}
