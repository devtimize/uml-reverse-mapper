package org.devtimize.urm.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Configuration {
    @Singular
    List<String> scanPackages;
    @Singular
    List<String> ignoreClasses;
    @Singular
    List<String> ignorePackages;
    boolean showPackageNames;
    boolean showFields;
    boolean showMethods;
    boolean showConstructors;
}
