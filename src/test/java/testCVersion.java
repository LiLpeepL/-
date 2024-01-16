import misc.VersionSortEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static init.init.changeToExpression;
import static init.init.getBaseVersion;

public class testCVersion {
    @Test
    public void testChangeToExpression() {

        System.out.println(changeToExpression("c","< > 3.2/.22 >3.1..2asAsdsdBdC3.1.dD.2 >3.2..2sdsad <3.2.2sddsds <4.23.3.sdds").getValue());



    }

}