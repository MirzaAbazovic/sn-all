/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2010 11:42:58
 */
package de.augustakom.common.tools.lang;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class EitherTest extends BaseTest {

    public void equalsTest() {
        Either<Integer, String> leftOne = Either.left(new Integer(1));
        Either<Integer, String> leftTwo = Either.left(new Integer(1));
        Either<Integer, Integer> leftDiffType = Either.left(new Integer(1));
        Either<Integer, String> leftThree = Either.left(new Integer(3));
        Either<Integer, String> rightOne = Either.right("Test");
        Either<Integer, String> rightTwo = Either.right(new String("Test"));
        Either<Integer, String> rightThree = Either.right(new String("Kein Test"));

        assertTrue(leftOne.equals(leftTwo));
        assertTrue(rightOne.equals(rightTwo));
        assertFalse(rightOne.equals(leftOne));
        assertFalse(leftOne.equals(rightOne));
        assertFalse(leftOne.equals(leftThree));
        assertFalse(rightThree.equals(rightOne));

        // Note that Left(4) is equal to Left(4) independent of the right generic Type!
        assertTrue(leftOne.equals(leftDiffType));
    }
}
