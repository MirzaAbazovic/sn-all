package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;
import javax.validation.*;
import javax.validation.ConstraintValidatorContext.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.validators.RufnummernBlockValid.RufnummernBlockValidator;

@Test(groups = UNIT)
public class RufnummernBlockValidatorTest {

    @Mock
    ConstraintValidatorContext context;

    @Mock
    ConstraintViolationBuilder constraintViolationBuilder;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
    }

    @DataProvider
    public Object[][] testCases() {
        return new Object[][] {
                { null, true },
                { Collections.emptyList(), false },
                { tooManyBlocks(), false },
                { singletonList(new RufnummernBlock()), false },
                { singletonList(differentLengths()), false },
                { singletonList(bisEqualsVon()), false },
                { singletonList(vonGreaterThanBis()), false },
                { singletonList(invalidVon()), false },
                { singletonList(invalidBis()), false },
                { equalBlocks(), false },
                { overlappingBlocks(), false },

                { singletonList(validBlock()), true },

        };
    }

    @Test(dataProvider = "testCases")
    public void test(List<RufnummernBlock> block, boolean expected) {
        assertThat(new RufnummernBlockValidator().isValid(block, context), equalTo(expected));
    }

    private RufnummernBlock validBlock() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("100");
        block.setBis("499");
        return block;
    }

    private RufnummernBlock differentLengths() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("1000");
        block.setBis("499");
        return block;
    }

    private RufnummernBlock bisEqualsVon() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("400");
        block.setBis("400");
        return block;
    }

    private RufnummernBlock vonGreaterThanBis() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("400");
        block.setBis("099");
        return block;
    }

    private RufnummernBlock invalidVon() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("123");
        block.setBis("099");
        return block;
    }

    private RufnummernBlock invalidBis() {
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("100");
        block.setBis("023");
        return block;
    }

    private List<RufnummernBlock> equalBlocks() {
        List<RufnummernBlock> res = new ArrayList<RufnummernBlock>();
        for (int i = 0; i <= 2; i++) {
            res.add(validBlock());
        }
        return res;
    }

    private List<RufnummernBlock> overlappingBlocks() {
        List<RufnummernBlock> res = new ArrayList<RufnummernBlock>();
        RufnummernBlock block = new RufnummernBlock();
        block.setVon("100");
        block.setBis("299");
        res.add(block);

        block = new RufnummernBlock();
        block.setVon("200");
        block.setBis("399");
        res.add(block);

        return res;
    }


    private List<RufnummernBlock> tooManyBlocks() {
        List<RufnummernBlock> res = new ArrayList<RufnummernBlock>();
        for (int i = 0; i <= 6; i++) {
            res.add(validBlock());
        }
        return res;
    }

}
