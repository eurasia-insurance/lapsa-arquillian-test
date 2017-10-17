package tech.lapsa.lapsa.arquillian.archive;

import java.io.PrintStream;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class EjbJar extends Jar {
    EjbJar(JavaArchive archive) {
	super(archive);
    }

    @Override
    public EjbJar dumpingTo(PrintStream ps) {
	super.dumpingTo(ps);
	return this;
    }
}
