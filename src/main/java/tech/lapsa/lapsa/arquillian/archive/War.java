package tech.lapsa.lapsa.arquillian.archive;

import java.io.PrintStream;

import org.jboss.shrinkwrap.api.spec.WebArchive;

public class War {
    private final WebArchive archive;

    War(WebArchive archive) {
	this.archive = archive;
    }

    public WebArchive asWebArchive() {
	return archive;
    }

    public War dumpingTo(PrintStream ps) {
	ps.println(archive.toString(true));
	return this;
    }
}
