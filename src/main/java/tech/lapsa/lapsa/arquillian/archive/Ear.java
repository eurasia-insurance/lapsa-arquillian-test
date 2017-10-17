package tech.lapsa.lapsa.arquillian.archive;

import java.io.PrintStream;

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

public class Ear {
    private final EnterpriseArchive archive;

    Ear(EnterpriseArchive archive) {
	this.archive = archive;
    }

    public EnterpriseArchive asEnterpriseArchive() {
	return archive;
    }

    public Ear dumpingTo(PrintStream ps) {
	ps.println(archive.toString(true));
	return this;
    }
}
