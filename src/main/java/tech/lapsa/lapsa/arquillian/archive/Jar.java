package tech.lapsa.lapsa.arquillian.archive;

import java.io.PrintStream;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class Jar {
    private final JavaArchive archive;

    Jar(JavaArchive archive) {
	this.archive = archive;
    }

    public JavaArchive asJavaArchive() {
	return archive;
    }

    public Jar dumpingTo(PrintStream ps) {
	ps.println(archive.toString(true));
	return this;
    }
}
