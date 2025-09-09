package cn.wubo.loader.util.class_loader;

import cn.wubo.loader.util.exception.LoaderRuntimeException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DynamicCompiler {

    public static String parseClassName(String sourceCode) {
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElse("");

        String className = compilationUnit.getTypes().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No className found in sourceCode"))
                .getNameAsString();

        return packageName.isEmpty() ? className : packageName + "." + className;
    }

    public static void compile(String className, String sourceCode, List<String> options) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Failed to get Java compiler. Please ensure you are running in a JDK environment.");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new MemoryJavaFileObject(className, sourceCode));

        JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        MemoryFileManager<JavaFileManager> memoryFileManager = new MemoryFileManager<>(fileManager);

        JavaCompiler.CompilationTask task = compiler.getTask(
                null, memoryFileManager, diagnostics, options, null, compilationUnits);

        boolean success = task.call();
        fileManager.close();

        if (!success) {
            throw new LoaderRuntimeException("dynamic compiler fail:\n" + diagnostics.getDiagnostics());
        }
    }

    public static Class<?> load(String className) throws ClassNotFoundException {
        return ByteArrayClassLoader.getInstance().findClass(className);
    }



    public static Class<?> compileAndLoad(String sourceCode) throws IOException, ClassNotFoundException {
        return compileAndLoad(sourceCode, (List<String>)null);
    }

    public static Class<?> compileAndLoad(String sourceCode, List<String> options) throws IOException, ClassNotFoundException {
        String className = parseClassName(sourceCode);
        compile(className, sourceCode, options);
        return load(className);
    }

    public static Class<?> compileAndLoad(String sourceCode, CompilerOptions options) throws IOException, ClassNotFoundException {
        return compileAndLoad(sourceCode, options.build());
    }

    public static void addJarPath(String jarPath) {
        try {
            ByteArrayClassLoader.getInstance().addURL(new URL("jar:file:" + new File(jarPath).getAbsolutePath() + "!/"));
        } catch (MalformedURLException e) {
            throw new LoaderRuntimeException(e.getMessage(), e);
        }
    }
}
