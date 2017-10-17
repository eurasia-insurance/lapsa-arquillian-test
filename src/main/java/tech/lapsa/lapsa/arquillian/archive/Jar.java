package tech.lapsa.lapsa.arquillian.archive;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class Jar {
    private final JavaArchive archive;

    Jar(JavaArchive archive) {
	this.archive = archive;
    }

    public JavaArchive asJavaArchive() {
	return archive;
    }
}
