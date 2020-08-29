package com.zsc.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsc.blog.Utils.RedisUtil;
import com.zsc.blog.entity.TStatistic;
import com.zsc.blog.entity.TUser;
import com.zsc.blog.mapper.TCollectMapper;
import com.zsc.blog.mapper.TCommentMapper;
import com.zsc.blog.mapper.TStatisticMapper;
import com.zsc.blog.mapper.TUserMapper;
import com.zsc.blog.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mff
 * @since 2020-07-26
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {
    @Autowired
    TUserMapper tUserMapper;
    @Autowired
    TCommentMapper tCommentMapper;
    @Autowired
    TCollectMapper tCollectMapper;
    @Autowired
    TStatisticMapper tStatisticMapper;
    @Resource
    RedisUtil redisUtil;
    //查询所有用户
    @Override
    public Collection<?> selectList(Object o) {
        return tUserMapper.selectList(null);
    }
    //根据名字查询
    @Override
    public TUser selectByusername(String username) {
//        QueryWrapper<TUser> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(TUser::getUsername,username);
        return tUserMapper.selectbyname(username);
    }
    //根据id查询
    @Override
    public TUser selectById(String userId) {
        return tUserMapper.selectById(userId);
    }
    //插入用户
    @Override
    public void insert_user(TUser tUser)
    {
        redisUtil.set(tUser.getUsername(),tUser);
        tUserMapper.insert(tUser);
    }

    @Override
    public void updata_I(TUser tUser) {
        tUserMapper.updateById(tUser);
    }

    @Override
    public int find_usercount(String username) {
      int count=  tUserMapper.selectCount(new QueryWrapper<TUser>().eq("username", username));
        return count;
    }

    @Override
    public int queryUserNumber(){
        return tUserMapper.queryCount();
    }

    @Override
    public void deleteUserWithId(int id) {
        TUser tUser = tUserMapper.selectById(id);
        String username = tUser.getUsername();
        redisUtil.del(username);
        redisUtil.removeAll("comment");
        redisUtil.removeAll("userPage_");
        tUserMapper.deleteUser(id);
        tCollectMapper.deleteColletWithUid(id);
        tCommentMapper.deleteCommentWithUser(username);
        tStatisticMapper.updateStatisticComment();
    }

    @Override
    public List<Map<String, Object>> adminSelectUser(int st, int en, int num, int pageSize) {
        List<Map<String, Object>> resultList;
        if (redisUtil.get("userPage_"+num+"pageSize_"+pageSize)==null) {
            resultList=tUserMapper.selectUser(st, en);
            redisUtil.set("userPage_"+num+"pageSize_"+pageSize,resultList, 30);
        }
        else {
            resultList =(List<Map<String, Object>>)redisUtil.get("userPage_"+num+"pageSize_"+pageSize);
        }
        return resultList;
    }
}
