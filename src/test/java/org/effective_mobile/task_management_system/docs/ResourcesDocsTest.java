package org.effective_mobile.task_management_system.docs;

import com.google.common.reflect.ClassPath;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcesDocsTest {
    public static final String packageResources = "org.effective_mobile.task_management_system.resource";
    public static final String packageResourceDocs = "org.effective_mobile.task_management_system.docs";

    private final String resourcesPostfix = "Resource";
    private final String resourceDocsPostfix = resourcesPostfix + "Docs";

    private final List<Class<? extends Annotation>> expectedAnnotationsInDocs =
        List.of(ApiResponses.class, Tag.class);

    private static final String noAnnotationTemplate = "%s#%s has no @%s";
    private static final String noAnnotatedMethodTemplate = "%s has no methods annotated with @%s";
    private static final String noAnnotationFieldValueTemplate = "%s#%s has no @%s with '%s' = '%s'";

    @Test
    public void newEndpointsHasDocumentsTest() throws IOException {
        Set<Class> resources = findAllClassesUsingGoogleGuice(packageResources);
        Set<Class> resourcesDocs = findAllClassesUsingGoogleGuice(packageResourceDocs);

        Pair<List<Pair<Class, Class>>, Set<Class>> zip = zip(
            resources,
            resourcesDocs,
            (resource, resourceDoc) -> areEqualByNameRoots(
                pair(resource, resourcesPostfix),
                pair(resourceDoc, resourceDocsPostfix)
            ),
            Pair::of
        );

        var pairs = zip.getLeft();
        var standalones = zip.getRight();

        Assertions.assertTrue(allDontEndWith(standalones, resourcesPostfix));
        Assertions.assertTrue(allDontEndWith(standalones, resourceDocsPostfix));

        List<String> errorMessages = new ArrayList<>();
        for (Pair<Class, Class> pair : pairs) {
            Class resource = pair.getLeft();
            Class resourceDocs = pair.getRight();

            Assertions.assertTrue(isAncestor(resource, resourceDocs));
            Assertions.assertTrue(hasAllMethods(this::isRequestMapped, resource, resourceDocs));

            List<Method> methodsWithApiResponses = getMethodsWith(ApiResponses.class, resourceDocs, errorMessages);
            for (Method method : methodsWithApiResponses) {
                ApiResponse[] apiResponses = method.getAnnotation(ApiResponses.class).value();
                for (ApiResponse apiResponse : apiResponses) {
                    if (extracted(
                        errorMessages,
                        resourceDocs,
                        ApiResponse.class,
                        ApiResponse::responseCode,
                        "200",
                        "responseCode",
                        method,
                        apiResponse,
                        Objects::equals)) break;
                }
            }
            assertClassHave(expectedAnnotationsInDocs, resourceDocs, errorMessages);

            List<Method> methodsWithTags = getMethodsWith(Tag.class, resourceDocs, errorMessages);
            for (Method method : methodsWithTags) {
                if (extracted(
                    errorMessages,
                    resourceDocs,
                    Tag.class,
                    Tag::name,
                    "<not empty>",
                    "description",
                    method,
                    method.getAnnotation(Tag.class),
                    (expected, actual) -> !actual.isBlank())) break;
            }
        }

        Assertions.assertTrue(errorMessages.isEmpty(), StringUtils.join(errorMessages, "\n"));
    }

    private <A extends Annotation, F> boolean extracted(
        List<String> errorMessages,
        Class resourceDocs,
        Class<A> annotationClass, Function<A, F> fieldGetter,
        F expectedValue,
        String expectedFieldName,
        Method method,
        A annotation,
        BiFunction<F, F, Boolean> condition
    ) {
        if (!condition.apply(expectedValue, fieldGetter.apply(annotation))) {
            errorMessages.add(noAnnotationFieldValueTemplate.formatted(
                resourceDocs.getSimpleName(),
                simpleName(method.getName()),
                annotationClass.getSimpleName(),
                expectedFieldName,
                expectedValue
            ));
        } else {
            return true;
        }
        return false;
    }

    private <A extends Annotation> List<Method> getMethodsWith(
        Class<A> expectedAnnotation,
        Class aClass,
        List<String> errorMessages
    ) {

        List<Method> annotatedMethods = getDeclaredMethods(aClass)
            .filter(it -> it.isAnnotationPresent(expectedAnnotation)).toList();

        if (annotatedMethods.isEmpty()) {
            String classSimpleName = aClass.getSimpleName();
            String annotationSimpleName = simpleName(expectedAnnotation.getName());
            errorMessages.add(
                noAnnotatedMethodTemplate.formatted(classSimpleName, annotationSimpleName)
            );

            return List.of();
        }

        return annotatedMethods
            .stream().filter(it -> it.isAnnotationPresent(expectedAnnotation)).toList();
    }

    private void assertClassHave(
        List<Class<? extends Annotation>> expectedAnnotations,
        Class aClass,
        List<String> errorMessages
    ) {
        List<Method> methods = getDeclaredMethods(aClass).toList();

        for (Class<? extends Annotation> annotation : expectedAnnotations) {
            for (Method method : methods) {
                if (!method.isAnnotationPresent(annotation)) {
                    String message = createHasNoAnnotationMessage(aClass, annotation, method);
                    errorMessages.add(message);
                }
            }
        }
    }

    private String createHasNoAnnotationMessage(Class aClass, Class<? extends Annotation> annotation, Method method) {
        String resourceDocsSimpleName = aClass.getSimpleName();

        return noAnnotationTemplate.formatted(
            resourceDocsSimpleName,
            method.getName(),
            simpleName(annotation.getName())
        );
    }

    private String simpleName(String name) {
        String[] split = name.split("\\.");
        return split[split.length - 1];
    }

    private Pair<Class, String> pair(Class aClass, String postfix) {
        return Pair.of(aClass, postfix);
    }

    private boolean allDontEndWith(Set<Class> standalones, String resourcesPostfix) {
        return standalones.stream().noneMatch(it -> nameEndsWith(it, resourcesPostfix));
    }

    private boolean hasAllMethods(Predicate<Method> condition, Class source, Class target) {
        var sourceFilteredMethods =
            getDeclaredMethods(source)
                .filter(condition)
                .map(Method::getName)
                .collect(Collectors.toSet());

        var targetMethods =
            getDeclaredMethods(target)
                .map(Method::getName)
                .collect(Collectors.toSet());

        return targetMethods.containsAll(sourceFilteredMethods);
    }

    private boolean isAncestor(Class descendant, Class ancestor) {
        return Arrays.stream(descendant.getInterfaces()).toList().contains(ancestor);
    }

    private boolean isRequestMapped(Method method) {
        return
            method.isAnnotationPresent(RequestMapping.class) ||
            method.isAnnotationPresent(GetMapping.class) ||
            method.isAnnotationPresent(PostMapping.class) ||
            method.isAnnotationPresent(PutMapping.class) ||
            method.isAnnotationPresent(DeleteMapping.class) ||
            method.isAnnotationPresent(PatchMapping.class);
    }

    private Stream<Method> getDeclaredMethods(Class resource) {
        return Arrays.stream(resource.getDeclaredMethods());
    }

    private boolean areEqualByNameRoots(
        Pair<Class, String> classAndPostfix,
        Pair<Class, String> anotherClassAndPostfix
    ) {
        return simpleNameWithoutPostfix(classAndPostfix.getLeft(), classAndPostfix.getRight())
            .equals(simpleNameWithoutPostfix(anotherClassAndPostfix.getLeft(), anotherClassAndPostfix.getRight()));
    }

    @NonNull
    private String simpleNameWithoutPostfix(Class aClass, String postfix) {
        return aClass.getSimpleName().replace(postfix, "");
    }

    private <E, T> Pair<List<T>, Set<E>> zip(
        Set<E> es,
        Set<E> others,
        BiFunction <E, E, Boolean> conditionToCombining,
        BiFunction <E, E, T> combiner
    ) {

        ArrayList<T> pairedByCondition = new ArrayList<>();
        ArrayList<E> copiedEs = new ArrayList<>(es);
        ArrayList<E> copiedOthers = new ArrayList<>(others);

        for (E e : es) {
            for (E other : others) {
                if (conditionToCombining.apply(e, other)) {
                    pairedByCondition.add(combiner.apply(e, other));
                    copiedEs.remove(e);
                    copiedOthers.remove(other);
                    break;
                }
            }
        }

        HashSet<E> withoutPair = new HashSet<>();
        withoutPair.addAll(copiedEs);
        withoutPair.addAll(copiedOthers);
        return Pair.of(pairedByCondition, withoutPair);
    }

    private boolean nameEndsWith(Class it, String end) {
        return it.getSimpleName().endsWith(end);
    }

    public Set<Class> findAllClassesUsingGoogleGuice(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
            .getAllClasses()
            .stream()
            .filter(clazz -> clazz.getPackageName().equalsIgnoreCase(packageName))
            .map(ClassPath.ClassInfo::load)
            .collect(Collectors.toSet());
    }

}
