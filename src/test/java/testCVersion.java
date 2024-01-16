import org.junit.jupiter.api.Test;

import static init.init.changeToExpression;

public class testCVersion {
    @Test
    public void testChangeToExpression() {

        assert "[1.1.1,]".equals(changeToExpression("c", ">=1.1.1").getValue());
        assert "(1.0,2.0)".equals(changeToExpression("c", "[>1.0 <2.0]").getValue());
        assert "[1.0,2.0)".equals(changeToExpression("c", "[~=1.0]").getValue());
        assert "1.1.1".equals(changeToExpression("c", "1.1.1").getValue());
        assert "*".equals(changeToExpression("c", "*").getValue());


    }

}