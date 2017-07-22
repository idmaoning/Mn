package com.mn.dao;

import com.mn.comm.Fixed;
import com.mn.comm.PageList;
import com.mn.domain.BaseEntity;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by mn on 2017/7/22 0022.
 */
public class BaseDaoAdapter<T extends BaseEntity> implements BaseDao<T> {

    protected MongoTemplate mongoTemplate;
    protected Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    protected String tabName = entityClass.getSimpleName();

    public BaseDaoAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClass, tabName);
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClass, tabName);
    }

    @Override
    public PageList<T> find(Query query, Pageable pageable) {
        PageList<T> pageList = new PageList<>();
        if (pageable != null) {
            long totalCount = count(query);
            int pageCount = (int) (totalCount / pageable.getPageSize());
            if (totalCount % pageable.getPageSize() != 0) {
                pageCount += 1;
            }
            query.with(pageable);
            pageList.makePageList(null, pageable.getPageSize(), totalCount, pageable.getPageNumber(), pageCount);
        }
        pageList.setPage(mongoTemplate.find(query, entityClass, tabName));
        return pageList;
    }

    /**
     * 通过反射将对象的值设置到update中
     * @param obj
     * @param cur_class
     * @param update
     */
    private void setClassFieldToUpdate(Object obj, Class cur_class, Update update) {
        Field[] obj_fields = cur_class.getDeclaredFields();
        try {
            for (Field field : obj_fields) {
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                boolean isFixed = false;
                Annotation annotations[] = field.getAnnotations();
                for (Annotation annotation:annotations){
                    if(annotation.getClass()== Fixed.class){
                        isFixed = true;
                    }
                }
                if(isFixed){//如果字段是不变的则不添加在update中
                    continue;
                }
                update.set(field.getName(), field.get(obj));
            }
            if (cur_class.getSuperclass() != null) {
                setClassFieldToUpdate(obj, cur_class.getSuperclass(), update);//递归获取父类的字段值
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int updateAll(T t) {
        Update update = new Update();
        setClassFieldToUpdate(t,entityClass,update);
        return mongoTemplate.updateFirst(Query.query(Criteria.where(BaseEntity._ID).is(t.get_id())),update,entityClass,tabName).getN();
    }

    @Override
    public int update(Query query, Update update) {
        return mongoTemplate.updateFirst(query,update,entityClass,tabName).getN();
    }

    @Override
    public int updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query,update,entityClass,tabName).getN();
    }

    @Override
    public T findAndModify(Query query, Update update, boolean isNew) {
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(isNew);
        findAndModifyOptions.upsert(true);
        return mongoTemplate.findAndModify(query,update,findAndModifyOptions,entityClass,tabName);
    }

    @Override
    public void insert(T t) {
        mongoTemplate.insert(t,tabName);
    }

    @Override
    public int delete(Query query) {
       return mongoTemplate.remove(query,entityClass,tabName).getN();
    }

    @Override
    public void insert(List<T> tList) {
        mongoTemplate.insertAll(tList);
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query,entityClass,tabName);
    }

    @Override
    public boolean isExist(Query query) {
        return mongoTemplate.exists(query,entityClass,tabName);
    }
}
