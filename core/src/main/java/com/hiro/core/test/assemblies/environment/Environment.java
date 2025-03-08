package com.hiro.core.test.assemblies.environment;

import com.hiro.core.test.assemblies.subject.Subject;

import java.util.*;

/**
 * Environment is also a subject, but contains other subjects.
 * 1. Define methods to control subjects in it.
 */
public abstract class Environment extends Subject {

    /**
     * Environ contains different subjects
     */
    protected final Map<Class<?>, Set<Subject>> subjects = new HashMap<>();

    /**
     * Add a new subject
     * @param subject to be added
     * @param clazz classification of subjects
     * @return this
     * @param <T> class to classification
     */
    public <T extends Subject> Environment addSubject(T subject, Class<T> clazz) {
        subjects.computeIfAbsent(clazz, c -> new HashSet<>()).add(subject);
        return this;
    }

    /**
     * get subjects of a classification
     * @param clazz classification of subjects
     * @return Set
     * @param <T> class to classification
     */
    @SuppressWarnings("unchecked")
    public <T extends Subject> Set<T> getSubjects(Class<T> clazz) {
        return (Set<T>) subjects.getOrDefault(clazz, Collections.emptySet());
    }

    /**
     * remove particular subject in pointed classification
     * @param clazz classification of subjects
     * @param subject to be removed
     * @param <T> class to classification
     */
    public <T extends Subject> void removeSubject(Class<T> clazz, T subject) {
        Set<Subject> subjectSet = subjects.get(clazz);
        if (subjectSet != null) {
            subjectSet.remove(subject);
            if (subjectSet.isEmpty()) {
                subjects.remove(clazz);
            }
        }
    }

    /**
     * remove all classification
     * @param clazz classification of subjects
     * @param <T> class to classification
     */
    public <T extends Subject> void removeSubjects(Class<T> clazz) {
        subjects.remove(clazz);
    }

}
