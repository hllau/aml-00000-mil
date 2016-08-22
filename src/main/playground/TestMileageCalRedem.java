import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalParam;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalRedem;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalReturn;
import com.asiamiles.ixClsMileageCal.beans.calculator.MileageCalSector;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by cheng on 8/20/2016.
 */
public class TestMileageCalRedem
{
    @Test
    public void testCal(){
        MileageCalParam mcParam;
        ArrayList sectors = new ArrayList();
        sectors.add(new MileageCalSector("HKG", "HND", "CPA", null));
//        sectors.add(new MileageCalSector("HVB", "MBH", "QFA", null));

        mcParam = new MileageCalParam(1, "OWEC", sectors, "-1");
        MileageCalRedem mcr = new MileageCalRedem();
        MileageCalReturn mcReturn = (MileageCalReturn) mcr.redemMileage(mcParam);
        System.out.println(mcReturn);

    }
}
