package com.mn.dao.impl;

import com.mn.dao.BaseDaoAdapter;
import com.mn.dao.IResMenuDao;
import com.mn.domain.ResMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/22 0022.
 */

@Repository
public class ResMenuDaoImpl extends BaseDaoAdapter<ResMenu> implements IResMenuDao {

    @Autowired
    public ResMenuDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
