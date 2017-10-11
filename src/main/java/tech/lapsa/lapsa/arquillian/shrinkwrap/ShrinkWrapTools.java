package com.lapsa.lapsa.arquilliantools;

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

    public static EnterpriseArchive createEAR(String archiveName) {
	return ShrinkWrap.create(EnterpriseArchive.class, archiveName);
    }

    public static JavaArchive createEJB() {
	return createJAR();
    }

    public static JavaArchive createEJB(String archiveName) {
	return createJAR(archiveName);
    }

    public static JavaArchive createJAR() {
	return ShrinkWrap.create(JavaArchive.class);
    }

    public static JavaArchive createJAR(String archiveName) {
	return ShrinkWrap.create(JavaArchive.class, archiveName);
    }

    public static WebArchive createWAR() {
	return ShrinkWrap.create(WebArchive.class);
    }

    public static WebArchive createWAR(String archiveName) {
	return ShrinkWrap.create(WebArchive.class, archiveName);
    }

    public static void earAddDependencyArtifactWithDependencies(EnterpriseArchive ear, MavenArtifact... artifactCanonicalForm) {
	initPomResolveStage();
	for (MavenArtifact canonicalForm : artifactCanonicalForm) {
	    MavenResolvedArtifact[] mars = pomResolveStage
		    .resolve(canonicalForm.canonicalForm())
		    .withTransitivity()
		    .asResolvedArtifact();
	    for (MavenResolvedArtifact mar : mars)
		earAddMavenResolvedArtifact(ear, mar);
	}
    }

    public static void earAddDependencyArtifact(EnterpriseArchive ear, MavenArtifact... artifactCanonicalForm) {
	initPomResolveStage();
	for (MavenArtifact canonicalForm : artifactCanonicalForm) {
	    MavenResolvedArtifact[] mars = pomResolveStage
		    .resolve(canonicalForm.canonicalForm())
		    .withoutTransitivity()
		    .asResolvedArtifact();
	    for (MavenResolvedArtifact mar : mars)
		earAddMavenResolvedArtifact(ear, mar);
	}
    }

    public static MavenArtifact toMavenArtifiact(String groupId, String artifactId, MavenArtifactType packagingType) {
	return new MavenArtifact(groupId, artifactId, packagingType);
    }

    public static void earAddRuntimeDependencies(EnterpriseArchive ear) {
	initPomResolveStage();
	MavenResolvedArtifact[] mars = pomResolveStage
		.importCompileAndRuntimeDependencies()
		.resolve()
		.withTransitivity()
		.asResolvedArtifact();
	for (MavenResolvedArtifact mar : mars)
	    earAddMavenResolvedArtifact(ear, mar);
    }

    public static void earAddTestDependencies(EnterpriseArchive ear) {
	initPomResolveStage();
	MavenResolvedArtifact[] mars = pomResolveStage
		.importTestDependencies()
		.resolve()
		.withTransitivity()
		.asResolvedArtifact();
	for (MavenResolvedArtifact mar : mars)
	    earAddMavenResolvedArtifact(ear, mar);
    }

    public static void warAddWebinfFolderRecursive(WebArchive war) {
	warAddResources(war, new File("src/main/webapp/WEB-INF"), "/", true, false);
    }

    public static void jarAddManifestFolderRecursive(JavaArchive jar) {
	jarAddResources(jar, new File("src/main/resources/META-INF"), "/", true, false);
    }

    public static void jarAddTestManifestFolerRecursive(JavaArchive jar) {
	jarAddResources(jar, new File("src/test/resources/META-INF"), "/", true, false);
    }
    
    public static void jarAddAsResroucesRecursive(JavaArchive jar, File root, String target) {
	jarAddResources(jar, root, target, true, true);
    }

    public static void jarAddAsResroucesRecursive(JavaArchive jar, File root) {
	jarAddResources(jar, root, "/", true, true);
    }

    public static void warAddAsResroucesRecursive(WebArchive war, File root, String target) {
	warAddResources(war, root, target, true, true);
    }

    public static void warAddAsResroucesRecursive(WebArchive war, File root) {
	warAddResources(war, root, "/", true, true);
    }

    public static void jarAddAsResroucesNonRecursive(JavaArchive jar, File root, String target) {
	jarAddResources(jar, root, target, false, true);
    }

    public static void jarAddAsResroucesNonRecursive(JavaArchive jar, File root) {
	jarAddResources(jar, root, "/", false, true);
    }

    public static enum MavenArtifactType {
	WAR("war"), //
	JAR("jar"), //
	EJB("ejb"), //
	EAR("ear"), //
	;
	private final String mavenPackagingType;

	private MavenArtifactType(String mavenPackagingType) {
	    this.mavenPackagingType = mavenPackagingType;
	}

	public static MavenArtifactType forType(String mavenPackagingType) {
	    for (MavenArtifactType mat : MavenArtifactType.values())
		if (mat.mavenPackagingType.equals(mavenPackagingType))
		    return mat;
	    return null;
	}
    }

    public static class MavenArtifact {
	private final String groupId;
	private final String artifactId;
	private final MavenArtifactType packagingType;

	private MavenArtifact(String groupId, String artifactId, MavenArtifactType packagingType) {
	    this.groupId = groupId;
	    this.artifactId = artifactId;
	    this.packagingType = packagingType;
	}

	// G:A:P:C:V

	public String canonicalForm() {
	    return String.format("%1$s:%2$s:%3$s:?", groupId, artifactId, packagingType.mavenPackagingType);
	}

	public String toString() {
	    return canonicalForm();
	}
    }

    // PRIVATE

    private static void earAddMavenResolvedArtifact(EnterpriseArchive ear, MavenResolvedArtifact mar) {
	MavenCoordinate co = mar.getCoordinate();
	if (co.getType().equals(PackagingType.JAR)) {
	    JavaArchive archive = ShrinkWrap.create(JavaArchive.class, co.getArtifactId() + ".jar");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsLibrary(archive);
	}
	if (co.getType().equals(PackagingType.EJB)) {
	    JavaArchive archive = ShrinkWrap.create(JavaArchive.class, co.getArtifactId() + ".jar");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsModule(archive);
	}
	if (co.getType().equals(PackagingType.WAR)) {
	    WebArchive archive = ShrinkWrap.create(WebArchive.class, co.getArtifactId() + ".war");
	    archive.merge(mar.as(JavaArchive.class));
	    ear.addAsModule(archive);
	}
    }

    private static void jarAddResources(JavaArchive jar, File file, String targetPath, boolean recursive,
	    boolean resourceOrManifest) {
	if (file == null)
	    throw new NullPointerException();
	if (!file.exists() || !file.isDirectory())
	    throw new RuntimeException(String.format("%1$s must be a directory", file));
	String sub = (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
	for (File f : file.listFiles())
	    if (f.isFile()) {
		if (resourceOrManifest)
		    jar.addAsResource(f, sub + f.getName());
		else
		    jar.addAsManifestResource(f, sub + f.getName());
	    } else if (f.isDirectory() && recursive)
		jarAddResources(jar, f, sub + f.getName() + "/", recursive, resourceOrManifest);
    }

    private static void warAddResources(WebArchive war, File file, String targetPath, boolean recursive,
	    boolean resourceOrManifest) {
	if (file == null)
	    throw new NullPointerException();
	if (!file.exists() || !file.isDirectory())
	    throw new RuntimeException(String.format("%1$s must be a directory", file));
	String sub = (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
	for (File f : file.listFiles())
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
