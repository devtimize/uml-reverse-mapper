# UML Reverse Mapper

Automatically generate class diagram from your code.

Using reflection, UML Reverse Mapper scans your packages that contain your code. It then builds a graph of class relations and outputs a [PlantUML](http://www.plantuml.com/) .puml file.

The tool is available as a Maven plugin (urm-maven-plugin).

### Using the Maven plugin

Add to your pom.xml the following:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.devtimize.urm</groupId>
      <artifactId>urm-maven-plugin</artifactId>
      <version>1.0.0</version>
      <configuration>
        <outputDirectory>${project.basedir}/diagram</outputDirectory>
        <scanPackages>
          <param>com.ticketmananger</param>
        </scanPackages>
        <ignorePackages>
          <param>com.ticketmananger.dto</param>
        </ignorePackages>
        <ignoreClasses>
          <param>Main</param>
          <param>UserBuilder</param>
        </ignoreClasses>
        <includeMainDirectory>true</includeMainDirectory>
        <includeTestDirectory>false</includeTestDirectory>
        <showPackageNames>false</showPackageNames>
        <showFields>false</showFields>
        <showConstructors>false</showConstructors>
        <presenter>plantuml</presenter>
      </configuration>
      <executions>
        <execution>
          <phase>process-classes</phase>
          <goals>
            <goal>map</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

- `scanPackages` configuration parameter contains a list of packages that should be included in the class
diagram 
- `ignoreClasses` configuration parameter contains a list of types that should be excluded from the class diagram
- `ignorepackages` configuration parameter contains a list of packages that should be excluded from the class diagram
- `showPackageName ` configuration parameter indicates whether to include package name of the classes in the class diagram
- `showFields ` configuration parameter indicates whether to include fields of a class in the class diagram
- `showMethods ` configuration parameter indicates whether to include methods of a class in the class diagram
- `showConstructors ` configuration parameter indicates whether to include constructors of a class in the class diagram
- `Dependencies` list should contain the artifacts where the classes are found. See https://maven.apache.org/guides/mini/guide-configuring-plugins.html#Using_the_dependencies_Tag
- `includeMainDirectory` configuration parameter indicates to include classes of src/main/java 
directory. Default value of `includeMainDirectory` is true. 
- `includeTestDirectory` configuration parameter indicates to include classes of src/test/java 
directory. Default value of `includeTestDirectory` configuration parameter is false.
- `presenter` parameter control which presenter is used. Currently `plantuml` is only supported

When `process-test-classes` life-cycle phase gets executed, the class diagram will be saved to
 `/target/${project.name}.urm.puml`. Use this file with your local
or online tools to show your class diagram.

### Showcases

Here are some class diagrams generated with the `urm-maven-plugin` and the PlantUML Presenter as well as PlantUML's
free generation/hosting service.

[![Async Method Invocation](http://plantuml.com/plantuml/png/3SlB3G8n303HLg2090TkT6Ey5easjYD_5kYUdERmDFSXEFEWj7dh4SkVhHbywdj4prSw6Qe4ILHKRWnsfhC-Ml8iHXUPKs5OYsoZnmvzWTSaR-0_mS8KNOyov5A462erZUlQ_ny0)](https://github.com/markusmo3/uml-reverse-mapper/blob/master/examples/async-method-invocation.urm.puml)
[![Builder](http://plantuml.com/plantuml/png/3ShB4S8m34NHLg20M0jsTECuRuW7oTRear0-Njt5kSy-6kU1D7wS4Ufl8gjt-VGuSq-7jJa28qgRGbBjcoxpHIcy6IwOOvEg2bleiO9V5MKuxTdvW9KqARh-Fm00)](https://github.com/markusmo3/uml-reverse-mapper/blob/master/examples/builder.urm.puml)
[![Datamapper](http://plantuml.com/plantuml/png/BSkx3SCm34NHLPm1B1Rkl0qZFyH6H8dW9_7uKP7g5WVtSVNQya1QMyu8zPt8-5jULvpvJ8VLqGCzIXr2mlPEbx5HIbiD7vXZ5LQ5JVIOmSsY3Ku71_-jf4dH-Vm0)](https://github.com/markusmo3/uml-reverse-mapper/blob/master/examples/data-mapper.urm.puml)

