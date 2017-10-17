package tech.lapsa.lapsa.arquillian.archive;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class Jar {
    private final JavaArchive archive;

    Jar(JavaArchive archive) {
	this.archive = archive;
    }

    public JavaArchive asJavaArchive() {
	return archive;
    }

    public Jar dumpingTo(Consumer<String> dumper) {
	dumper.accept(archive.toString(true));
	return this;
    }
}
