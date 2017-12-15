package tech.lapsa.lapsa.arquillian.archive;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.spec.WebArchive;

public class War {
    private final WebArchive archive;

    War(final WebArchive archive) {
	this.archive = archive;
    }

    public WebArchive asWebArchive() {
	return archive;
    }

    public War dumpingTo(final Consumer<String> dumper) {
	dumper.accept(archive.toString(true));
	return this;
    }
}
