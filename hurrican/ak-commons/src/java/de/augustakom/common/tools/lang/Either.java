/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2010 14:46:47
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.util.*;


/**
 * The Either type represents a value of one of two possible types (a disjoint union). The factory methods left and
 * right represent the two possible values. Via isLeft or isRight one can check if the instance is the Left or Right.
 * Calling the corresponding method getLeft/getRight yields the content, calling the opposing one throws a
 * NoSuchElementException
 *
 *
 */
public abstract class Either<S, T> implements Serializable {

    public static <S, T> Either<S, T> left(S left) {
        return new Left<>(left);
    }

    public static <S, T> Either<S, T> right(T right) {
        return new Right<>(right);
    }

    private Either() {
    }

    abstract public S getLeft();

    abstract public T getRight();

    abstract public boolean isRight();

    abstract public boolean isLeft();

    private static final class Left<S, T> extends Either<S, T> {

        final private S left;

        private Left(S left) {
            this.left = left;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#getLeft()
         */
        @Override
        public S getLeft() {
            return left;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#getRight()
         */
        @Override
        public T getRight() {
            throw new NoSuchElementException("Either.getRight on Left");
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#isLeft()
         */
        @Override
        public boolean isLeft() {
            return true;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#isRight()
         */
        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((left == null) ? 0 : left.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Left<?, ?> other = (Left<?, ?>) obj;
            if (left == null) {
                if (other.left != null) {
                    return false;
                }
            }
            else if (!left.equals(other.left)) {
                return false;
            }
            return true;
        }
    }

    private static final class Right<S, T> extends Either<S, T> {

        final private T right;

        private Right(T right) {
            this.right = right;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#getLeft()
         */
        @Override
        public S getLeft() {
            throw new NoSuchElementException("Either.getLeft on Right");
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#getRight()
         */
        @Override
        public T getRight() {
            return right;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#isLeft()
         */
        @Override
        public boolean isLeft() {
            return false;
        }

        /**
         * @see de.augustakom.common.tools.lang.Either#isRight()
         */
        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 17;
            int result = 1;
            result = prime * result + ((right == null) ? 0 : right.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof Right<?, ?>)) {
                return false;
            }
            Right<?, ?> other = (Right<?, ?>) obj;
            if (right == null) {
                if (other.right != null) {
                    return false;
                }
            }
            else if (!right.equals(other.right)) {
                return false;
            }
            return true;
        }


    }
}
