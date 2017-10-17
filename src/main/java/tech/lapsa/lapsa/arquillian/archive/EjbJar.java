package tech.lapsa.lapsa.arquillian.archive;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class EjbJar extends Jar {
    EjbJar(JavaArchive archive) {
	super(archive);
    }

    @Override
    public EjbJar dumpingTo(Consumer<String> dumper) {
	super.dumpingTo(dumper);
	return this;
    }
}
