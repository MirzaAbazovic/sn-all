package de.augustakom.common.tools.lang;

public interface Function2<T, ARG1, ARG2> {
    T apply(ARG1 arg1, ARG2 arg2) throws Exception;
}
