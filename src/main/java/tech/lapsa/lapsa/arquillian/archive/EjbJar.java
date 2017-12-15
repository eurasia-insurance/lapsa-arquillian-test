package tech.lapsa.lapsa.arquillian.archive;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class EjbJar extends Jar {
    EjbJar(final JavaArchive archive) {
	super(archive);
    }

    @Override
    public EjbJar dumpingTo(final Consumer<String> dumper) {
	super.dumpingTo(dumper);
	return this;
    }
}
