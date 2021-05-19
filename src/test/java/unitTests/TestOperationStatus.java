package unitTests;

import it.polito.ezshop.model.OperationStatus;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestOperationStatus {

    @Test
    public void testAffectsBalance () {
        assertTrue(OperationStatus.COMPLETED.affectsBalance());
        assertTrue(OperationStatus.PAID.affectsBalance());
        assertFalse(OperationStatus.CLOSED.affectsBalance());
        assertFalse(OperationStatus.OPEN.affectsBalance());
    }
}
