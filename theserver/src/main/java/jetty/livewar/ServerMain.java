package jetty.livewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class ServerMain {
    enum OperationalMode {UNKNOWN, DEV, PROD}

    private Path basePath;

    public static void main(String[] args) {
        try {
//            URLClassLoader currentCl = (URLClassLoader) Thread.currentThread().getContextClassLoader();
//            URL[] urls = Arrays.stream(currentCl.getURLs()).filter(url ->
//                !url.getPath().contains("jre/lib")).toArray(URL[]::new
//            );
//            Thread.currentThread().setContextClassLoader(new URLClassLoader(urls, currentCl.getParent()));
            new ServerMain().run();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void run() throws Throwable {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
//        WebAppClassLoader classLoader = new WebAppClassLoader(context);
//        context.setClassLoader(classLoader);
        context.setParentLoaderPriority(true);
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*");
        context.setInitParameter("resteasy.injector.factory", "org.jboss.resteasy.cdi.CdiInjectorFactory");
        context.setConfigurations(new Configuration[]{
            new AnnotationConfiguration(),
            new WebXmlConfiguration(),
            new WebInfConfiguration(),
            new PlusConfiguration(),
            new MetaInfConfiguration(),
            new FragmentConfiguration(),
            new EnvConfiguration()
        });

        switch (getOperationalMode()) {
            case PROD:
                // Configure as WAR
                context.setWar(basePath.toString());
                context.getMetaData().addContainerResource(new PathResource(basePath));
//                context.setExtraClasspath(basePath + "!WEB-INF/jetty-server/");
                break;
            case DEV:
                context.setBaseResource(new PathResource(basePath.resolve("target/thewebapp")));
                break;
            default:
                throw new FileNotFoundException("Unable to configure WebAppContext base resource undefined");
        }

        server.setHandler(context);

        server.start();
        server.dumpStdErr();
        server.join();

    }

    private OperationalMode getOperationalMode() throws IOException {
        String warLocation = System.getProperty("org.eclipse.jetty.livewar.LOCATION");
        if (warLocation != null) {
            Path warPath = new File(warLocation).toPath().toRealPath();
            if (Files.exists(warPath) && Files.isRegularFile(warPath)) {
                this.basePath = warPath;
                return OperationalMode.PROD;
            }
        }

        Path devPath = new File("thewebapp").toPath().toRealPath();
        if (Files.exists(devPath) && Files.isDirectory(devPath)) {
            this.basePath = devPath;
            return OperationalMode.DEV;
        }

        return OperationalMode.UNKNOWN;
    }
}
