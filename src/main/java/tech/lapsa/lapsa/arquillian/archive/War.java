package tech.lapsa.lapsa.arquillian.archive;

import org.jboss.shrinkwrap.api.spec.WebArchive;

public class War {
    private final WebArchive archive;

    War(WebArchive archive) {
	this.archive = archive;
    }

    public WebArchive asWebArchive() {
	return archive;
    }

}
