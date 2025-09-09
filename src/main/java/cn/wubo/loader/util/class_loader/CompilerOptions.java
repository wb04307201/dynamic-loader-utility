package cn.wubo.loader.util.class_loader;

import java.util.ArrayList;
import java.util.List;

public class CompilerOptions {

    private List<String> options = new ArrayList<>();

    public static CompilerOptions create() {
        return new CompilerOptions();
    }

    public CompilerOptions sourceVersion(String version) {
        options.add("-source");
        options.add(version);
        return this;
    }

    public CompilerOptions targetVersion(String version) {
        options.add("-target");
        options.add(version);
        return this;
    }

    public CompilerOptions enableDebug() {
        options.add("-g");
        return this;
    }

    public CompilerOptions disableDebug() {
        options.add("-g:none");
        return this;
    }

    public CompilerOptions addOption(String option) {
        options.add(option);
        return this;
    }

    public CompilerOptions addOptions(String... opts) {
        for (String opt : opts) {
            options.add(opt);
        }
        return this;
    }

    public List<String> build() {
        return new ArrayList<>(options);
    }

}
