# 02 - Your first Quarkus application

[Source code](https://github.com/apedano/kubernetes-native-microservices-sources/tree/main/02-your-first-quarkus-application)

The application is created with https://code.quarkus.io/ and the **RESTEasy Classic** extension.

## Develop with developer joy

By running the maven quarku plugin:

```bash
mvn quarkus:dev
```
Using live coding enables us to **update Java source, resources, and configuration of
a running application**. All changes are reflected in the running application automatically, enabling developers to improve the turnaround time when developing a new application.
 Live coding enables hot deployment via **background compilation**. Any changes to
the Java source, or resources, will be reflected **as soon as the application receives a
new request from the browser**. Refreshing the browser or issuing a new browser
request triggers a scan of the project for any changes to then recompile and redeploy
the application. If any issues arise with compilation or deployment, an error page provides details of the problem.

## Add lombok support

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

## Resteasy client and serialization

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy</artifactId>
</dependency>
<!-- Introduces json serialization in the rest calls   -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
<dependency>
```

## Test the endpoints

```xml
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-junit5</artifactId>
 <scope>test</scope>
</dependency>
<dependency>
 <groupId>io.rest-assured</groupId>
 <artifactId>rest-assured</artifactId>
 <scope>test</scope>
</dependency>
```

The `@QuarkusTest` tests the methods in the `AccountResource` class (including the post action)

[AccountResourceTest.java](https://github.com/apedano/kubernetes-native-microservices-sources/blob/a687bc3b50ca0c809718891fbd8223c2bebd69ae/02-your-first-quarkus-application/src/test/java/org/acme/AccountResourceTest.java)

## Build native (GraalVM)

The `pom.xml` file of the project contains the `native` **profile** that we can use to build the native application:

```xml
<profiles>
    <profile>
      <id>native</id>
```

```bash
mvn clean install -Pnative
mvn clean install -Dquarkus.package.type=native
```
You can build Quarkus native executables in two ways:

* Use a **Container Image of GraalVM**. This option does not require installing GraalVM locally
* Install **GraalVM locally** and use it to build a native executable

### Build with the container image

We need Docker running in the host

```bash
service docker start
```
Then we can build using the **container build** option

```
quarkus build --native -Dquarkus.native.container-build=true
```

Aleternatevely, we can set the property in the `application.properties` file

```properties
quarkus.native.container-build=true
```

The build will 

```bash
[INFO] -------------< org.acme:02-your-first-quarkus-application >-------------
[INFO] Building 02-your-first-quarkus-application 1.0.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- quarkus:3.5.0:build (default-cli) @ 02-your-first-quarkus-application ---
[INFO] [io.quarkus.deployment.pkg.steps.JarResultBuildStep] Building native image source jar: C:\projects\personal\kubernetes-native-microservices-sources\02-your-first-quarkus-application\target\02-your-first-quarkus-application-1.0.0-SNAPSHOT-native-image-source-jar\02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner.jar
```
The container build option will make the build pull the `builder-image` (`ubi-quarkus-mandrel-builder-image:jdk-21`) from the Docker hub to the local Docker instance
```bash
[INFO] [io.quarkus.deployment.pkg.steps.NativeImageBuildContainerRunner] Using docker to run the native image builder
[INFO] [io.quarkus.deployment.pkg.steps.NativeImageBuildContainerRunner] Checking status of builder image 'quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21'
jdk-21: Pulling from quarkus/ubi-quarkus-mandrel-builder-image
01858fc5b538: Pulling fs layer
9584a3317024: Pulling fs layer
...
...
Status: Downloaded newer image for quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21
quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21
```
Once the container is running the build command is sent

```bash
[INFO] [io.quarkus.deployment.pkg.steps.NativeImageBuildStep] Running Quarkus native-image plugin on MANDREL 23.1.1.0 JDK 21.1
[INFO] [io.quarkus.deployment.pkg.steps.NativeImageBuildRunner] docker run --env LANG=C --rm -v /c/projects/personal/kubernetes-native-microservices-sources/02-your-first-quarkus-application/target/02-your-first-quarkus-application-1.0.0-SNAPSHOT-native-image-source-jar:/project:z...
...
========================================================================================================================
GraalVM Native Image: Generating '02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner' (executable)...
========================================================================================================================
For detailed information and explanations on the build output, visit:
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
------------------------------------------------------------------------------------------------------------------------
[1/8] Initializing...                                                                                   (13,8s @ 0,15GB)
 Java version: 21.0.1+12-LTS, vendor version: Mandrel-23.1.1.0-Final
 Graal compiler: optimization level: 2, target machine: x86-64-v3
 C compiler: gcc (redhat, x86_64, 8.5.0)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
 4 user-specific feature(s):
 - com.oracle.svm.thirdparty.gson.GsonFeature
 - io.quarkus.runner.Feature: Auto-generated class by Quarkus from the existing extensions
 - io.quarkus.runtime.graal.DisableLoggingFeature: Disables INFO logging during the analysis phase
 - org.eclipse.angus.activation.nativeimage.AngusActivationFeature
------------------------------------------------------------------------------------------------------------------------
 4 experimental option(s) unlocked:
 - '-H:+AllowFoldMethods' (origin(s): command line)
 - '-H:BuildOutputJSONFile' (origin(s): command line)
 - '-H:-UseServiceLoaderFeature' (origin(s): command line)
 - '-H:ReflectionConfigurationResources' (origin(s): 'META-INF/native-image/io.netty/netty-transport/native-image.properties' in 'file:///project/lib/io.netty.netty-transport-4.1.100.Final.jar')
------------------------------------------------------------------------------------------------------------------------
Build resources:
 - 10,00GB of memory (64,1% of 15,60GB system memory, determined at start)
 - 12 thread(s) (100,0% of 12 available processor(s), determined at start)
[2/8] Performing analysis...
...
---------------------------------------------------------------
Produced artifacts:
 /project/02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner (executable)
 /project/02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner-build-output-stats.json (build_info)
========================================================================================================================
Finished generating '02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner' in 1m 28s.
```
The output contains also an option to run the native image in docker

```bash
[INFO] [io.quarkus.deployment.pkg.steps.NativeImageBuildRunner] docker run --env LANG=C --rm -v /c/projects/personal/kubernetes-native-microservices-sources/02-your-first-quarkus-application/target/02-your-first-quarkus-application-1.0.0-SNAPSHOT-native-image-source-jar:/project:z --entrypoint /bin/bash quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 -c objcopy --strip-debug 02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner
```
We can also run the native app locally from 

```bash
./target/02-your-first-quarkus-application-1.0.0-SNAPSHOT-runner
```


**NOTE**: [GraalVM](https://quarkus.io/guides/building-native-image#configuring-graalvm) must be installed 

## Deploy the native applications to Kubernetes/Openshift

Quakus implement the possibility to **build kubernetes deployment application YAML files automatically**

````xml
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-kubernetes</artifactId>
</dependency>
````
When running ```clean install``` the dependency will generate both json and yaml config files in ```/target/kubernetes```.

### Add Quarkus Openshift extension

[Guide](https://access.redhat.com/documentation/en-us/red_hat_build_of_quarkus/2.2/guide/9f629efb-0765-445e-9c2f-6ff721bd86bd)

There is also an Openshift specific Quarkus extension

````xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-openshift</artifactId>
</dependency>
````

### Build strategies
For security and convenience, OpenShift supports different build strategies that are not available in the upstream Kubernetes distributions.

#### Docker build

This strategy builds the artifacts (**JAR files or a native executable**) outside the OpenShift cluster, either **locally or in a CI environment**, and then **provides them to the OpenShift build system together with a `Dockerfile`**.  So the produced Dockerfile is used in combination wiht the build output to create a new `ImagStream` inside the cluster.

#### Source to Image (S2I)

The build process is performed inside the OpenShift cluster. Using `S2I` to deploy Red Hat build of **Quarkus as a JVM application** is fully supported.

#### Binary S2I

This strategy uses a **JAR file as an input** to the S2I build process, which speeds up the build process and deployment of your application.

|  Build strategy  |  Support for Quarkus tooling  |  Support for JVM  |  Support for Native  |  Support for JVM Serverless  |  Support for native Serverless  |
|------------------|-------------------------------|-------------------|----------------------|------------------------------|---------------------------------|
|  Docker build    |  YES                          |  YES              |  YES                 |  YES                         |  YES                            |
|  S2I Binary      |  YES                          |  YES              |  NO                  |  NO                          |  NO                             |
|  Source S2I      |  NO                           |  YES              |  NO                  |  NO                          |  NO                             |

### Deploy the native application to Openshift

If we use the `quarkus-openshift` extension, you can deploy your application to OpenShift using the **Docker build strategy**. The container is built inside the OpenShift cluster and provided as an image stream.

#### Building using custom `Dockerfile`
The Quarkus project includes pre-generated Dockerfiles with instructions. When you want to use a custom Dockerfile, you need to add the file in the `src/main/docker` directory or anywhere inside the module. Additionally, you need to **set the path to your Dockerfile** using the `quarkus.openshift.jvm-dockerfile` property.

#### Configuration

In the `application.properties`

```properties
quarkus.openshift.build-strategy=docker #sets the docker build strategy
quarkus.kubernetes-client.trust-certs=true #Optional, if an untrusted cert is used
quarkus.openshift.route.expose=true #expose the deployment via a route
quarkus.openshift.native-dockerfile=src/main/docker/Dockerfile.native #used for the ImageStream creation

```
This is the link to the [Dockerfile](https://github.com/apedano/kubernetes-native-microservices-sources/blob/a687bc3b50ca0c809718891fbd8223c2bebd69ae/02-your-first-quarkus-application/src/main/docker/Dockerfile.native)

#### Build deploy execution

```bash
mvn clean package -Dquarkus.kubernetes.deploy=true
```

## Login into the Openshift cluster

do the login action from the local machine with the desired project

## Build and deploy to Openshift the application

Execute the following goal

````mvn clean package -Pnative -Dquarkus.kubernetes.deploy=true````

#### Step 1: Cluster image build (BuildConfig and ImageStream)

The first step is the** native image creation** based on the Mandrel container (docker build)

````shell
Starting (in-cluster) container image build for jar using: DOCKER on server: https://api.sandbox-m4.g2pi.p1.openshiftapps.com:6443/ in namespace:xan80-dev.
...
````

The build config is a Docker build (the option supporting native images).
The content of the Dockerfile is the same as from  ```src/main/docker/Dockerfile.native```. The builds is launched from the local machine (so it has visibility on the target folder containing the built native image)
The generated Openshift resources are


````yaml
kind: BuildConfig
apiVersion: build.openshift.io/v1
metadata:
  annotations:
    app.openshift.io/vcs-url: <<unknown>>
    app.quarkus.io/build-timestamp: '2023-06-24 - 14:16:31 +0000'
    app.quarkus.io/commit-id: 015e167037eec95776b10f99a102aef536aad29f
  name: account-service
  namespace: xan80-dev
  labels:
    app.kubernetes.io/managed-by: quarkus
    app.kubernetes.io/name: account-service
    app.kubernetes.io/version: 1.0.0-SNAPSHOT
    app.openshift.io/runtime: quarkus
spec:
  runPolicy: Serial
  source:
    type: Dockerfile
    dockerfile: "####\r\n# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode.\r\n#\r\n# Before building the container image run:\r\n#\r\n# ./mvnw package -Pnative\r\n#\r\n# Then, build the image with:\r\n#\r\n# docker build -f src/main/docker/Dockerfile.native -t quarkus/account-service .\r\n#\r\n# Then run the container using:\r\n#\r\n# docker run -i --rm -p 8080:8080 quarkus/account-service\r\n#\r\n###\r\nFROM registry.access.redhat.com/ubi8/ubi-minimal:8.6\r\nWORKDIR /work/\r\nRUN chown 1001 /work \\\r\n    && chmod \"g+rwX\" /work \\\r\n    && chown 1001:root /work\r\nCOPY --chown=1001:root target/*-runner /work/application\r\n\r\nEXPOSE 8080\r\nUSER 1001\r\n\r\nCMD [\"./application\", \"-Dquarkus.http.host=0.0.0.0\"]\r\n"
  strategy:
    type: Docker
    dockerStrategy: {}
  output:
    to:
      kind: ImageStreamTag
      name: 'account-service:1.0.0-SNAPSHOT'
  resources: {}
  postCommit: {}
  nodeSelector: null
status:
  lastVersion: 2
````

````yaml
kind: ImageStream
apiVersion: image.openshift.io/v1
metadata:
  annotations:
    app.openshift.io/vcs-url: <<unknown>>
    app.quarkus.io/build-timestamp: '2023-06-24 - 14:16:31 +0000'
    app.quarkus.io/commit-id: 015e167037eec95776b10f99a102aef536aad29f
  name: account-service
  namespace: xan80-dev
  labels:
    app.kubernetes.io/managed-by: quarkus
    app.kubernetes.io/name: account-service
    app.kubernetes.io/version: 1.0.0-SNAPSHOT
    app.openshift.io/runtime: quarkus
spec:
  lookupPolicy:
    local: true
status:
  dockerImageRepository: 'image-registry.openshift-image-registry.svc:5000/xan80-dev/account-service'
  publicDockerImageRepository: >-
    default-route-openshift-image-registry.apps.sandbox-m4.g2pi.p1.openshiftapps.com/xan80-dev/account-service
  tags:
    - tag: 1.0.0-SNAPSHOT
      items:
        - created: '2023-06-24T14:16:54Z'
          dockerImageReference: >-
            image-registry.openshift-image-registry.svc:5000/xan80-dev/account-service@sha256:dc23a1f088233b5a3631ee5e776f4214469e06f42ceed148db59efe92cb9cb22
          image: >-
            sha256:dc23a1f088233b5a3631ee5e776f4214469e06f42ceed148db59efe92cb9cb22
          generation: 1
        - created: '2023-06-24T13:31:52Z'
          dockerImageReference: >-
            image-registry.openshift-image-registry.svc:5000/xan80-dev/account-service@sha256:76ffd5b404e2a3528020d8a02a62c7d56a0bea34f39bba1dd1ccf0d98630909f
          image: >-
            sha256:76ffd5b404e2a3528020d8a02a62c7d56a0bea34f39bba1dd1ccf0d98630909f
          generation: 1
````

After the build in openshift happens, the image stream will be ready to be used in the Openshift application 

### Deploy the application to the cluster

The process will generate the _DeploymentConfig_, the _Service_ and the _Route_ to expose the app endpoints externally. 

````shell

[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Deploying to openshift server: https://api.sandbox-m4.g2pi.p1.openshiftapps.com:6443/ in namespace: xan80-dev.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: Service account-service.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: ImageStream s2i-java.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: ImageStream account-service.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: BuildConfig account-service.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: DeploymentConfig account-service.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: Route account-service.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] The deployed application can be accessed at: http://account-service-xan80-dev.apps.sandbox-m4.g2pi.p1.openshiftapps.com
[INFO] [io.quarkus.deployment.QuarkusAugmentor] Quarkus augmentation completed in 133466ms
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:26 min
[INFO] Finished at: 2023-06-24T16:17:00+02:00
[INFO] ------------------------------------------------------------------------

Process finished with exit code 0

````