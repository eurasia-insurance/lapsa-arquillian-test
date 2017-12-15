package tech.lapsa.lapsa.arquillian.archive;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

public class Ear {
    private final EnterpriseArchive archive;

    Ear(final EnterpriseArchive archive) {
	this.archive = archive;
    }

    public EnterpriseArchive asEnterpriseArchive() {
	return archive;
    }

    public Ear dumpingTo(final Consumer<String> dumper) {
	dumper.accept(archive.toString(true));
	return this;
    }
}
