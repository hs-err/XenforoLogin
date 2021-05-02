/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.utils.dependency;

import com.google.common.collect.ImmutableList;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.dependency.classloader.ReflectionClassLoader;
import red.mohist.sodionauth.libs.maven.repository.internal.MavenRepositorySystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyManager {

    private static final ReflectionClassLoader reflectionClassLoader = new ReflectionClassLoader();
    private static final RepositorySystem repositorySystem;
    private static final DefaultRepositorySystemSession repositorySystemSession;
    private static final LocalRepository localRepo;
    private static final List<Relocation> rules = new ImmutableList.Builder<Relocation>()
            .add(new Relocation("org.apache.maven", "red.mohist.sodionauth.libs.maven"))
            .add(new Relocation("org.apache.http", "red.mohist.sodionauth.libs.http"))
            .add(new Relocation("org.apache.commons", "red.mohist.sodionauth.libs.commons"))
            .add(new Relocation("org.objectweb.asm", "red.mohist.sodionauth.libs.asm"))
            .build();

    static {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                Helper.getLogger().warn(String.format("Service creation failed for %s with implementation %s", type, impl), new Exception(exception));
            }
        });

        repositorySystem = locator.getService(RepositorySystem.class);

        repositorySystemSession = MavenRepositorySystemUtils.newSession();
        repositorySystemSession.setTransferListener(ConsoleTransferListener.INSTANCE);
        repositorySystemSession.setRepositoryListener(ConsoleRepositoryListener.INSTANCE);
        String librariesPath = Helper.getConfigPath("libraries");
        new File(librariesPath).mkdirs();
        localRepo = new LocalRepository(librariesPath);
        repositorySystemSession.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(repositorySystemSession, localRepo));
        repositorySystemSession.setSystemProperties(System.getProperties());
        repositorySystemSession.setConfigProperties(System.getProperties());
        repositorySystemSession.setSystemProperty("os.detected.name", System.getProperty("os.name", "unknown").toLowerCase());
        repositorySystemSession.setSystemProperty("os.detected.arch", System.getProperty("os.arch", "unknown").replaceAll("amd64", "x86_64"));
    }

    public static void checkDependencyMaven(String group, String name, String version) {

        File librariesPath = new File(Helper.getConfigPath("libraries"));
        librariesPath.mkdirs();

        Artifact artifact = new DefaultArtifact(group, name, "jar", version);
        DependencyFilter dependencyFilter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
        collectRequest.setRepositories(Collections.singletonList(new RemoteRepository.Builder("central", "default", Config.dependencies.mavenRepository).build()));
        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, dependencyFilter);

        List<ArtifactResult> artifactResults;
        try {
            artifactResults = new LinkedList<>(repositorySystem.resolveDependencies(repositorySystemSession, dependencyRequest).getArtifactResults());
        } catch (DependencyResolutionException e) {
            Helper.getLogger().warn("Error resolving dependencies", e);
            return;
        }

        for (ArtifactResult artifactResult : artifactResults) {
            if (artifactResult.isResolved()) {
                String sourcePath = artifactResult.getArtifact().getFile().toPath().toString();
                String relocatedPath = sourcePath.substring(0, sourcePath.length() - ".jar".length()) + "-relocated.jar";
                File sourceFile = new File(sourcePath);
                File relocatedFile = new File(relocatedPath);
                if (!relocatedFile.exists()) {
                    Helper.getLogger().info("Relocating " +
                            artifactResult.getArtifact().getGroupId()
                            + ":" + artifactResult.getArtifact().getArtifactId()
                            + ":" + artifactResult.getArtifact().getVersion());
                    JarRelocator relocator = new JarRelocator(sourceFile, relocatedFile, rules);
                    try {
                        relocator.run();
                    } catch (Exception e) {
                        relocatedFile.delete();
                        throw new RuntimeException("Unable to relocate dependencies " + sourcePath, e);
                    }
                }
                Helper.getLogger().info("Injecting " +
                        artifactResult.getArtifact().getGroupId()
                        + ":" + artifactResult.getArtifact().getArtifactId()
                        + ":" + artifactResult.getArtifact().getVersion());
                reflectionClassLoader.addJarToClasspath(Paths.get(relocatedPath));
            } else {
                Helper.getLogger().info("Failed " +
                        artifactResult.getArtifact().getGroupId()
                        + ":" + artifactResult.getArtifact().getArtifactId()
                        + ":" + artifactResult.getArtifact().getVersion());
            }
        }
    }

    public static class ConsoleRepositoryListener extends AbstractRepositoryListener {

        public static final ConsoleRepositoryListener INSTANCE = new ConsoleRepositoryListener();

        private ConsoleRepositoryListener() {

        }

        public void artifactDeployed(RepositoryEvent event) {
            // Helper.getLogger().info("Deployed " + event.getArtifact() + " to " + event.getRepository());
        }

        public void artifactDeploying(RepositoryEvent event) {
            // Helper.getLogger().info("Deploying " + event.getArtifact() + " to " + event.getRepository());
        }

        public void artifactDescriptorInvalid(RepositoryEvent event) {
            Helper.getLogger().warn("Invalid artifact descriptor for " + event.getArtifact() + ": "
                    + event.getException().getMessage());
        }

        public void artifactDescriptorMissing(RepositoryEvent event) {
            Helper.getLogger().warn("Missing artifact descriptor for " + event.getArtifact());
        }

        public void artifactInstalled(RepositoryEvent event) {
            // Helper.getLogger().info("Installed " + event.getArtifact() + " to " + event.getFile());
        }

        public void artifactInstalling(RepositoryEvent event) {
            // Helper.getLogger().info("Installing " + event.getArtifact() + " to " + event.getFile());
        }

        public void artifactResolved(RepositoryEvent event) {
            // Helper.getLogger().info("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
        }

        public void artifactDownloading(RepositoryEvent event) {
            Helper.getLogger().info("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
        }

        public void artifactDownloaded(RepositoryEvent event) {
            Helper.getLogger().info("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
        }

        public void artifactResolving(RepositoryEvent event) {
            // Helper.getLogger().info("Resolving artifact " + event.getArtifact());
        }

        public void metadataDeployed(RepositoryEvent event) {
            // Helper.getLogger().info("Deployed " + event.getMetadata() + " to " + event.getRepository());
        }

        public void metadataDeploying(RepositoryEvent event) {
            // Helper.getLogger().info("Deploying " + event.getMetadata() + " to " + event.getRepository());
        }

        public void metadataInstalled(RepositoryEvent event) {
            // Helper.getLogger().info("Installed " + event.getMetadata() + " to " + event.getFile());
        }

        public void metadataInstalling(RepositoryEvent event) {
            // Helper.getLogger().info("Installing " + event.getMetadata() + " to " + event.getFile());
        }

        public void metadataInvalid(RepositoryEvent event) {
            Helper.getLogger().warn("Invalid metadata " + event.getMetadata());
        }

        public void metadataResolved(RepositoryEvent event) {
            Helper.getLogger().warn("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
        }

        public void metadataResolving(RepositoryEvent event) {
            // Helper.getLogger().info("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
        }
    }

    public static class ConsoleTransferListener extends AbstractTransferListener {
        public static final ConsoleTransferListener INSTANCE = new ConsoleTransferListener();

        private final Map<TransferResource, Long> downloads = new ConcurrentHashMap<>();
        private int lastLength;

        private ConsoleTransferListener() {
        }

        @Override
        public void transferInitiated(TransferEvent event) {
            String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";

            Helper.getLogger().info(message + ": " + event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
        }

        @Override
        public void transferProgressed(TransferEvent event) {
            TransferResource resource = event.getResource();
            downloads.put(resource, event.getTransferredBytes());

            StringBuilder buffer = new StringBuilder(64);

            for (Map.Entry<TransferResource, Long> entry : downloads.entrySet()) {
                long total = entry.getKey().getContentLength();
                long complete = entry.getValue();

                buffer.append(getStatus(complete, total)).append("  ");
            }

            int pad = lastLength - buffer.length();
            lastLength = buffer.length();
            pad(buffer, pad);
            buffer.append('\r');

            System.out.print(buffer.toString());
        }

        private String getStatus(long complete, long total) {
            if (total >= 1024) {
                return toKB(complete) + "/" + toKB(total) + " KB ";
            } else if (total >= 0) {
                return complete + "/" + total + " B ";
            } else if (complete >= 1024) {
                return toKB(complete) + " KB ";
            } else {
                return complete + " B ";
            }
        }

        private void pad(StringBuilder buffer, int spaces) {
            String block = "                                        ";
            while (spaces > 0) {
                int n = Math.min(spaces, block.length());
                buffer.append(block, 0, n);
                spaces -= n;
            }
        }

        @Override
        public void transferSucceeded(TransferEvent event) {
            transferCompleted(event);

            TransferResource resource = event.getResource();
            long contentLength = event.getTransferredBytes();
            if (contentLength >= 0) {
                String type = (event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded");
                String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B";

                String throughput = "";
                long duration = System.currentTimeMillis() - resource.getTransferStartTime();
                if (duration > 0) {
                    long bytes = contentLength - resource.getResumeOffset();
                    DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
                    double kbPerSec = (bytes / 1024.0) / (duration / 1000.0);
                    throughput = " at " + format.format(kbPerSec) + " KB/sec";
                }

                Helper.getLogger().info(type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len
                        + throughput + ")");
            }
        }

        @Override
        public void transferFailed(TransferEvent event) {
            transferCompleted(event);

            if (!(event.getException() instanceof MetadataNotFoundException)) {
                Helper.getLogger().warn("Transfer failed", event.getException());
            }
        }

        private void transferCompleted(TransferEvent event) {
            downloads.remove(event.getResource());

            StringBuilder buffer = new StringBuilder(64);
            pad(buffer, lastLength);
            buffer.append('\r');
            System.out.print(buffer.toString());
        }

        public void transferCorrupted(TransferEvent event) {
            Helper.getLogger().warn("Corrupted transfer", event.getException());
        }

        @SuppressWarnings("checkstyle:magicnumber")
        protected long toKB(long bytes) {
            return (bytes + 1023) / 1024;
        }
    }

}