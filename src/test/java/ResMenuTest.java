import com.mn.dao.IResMenuDao;
import com.mn.domain.ResMenu;
import com.mn.utils.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 2017/7/22 0022.
 */
public class ResMenuTest extends AbstractSpringWithJunitTestRunner {

    @Autowired
    IResMenuDao resMenuDao;

    @Test
    public void update() {
        ResMenu resMenu = new ResMenu();
        resMenu.setName("测试");
        resMenu.setPid("");
        resMenu.setUrl("www.baidu.com");
        resMenu.set_id(StringUtils.getId());
        resMenuDao.insert(resMenu);
        List<ResMenu> resMenuList = resMenuDao.find(null);
        System.out.println();
    }
}
