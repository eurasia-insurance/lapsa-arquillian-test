package tech.lapsa.lapsa.arquillian.archive;

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

public class Ear {
    private final EnterpriseArchive archive;

    Ear(EnterpriseArchive archive) {
	this.archive = archive;
    }

    public EnterpriseArchive asEnterpriseArchive() {
	return archive;
    }
}
