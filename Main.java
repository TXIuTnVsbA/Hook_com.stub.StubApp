import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String appName = "com.lightsky.video";
        String packageName = "com.stub";
        String className = "StubApp";
        String methodName = "getOrigApplicationContext";
        if (lpparam.packageName.equals(appName)) {
            XposedBridge.log("hook壳开始\n");
            Class clazz = lpparam.classLoader.loadClass(packageName + "." + className);
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getReturnType().toString().contains("Context")) {
                    XposedBridge.log("Debug:\t" + method.getName() + "\n");
                }
            }
            try {
                XposedHelpers.findAndHookMethod(clazz, methodName, Context.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //获取到360的Context对象，通过这个对象来获取classloader
                        Context context = (Context) param.args[0];
                        //获取360的classloader，之后hook加固后的就使用这个classloader
                        ClassLoader classLoader = context.getClassLoader();
                        //classloader修改成360的classloader
                        Class cls = classLoader.loadClass("com.lightsky.video.MainActivity");
                        XposedBridge.log("=====================================\n");
                        XposedBridge.log(cls.getName());
                        Method[] methods = cls.getDeclaredMethods();
                        for (int i = 0; i < methods.length; i++) {
                            XposedBridge.log(methods[i].getName());
                        }
                        XposedBridge.log("=====================================\n");
                        super.afterHookedMethod(param);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            XposedBridge.log("hook壳结束\n");
        }
    }
}

