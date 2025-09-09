package cn.wubo.dynamic.loader.utility.aspect;

import org.springframework.cglib.proxy.Enhancer;

public class DynamicAspect {

        /**
     * 创建代理对象，将目标对象和切面对象进行代理包装
     *
     * @param <T> 目标对象的类型
     * @param <E> 切面对象的类型，必须实现IAspect接口
     * @param target 目标对象，需要被代理的实际对象
     * @param aspectTarget 切面对象，用于处理代理逻辑的对象
     * @return 返回代理后的对象，类型与目标对象相同
     */
    public static <T, E extends IAspect> T proxy(T target, E aspectTarget) {
        // 创建CGLIB增强器实例
        Enhancer enhancer = new Enhancer();
        // 设置代理对象的父类为target的类
        enhancer.setSuperclass(target.getClass());
        // 设置回调处理器，用于处理代理逻辑
        enhancer.setCallback(new AspectHandler(target, aspectTarget));
        // 创建并返回代理对象
        return (T) enhancer.create();
    }

}
