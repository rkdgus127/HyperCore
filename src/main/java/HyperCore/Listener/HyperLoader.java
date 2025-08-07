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

            // JAR 파일 경로 처리
            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            File jarFile = new File(jarUrl.toURI().getSchemeSpecificPart());
            JarFile jar = new JarFile(jarFile);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // 클래스 파일이고 우리가 지정한 패키지에 속하면
                if (name.endsWith(".class") && name.startsWith(pathPrefix)) {
                    String className = name
                            .replace('/', '.')
                            .replace(".class", "");

                    Class<?> clazz = Class.forName(className);

                    if (Hyper.class.isAssignableFrom(clazz)
                            && !clazz.isInterface()
                            && !clazz.isEnum()
                            && !clazz.isAnnotation()
                            && !clazz.isAnonymousClass()) {

                        Object instance = clazz.getDeclaredConstructor().newInstance();

                        if (instance instanceof Listener) {
                            Bukkit.getPluginManager().registerEvents((Listener) instance, plugin);
                            plugin.getLogger().info("Hyper 등록됨: " + className);
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
