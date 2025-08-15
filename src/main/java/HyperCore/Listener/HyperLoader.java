package HyperCore.Listener;

import HyperCore.Listener.Hyper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class HyperLoader {
    public static void loadAll(Plugin plugin) {
        try {
            String basePackage = "HyperCore";
            String pathPrefix = basePackage.replace('.', '/');

            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            File jarFile = new File(jarUrl.toURI().getSchemeSpecificPart());
            JarFile jar = new JarFile(jarFile);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.endsWith(".class") && name.startsWith(pathPrefix)) {
                    String className = name.replace('/', '.').replace(".class", "");
                    Class<?> clazz = Class.forName(className);

                    if (clazz != Hyper.class
                            && Hyper.class.isAssignableFrom(clazz)
                            && !clazz.isInterface()
                            && !clazz.isEnum()
                            && !clazz.isAnnotation()
                            && !clazz.isAnonymousClass()
                            && !clazz.isSynthetic()
                            && !clazz.getName().contains("Companion")) {

                        try {
                            Object instance = clazz.getDeclaredConstructor().newInstance();
                            if (instance instanceof Listener) {
                                Bukkit.getPluginManager().registerEvents((Listener) instance, plugin);
                                plugin.getLogger().info("Hyper 등록됨: " + className);
                            }
                        } catch (NoSuchMethodException e) {
                            plugin.getLogger().warning("기본 생성자 없음: " + className);
                        } catch (Exception e) {
                            plugin.getLogger().warning("인스턴스 생성 실패: " + className);
                            e.printStackTrace();
                        }
                    }
                }
            }

            jar.close();

        } catch (URISyntaxException e) {
            plugin.getLogger().severe("JAR 경로 처리 실패: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().severe("HyperLoader 에러 발생:");
            e.printStackTrace();
        }
    }
}
