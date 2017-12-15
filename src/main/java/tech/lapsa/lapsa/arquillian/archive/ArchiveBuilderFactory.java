package tech.lapsa.lapsa.arquillian.archive;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

import tech.lapsa.java.commons.logging.MyLogger;

public final class ArchiveBuilderFactory {
    private ArchiveBuilderFactory() {
    }

    private final static MyLogger logger = MyLogger.newBuilder().withNameOf(ArchiveBuilderFactory.class).build();

    public static EarBuilder newEarBuilder() {
	return new EarBuilder();
    }

    public static class EarBuilder {
	private final List<Jar> jarModules = new ArrayList<>();
	private final List<EjbJar> ejbJarModules = new ArrayList<>();
	private final List<War> warModules = new ArrayList<>();

	private boolean usingRuntimeDependencies = false;

	private EarBuilder() {
	}

	public EarBuilder withRuntimeDependencies() {
	    usingRuntimeDependencies = true;
	    return this;
	}

	public EarBuilder withModule(final Jar jar) {
	    jarModules.add(jar);
	    return this;
	}

	public EarBuilder withModule(final EjbJar ejbJar) {
	    ejbJarModules.add(ejbJar);
	    return this;
	}

	public EarBuilder withModule(final War war) {
	    warModules.add(war);
	    return this;
	}

	public Ear build() {
	    final EnterpriseArchive archive = ShrinkWrap.create(EnterpriseArchive.class);
	    jarModules.stream() //
		    .map(Jar::asJavaArchive)
		    .forEach(archive::addAsModule);
	    warModules.stream() //
		    .map(War::asWebArchive)
		    .forEach(archive::addAsModule);
	    ejbJarModules.stream() //
		    .map(EjbJar::asJavaArchive)
		    .forEach(archive::addAsModule);
	    if (usingRuntimeDependencies)
		earAddRuntimeDependencies(archive);
	    return new Ear(archive);
	}
    }

    public static JarBuilder newJarBuilder() {
	return new JarBuilder();
    }

    public static class JarBuilder {

	private static final String TEST_MANIFEST_FOLDER = "target/test-classes/META-INF";
	private static final String MANIFEST_FOLDER = "target/classes/META-INF";

	private boolean usingManifestFolder = false;
	private boolean usingTestManifestFolder = false;

	private final List<Package> packages = new ArrayList<>();
	private final List<Class<?>> classes = new ArrayList<>();
	private final List<Resource> resources = new ArrayList<>();

	private JarBuilder() {
	}

	public JarBuilder withPackageOf(final Class<?>... clazz) {
	    Arrays.stream(clazz).map(Class::getPackage).forEach(packages::add);
	    return this;
	}

	public JarBuilder withClass(final Class<?>... clazz) {
	    classes.addAll(Arrays.asList(clazz));
	    return this;
	}

	public JarBuilder withPackage(final Package... pack) {
	    packages.addAll(Arrays.asList(pack));
	    return this;
	}

	public JarBuilder withManifestFolder() {
	    usingManifestFolder = true;
	    return this;
	}

	public JarBuilder withTestManifestFolder() {
	    usingTestManifestFolder = true;
	    return this;
	}

	private static class Resource {
	    final File root;
	    final String target;
	    public final boolean recursive;

	    private Resource(final File root, final String target) {
		this.root = root;
		this.target = target;
		recursive = true;
	    }
	}

	public JarBuilder withResource(final File root, final String target) {
	    resources.add(new Resource(root, target));
	    return this;
	}

	protected <T extends Jar> T build(final Function<JavaArchive, T> supplier) {
	    final JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

	    packages.stream() //
		    .forEach(x -> archive.addPackages(true, x));

	    classes.stream() //
		    .forEach(archive::addClass);

	    resources.stream() //
		    .forEach(x -> addResources(archive, x.root, x.target, x.recursive, true));

	    if (usingManifestFolder)
		addResources(archive, new File(MANIFEST_FOLDER), "/", true, false);

	    if (usingTestManifestFolder)
		addResources(archive, new File(TEST_MANIFEST_FOLDER), "/", true, false);

	    return supplier.apply(archive);
	}

	public Jar build() {
	    return build(Jar::new);
	}
    }

    public static EjbJarBuilder newEjbBuilder() {
	return new EjbJarBuilder();
    }

    public static class EjbJarBuilder extends JarBuilder {
	private EjbJarBuilder() {
	    withManifestFolder();
	}

	@Override
	public EjbJarBuilder withPackageOf(final Class<?>... clazz) {
	    super.withPackageOf(clazz);
	    return this;
	}

	@Override
	public EjbJarBuilder withPackage(final Package... pack) {
	    super.withPackage(pack);
	    return this;
	}

	@Override
	public JarBuilder withClass(final Class<?>... clazz) {
	    super.withClass(clazz);
	    return this;
	}

	@Override
	public EjbJarBuilder withManifestFolder() {
	    super.withManifestFolder();
	    return this;
	}

	@Override
	public EjbJarBuilder withTestManifestFolder() {
	    super.withTestManifestFolder();
	    return this;
	}

	@Override
	public EjbJarBuilder withResource(final File root, final String target) {
	    super.withResource(root, target);
	    return this;
	}

	@Override
	public EjbJar build() {
	    return build(EjbJar::new);
	}
    }

    private static void addResources(final JavaArchive archive, final File file, final String targetPath,
	    final boolean recursive,
	    final boolean resourceOrManifest) {
	if (file == null)
	    throw new NullPointerException();

	if (!(file.exists() && file.isDirectory())) {
	    logger.WARN.log("Resource '%1$s' is not a directory. Skipped.", file);
	    return;
	}

	final String sub = (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
	for (final File f : file.listFiles())
	    if (f.isFile()) {
		if (resourceOrManifest)
		    archive.addAsResource(f, sub + f.getName());
		else
		    archive.addAsManifestResource(f, sub + f.getName());
	    } else if (f.isDirectory() && recursive)
		addResources(archive, f, sub + f.getName() + "/", recursive, resourceOrManifest);
    }

    private static PomEquippedResolveStage pomResolveStage;

    private static void initPomResolveStage() {
	if (pomResolveStage == null)
	    pomResolveStage = Maven
		    .resolver()
		    .loadPomFromFile("pom.xml");
    }

    private static void earAddRuntimeDependencies(final EnterpriseArchive ear) {
	initPomResolveStage();
	final MavenResolvedArtifact[] mars = pomResolveStage
		.importCompileAndRuntimeDependencies()
		.resolve()
		.withTransitivity()
		.asResolvedArtifact();
	for (final MavenResolvedArtifact mar : mars)
	    earAddMavenResolvedArtifact(ear, mar);
    }

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

}
