package cn.wubo.dynamic.loader.utility.compiler;

import cn.wubo.dynamic.loader.utility.exception.CompilerRuntimeException;
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

    /**
     * 解析Java源代码中的类名（包括包名）
     *
     * @param sourceCode Java源代码字符串
     * @return 完整的类名（包含包名前缀），如果无包名则只返回类名
     * @throws IllegalArgumentException 当源代码中找不到类定义时抛出
     */
    public static String parseClassName(String sourceCode) {
        // 解析源代码为编译单元
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        // 获取包名，如果不存在则返回空字符串
        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElse("");

        // 获取第一个类型声明的类名
        String className = compilationUnit.getTypes().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No className found in sourceCode"))
                .getNameAsString();

        // 根据是否有包名返回完整的类名
        return packageName.isEmpty() ? className : packageName + "." + className;
    }


    /**
     * 编译Java源代码
     *
     * @param className  类名
     * @param sourceCode 源代码内容
     * @param options    编译选项列表
     * @throws IOException 当编译过程中发生IO异常时抛出
     */
    public static void compile(String className, String sourceCode, List<String> options) throws IOException {
        // 获取系统Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Failed to get Java compiler. Please ensure you are running in a JDK environment.");
        }

        // 创建诊断收集器和编译单元
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new MemoryJavaFileObject(className, sourceCode));

        // 创建文件管理器和内存文件管理器
        JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        MemoryFileManager<JavaFileManager> memoryFileManager = new MemoryFileManager<>(fileManager);

        // 创建并执行编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, memoryFileManager, diagnostics, options, null, compilationUnits);

        boolean success = task.call();
        fileManager.close();

        // 如果编译失败，收集错误信息并抛出运行时异常
        if (!success) {
            String errorMessage = diagnostics.getDiagnostics().stream().map(Object::toString).reduce("", (acc, x) -> acc + "\r\n" + x);
            throw new CompilerRuntimeException(errorMessage);
        }
    }


    /**
     * 加载指定类名的类
     *
     * @param className 要加载的类的全限定名
     * @return 加载成功的Class对象
     * @throws ClassNotFoundException 当找不到指定类时抛出此异常
     */
    public static Class<?> load(String className) throws ClassNotFoundException {
        return ByteArrayClassLoader.getInstance().findClass(className);
    }


    /**
     * 编译并加载Java源代码
     *
     * @param sourceCode 要编译和加载的Java源代码字符串
     * @return 编译并加载后得到的Class对象
     * @throws IOException            当编译过程中发生IO错误时抛出
     * @throws ClassNotFoundException 当无法找到或加载编译后的类时抛出
     */
    public static Class<?> compileAndLoad(String sourceCode) throws IOException, ClassNotFoundException {
        return compileAndLoad(sourceCode, CompilerOptions.create());
    }


    /**
     * 编译并加载Java源代码
     *
     * @param sourceCode 要编译的Java源代码字符串
     * @param options    编译器选项配置
     * @return 编译并加载成功的Class对象
     * @throws IOException            当编译过程中发生IO错误时抛出
     * @throws ClassNotFoundException 当无法找到或加载编译后的类时抛出
     */
    public static Class<?> compileAndLoad(String sourceCode, CompilerOptions options) throws IOException, ClassNotFoundException {
        String className = parseClassName(sourceCode);
        compile(className, sourceCode, options.build());
        return load(className);
    }


    /**
     * 添加JAR文件路径到类加载器中
     *
     * @param jarPath JAR文件的路径字符串
     */
    public static void addJarPath(String jarPath) {
        try {
            // 将JAR文件路径转换为URL格式并添加到字节数组类加载器中
            ByteArrayClassLoader.getInstance().addURL(new URL("jar:file:" + new File(jarPath).getAbsolutePath() + "!/"));
        } catch (MalformedURLException e) {
            throw new CompilerRuntimeException(e.getMessage(), e);
        }
    }

}
