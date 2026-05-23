package com.zoopick.server.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.zoopick.server.annotation.ServiceOnly;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ServiceOnlyArchitectureTest {
    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.zoopick.server");

    @Test
    void serviceOnlyMethodsShouldOnlyBeCalledByAllowedServices() {
        importedClasses.stream()
                .map(JavaClass::reflect)
                .flatMap(javaClass -> Arrays.stream(javaClass.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(ServiceOnly.class))
                .forEach(this::assertAllowedCallersOnly);
    }

    private void assertAllowedCallersOnly(Method method) {
        ServiceOnly serviceOnly = method.getAnnotation(ServiceOnly.class);
        List<String> allowedCallerNames = Arrays.stream(serviceOnly.value())
                .map(Class::getName)
                .toList();

        ArchRule rule = noClasses()
                .that(new DescribedPredicate<>("are not allowed callers") {
                    @Override
                    public boolean test(JavaClass input) {
                        return !allowedCallerNames.contains(input.getName());
                    }
                })
                .should().callMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes());

        rule.check(importedClasses);
    }
}
