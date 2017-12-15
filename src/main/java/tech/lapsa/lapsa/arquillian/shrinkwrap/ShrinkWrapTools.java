package tech.lapsa.lapsa.arquillian.shrinkwrap;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

public class ShrinkWrapTools {

    public static final ShrinkWrapTools getInstance() {
	return new ShrinkWrapTools();
    }

    private static PomEquippedResolveStage pomResolveStage;

    public static EnterpriseArchive createEAR() {
	return ShrinkWrap.create(EnterpriseArchive.class);
    }

    public static EnterpriseArchive createEAR(final String archiveName) {
	return ShrinkWrap.create(EnterpriseArchive.class, archiveName);
    }

    public static JavaArchive createEJB() {
	return createJAR();
    }

    public static JavaArchive createEJB(final String archiveName) {
	return createJAR(archiveName);
    }

    public static JavaArchive createJAR() {
	return ShrinkWrap.create(JavaArchive.class);
    }

    public static JavaArchive createJAR(final String archiveName) {
	return ShrinkWrap.create(JavaArchive.class, archiveName);
    }

    public static WebArchive createWAR() {
	return ShrinkWrap.create(WebArchive.class);
    }

    public static WebArchive createWAR(final String archiveName) {
	return ShrinkWrap.create(WebArchive.class, archiveName);
    }

    public static void earAddDependencyArtifactWithDependencies(final EnterpriseArchive ear,
	    final MavenArtifact... artifactCanonicalForm) {
	initPomResolveStage();
	for (final MavenArtifact canonicalForm : artifactCanonicalForm) {
	    final MavenResolvedArtifact[] mars = pomResolveStage
		    .resolve(canonicalForm.canonicalForm())
		    .withTransitivity()
		    .asResolvedArtifact();
	    for (final MavenResolvedArtifact mar : mars)
		earAddMavenResolvedArtifact(ear, mar);
	}
    }

    public static void earAddDependencyArtifact(final EnterpriseArchive ear,
	    final MavenArtifact... artifactCanonicalForm) {
	initPomResolveStage();
	for (final MavenArtifact canonicalForm : artifactCanonicalForm) {
	    final MavenResolvedArtifact[] mars = pomResolveStage
		    .resolve(canonicalForm.canonicalForm())
		    .withoutTransitivity()
		    .asResolvedArtifact();
	    for (final MavenResolvedArtifact mar : mars)
		earAddMavenResolvedArtifact(ear, mar);
	}
    }

    public static MavenArtifact toMavenArtifiact(final String groupId, final String artifactId,
	    final MavenArtifactType packagingType) {
	return new MavenArtifact(groupId, artifactId, packagingType);
    }

    public static void earAddRuntimeDependencies(final EnterpriseArchive ear) {
	initPomResolveStage();
	final MavenResolvedArtifact[] mars = pomResolveStage
		.importCompileAndRuntimeDependencies()
		.resolve()
		.withTransitivity()
		.asResolvedArtifact();
	for (final MavenResolvedArtifact mar : mars)
	    earAddMavenResolvedArtifact(ear, mar);
    }

    public static void earAddTestDependencies(final EnterpriseArchive ear) {
	initPomResolveStage();
	final MavenResolvedArtifact[] mars = pomResolveStage
		.importTestDependencies()
		.resolve()
		.withTransitivity()
		.asResolvedArtifact();
	for (final MavenResolvedArtifact mar : mars)
	    earAddMavenResolvedArtifact(ear, mar);
    }

    public static void warAddWebinfFolderRecursive(final WebArchive war) {
	warAddResources(war, new File("src/main/webapp/WEB-INF"), "/", true, false);
    }

    public static void jarAddManifestFolderRecursive(final JavaArchive jar) {
	jarAddResources(jar, new File("src/main/resources/META-INF"), "/", true, false);
    }

    public static void jarAddTestManifestFolerRecursive(final JavaArchive jar) {
	jarAddResources(jar, new File("src/test/resources/META-INF"), "/", true, false);
    }

    public static void jarAddAsResroucesRecursive(final JavaArchive jar, final File root, final String target) {
	jarAddResources(jar, root, target, true, true);
    }

    public static void jarAddAsResroucesRecursive(final JavaArchive jar, final File root) {
	jarAddResources(jar, root, "/", true, true);
    }

    public static void warAddAsResroucesRecursive(final WebArchive war, final File root, final String target) {
	warAddResources(war, root, target, true, true);
    }

    public static void warAddAsResroucesRecursive(final WebArchive war, final File root) {
	warAddResources(war, root, "/", true, true);
    }

    public static void jarAddAsResroucesNonRecursive(final JavaArchive jar, final File root, final String target) {
	jarAddResources(jar, root, target, false, true);
    }

    public static void jarAddAsResroucesNonRecursive(final JavaArchive jar, final File root) {
	jarAddResources(jar, root, "/", false, true);
    }

    public static enum MavenArtifactType {
	WAR("war"), //
	JAR("jar"), //
	EJB("ejb"), //
	EAR("ear"), //
	;
	private final String mavenPackagingType;

	private MavenArtifactType(final String mavenPackagingType) {
	    this.mavenPackagingType = mavenPackagingType;
	}

	public static MavenArtifactType forType(final String mavenPackagingType) {
	    for (final MavenArtifactType mat : MavenArtifactType.values())
		if (mat.mavenPackagingType.equals(mavenPackagingType))
		    return mat;
	    return null;
	}
    }

    public static class MavenArtifact {
	private final String groupId;
	private final String artifactId;
	private final MavenArtifactType packagingType;

	private MavenArtifact(final String groupId, final String artifactId, final MavenArtifactType packagingType) {
	    this.groupId = groupId;
	    this.artifactId = artifactId;
	    this.packagingType = packagingType;
	}

	// G:A:P:C:V

	public String canonicalForm() {
	    return String.format("%1$s:%2$s:%3$s:?", groupId, artifactId, packagingType.mavenPackagingType);
	}

	@Override
	public String toString() {
	    return canonicalForm();
	}
    }

    // PRIVATE

    private static void earAddMavenResolvedArtifact(final EnterpriseArchive ear, final MavenResolvedArtifact mar) {
	final MavenCoordinate co = mar.getCoordinate();
	if (co.getType().equals(PackagingType.JAR)) {
	    final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, co.getArtifactId() + ".jar");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsLibrary(archive);
	}
	if (co.getType().equals(PackagingType.EJB)) {
	    final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, co.getArtifactId() + ".jar");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsModule(archive);
	}
	if (co.getType().equals(PackagingType.WAR)) {
	    final WebArchive archive = ShrinkWrap.create(WebArchive.class, co.getArtifactId() + ".war");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsModule(archive);
	}
    }

    private static void jarAddResources(final JavaArchive jar, final File file, final String targetPath,
	    final boolean recursive,
	    final boolean resourceOrManifest) {
	if (file == null)
	    throw new NullPointerException();
	if (!file.exists() || !file.isDirectory())
	    throw new RuntimeException(String.format("%1$s must be a directory", file));
	final String sub = (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
	for (final File f : file.listFiles())
	    if (f.isFile()) {
		if (resourceOrManifest)
		    jar.addAsResource(f, sub + f.getName());
		else
		    jar.addAsManifestResource(f, sub + f.getName());
	    } else if (f.isDirectory() && recursive)
		jarAddResources(jar, f, sub + f.getName() + "/", recursive, resourceOrManifest);
    }

    private static void warAddResources(final WebArchive war, final File file, final String targetPath,
	    final boolean recursive,
	    final boolean resourceOrManifest) {
	if (file == null)
	    throw new NullPointerException();
	if (!file.exists() || !file.isDirectory())
	    throw new RuntimeException(String.format("%1$s must be a directory", file));
	final String sub = (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
	for (final File f : file.listFiles())
	    if (f.isFile()) {
		if (resourceOrManifest)
		    war.addAsResource(f, sub + f.getName());
		else
		    war.addAsWebInfResource(f, sub + f.getName());
	    } else if (f.isDirectory() && recursive)
		warAddResources(war, f, sub + f.getName() + "/", recursive, resourceOrManifest);
    }

    private static void initPomResolveStage() {
	if (pomResolveStage == null)
	    pomResolveStage = Maven
		    .resolver()
		    .loadPomFromFile("pom.xml");
    }

}
